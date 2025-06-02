from flask import Blueprint, request, jsonify, current_app
import hashlib
from db import get_db
from ..protected_record import handle_concert_creation
import json
from ..protected_fcm import send_push_notification

experimental_bp = Blueprint('experimental', __name__)

@experimental_bp.route('/experimental/ticket/validate', methods=['POST'])
def experimental_validate_ticket():

    data = request.get_json()
    required_fields = ['record_company_id', 'ticket_id', 'concert_id', 'user_id']
    if not all(field in data for field in required_fields):
        return jsonify({'message': 'Missing required fields'}), 400

    record_company_id = data['record_company_id']
    ticket_id = data['ticket_id']
    concert_id = data['concert_id']
    user_id = data['user_id']

    conn = get_db()
    cursor = conn.cursor(dictionary=True)

    try:
        cursor.execute("""
            SELECT id FROM concert 
            WHERE id = %s AND record_company_id = %s
        """, (concert_id, record_company_id))
        concert = cursor.fetchone()
        if not concert:
            return jsonify({'message': 'Concert does not belong to this record company'}), 403

        cursor.execute("""
            SELECT id, validated FROM ticket 
            WHERE id = %s AND user_id = %s AND concert_id = %s
        """, (ticket_id, user_id, concert_id))
        ticket = cursor.fetchone()
        if not ticket:
            return jsonify({'message': 'Ticket not found or does not belong to the specified user'}), 404
        if ticket['validated']:
            return jsonify({'message': 'Ticket already validated'}), 403

        cursor.execute("""
            UPDATE ticket 
            SET validated = 1 
            WHERE id = %s
        """, (ticket_id,))
        conn.commit()

        return jsonify({'message': 'Ticket validated successfully'}), 200

    except Exception as e:
        current_app.logger.error(f"Error validating ticket: {str(e)}")
        return jsonify({'message': f'Server error: {str(e)}'}), 500
    finally:
        conn.close()

@experimental_bp.route('/experimental/record_companies', methods=['GET'])
def experimental_get_record_companies():
    conn = get_db()
    cursor = conn.cursor(dictionary=True)

    try:
        cursor.execute("SELECT id, email FROM record_company")
        record_companies = cursor.fetchall()
        return jsonify(record_companies), 200
    except Exception as e:
        current_app.logger.error(f"Error fetching record companies: {str(e)}")
        return jsonify({'message': f'Server error: {str(e)}'}), 500
    finally:
        conn.close()

@experimental_bp.route('/experimental/concert/create', methods=['POST'])
def experimental_create_concert():
    
    try:
        data = request.form
        record_company_id = data.get('record_company_id')
        title = data.get('title')
        date = data.get('date')
        time = data.get('time')
        artist_ids = request.form.getlist('artist_ids') 
        place_id = int(data.get('place_id'))
        tour_id = data.get('tour_id', None)
        tour_id = int(tour_id) if tour_id else None

        sector_prices = json.loads(data.get('sector_prices', '{}'))
        artist_ids = [int(aid) for aid in artist_ids]

        image_file = request.files.get('image')

        success, result = handle_concert_creation(
            record_company_id, title, image_file, date, time, artist_ids, place_id, sector_prices, tour_id
        )

        if not success:
            return jsonify({'message': result}), 400

        conn = get_db()
        cursor = conn.cursor(dictionary=True)

        placeholders = ','.join(['%s'] * len(artist_ids))
        cursor.execute(f"""
            SELECT DISTINCT u.id AS user_id
            FROM user u
            JOIN likes l ON l.user_id = u.id
            WHERE l.artist_id IN ({placeholders})
        """, tuple(artist_ids))
        users_to_notify = cursor.fetchall()

        for user in users_to_notify:
            cursor.execute("""
                INSERT INTO notification (title, description, user_id)
                VALUES (%s, %s, %s)
            """, ("New Concert!", f"A new concert has been created that might interest you: {title}", user['user_id']))

            send_push_notification(
                user['user_id'], True, "New Concert!", f"A new concert has been created that might interest you: {title}"
            )

        conn.commit()
        conn.close()

        return jsonify(result), 201
    except Exception as e:
        current_app.logger.error(f"API concert creation error: {str(e)}")
        return jsonify({'error': str(e)}), 500
    

@experimental_bp.route('/experimental/record_company/artists', methods=['POST'])
def experimental_get_artists():
    try:
        data = request.form
        record_company_id = data.get('record_company_id')

        conn = get_db()
        cursor = conn.cursor(dictionary=True)

        cursor.execute("""
            SELECT 
                a.id AS artist_id,
                a.name AS artist_name,
                a.image AS artist_image
            FROM 
                artist a
            WHERE 
                a.record_company_id = %s
        """, (record_company_id,))

        artists = cursor.fetchall()
        conn.close()

        return jsonify(artists), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500
    
@experimental_bp.route('/experimental/places_for_creation', methods=['POST'])
def experimental_get_places_for_creation():
    data = request.get_json()

    if not data or 'date' not in data:
        return jsonify({'error': 'Missing "date" in request body'}), 400

    concert_date = data['date']

    try:
        conn = get_db()
        cursor = conn.cursor(dictionary=True)

        cursor.execute("""
            SELECT 
                p.id AS place_id,
                p.name AS place_name,
                p.address AS place_address,
                s.id AS sector_id,
                s.name AS sector_name
            FROM place p
            LEFT JOIN sector s ON p.id = s.place_id AND s.is_stage = FALSE
            WHERE p.id NOT IN (
                SELECT DISTINCT place_id
                FROM concert
                WHERE date = %s
            )
            ORDER BY p.id
        """, (concert_date,))

        results = cursor.fetchall()
        conn.close()

        places_dict = {}
        for row in results:
            place_id = row['place_id']
            if place_id not in places_dict:
                places_dict[place_id] = {
                    'id': place_id,
                    'name': row['place_name'],
                    'address': row['place_address'],
                    'sectors': []
                }
            if row['sector_id'] is not None:
                places_dict[place_id]['sectors'].append({
                    'id': row['sector_id'],
                    'name': row['sector_name']
                })

        return jsonify(list(places_dict.values())), 200

    except Exception as e:
        current_app.logger.error(f"Error fetching available places: {str(e)}")
        return jsonify({'error': str(e)}), 500

@experimental_bp.route('/experimental/place/is_free/<place_id>', methods=['GET'])
def experimental_is_place_free(place_id):
    try:
        conn = get_db()
        cursor = conn.cursor(dictionary=True)

        cursor.execute("""
            SELECT COUNT(*) AS count
            FROM concert
            WHERE place_id = %s
        """, (place_id,))
        result = cursor.fetchone()

        if result['count'] > 0:
            return jsonify({'is_free': False}), 200
        else:
            return jsonify({'is_free': True}), 200

    except Exception as e:
        current_app.logger.error(f"Error checking place availability: {str(e)}")
        return jsonify({'error': str(e)}), 500
    finally:
        conn.close()