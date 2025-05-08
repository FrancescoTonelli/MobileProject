from flask import Blueprint, request, jsonify, current_app
from db import get_db
import bcrypt
import jwt
import datetime
from config import SECRET_FOR_TOKEN
import math
import json
import os
from werkzeug.utils import secure_filename
from PIL import Image

protected_record_bp = Blueprint('protected_record', __name__)

def create_token(company_id):
    token = jwt.encode({
        'company_id': company_id
    }, SECRET_FOR_TOKEN, algorithm='HS256')

    conn = get_db()
    cursor = conn.cursor(dictionary=True)
    cursor.execute("UPDATE RECORD_COMPANY SET session_token = %s WHERE id = %s", (token, company_id))
    conn.commit()
    conn.close()

    return token

def verify_token():
    token = request.headers.get('Authorization')
    if not token:
        return jsonify({'message': 'Token is missing'}), 401

    try:
        if " " in token:
            token = token.split(" ")[1]
        decoded_token = jwt.decode(token, SECRET_FOR_TOKEN, algorithms=['HS256'])
        company_id = decoded_token['company_id']
    except jwt.ExpiredSignatureError:
        return jsonify({'message': 'Token has expired'}), 401
    except jwt.InvalidTokenError:
        return jsonify({'message': 'Invalid token'}), 401

    conn = get_db()
    cursor = conn.cursor(dictionary=True)
    cursor.execute("SELECT * FROM RECORD_COMPANY WHERE id = %s", (company_id,))
    company = cursor.fetchone()

    if not company or company['session_token'] != token:
        return jsonify({'message': 'Invalid token'}), 401

    conn.close()
    return company_id, 200

def handle_concert_deletion(concert_id):
    conn = get_db()
    cursor = conn.cursor(dictionary=True)
    
    try:

        cursor.execute("SELECT image FROM concert WHERE id = %s", (concert_id,))
        concert = cursor.fetchone()
        concert_image = concert['image'] if concert else None

        cursor.execute("""
            SELECT t.id, t.price, t.user_id, u.username, u.email 
            FROM ticket t
            LEFT JOIN user u ON t.user_id = u.id
            WHERE t.concert_id = %s AND t.user_id IS NOT NULL
        """, (concert_id,))
        tickets = cursor.fetchall()

        for ticket in tickets:
            cursor.execute("""
                UPDATE user 
                SET refunds = refunds + %s 
                WHERE id = %s
            """, (ticket['price'], ticket['user_id']))

        cursor.execute("DELETE FROM ticket WHERE concert_id = %s", (concert_id,))
        cursor.execute("DELETE FROM review WHERE concert_id = %s", (concert_id,))
        cursor.execute("DELETE FROM artist_concert WHERE concert_id = %s", (concert_id,))

        if concert_image:
            try:
                image_path = os.path.join(current_app.root_path, 'static', 'images', 'concerts', concert_image)
                if os.path.exists(image_path):
                    os.remove(image_path)
                    current_app.logger.info(f"Deleted concert image: {image_path}")
            except Exception as e:
                current_app.logger.error(f"Failed to delete concert image {concert_image}: {str(e)}")

        cursor.execute("DELETE FROM concert WHERE id = %s", (concert_id,))
        rows_affected = cursor.rowcount

        if rows_affected == 0:
            raise Exception("No concert found with the given ID")

        conn.commit()
        
        return True, "Concert and all related data deleted successfully"

    except Exception as e:
        conn.rollback()
        current_app.logger.error(f"Error in concert deletion: {str(e)}")
        return False, f"Error during concert deletion: {str(e)}"

    finally:
        conn.close()

def handle_concert_creation(record_company_id, title, image_file, date, time, artist_ids, place_id, sector_prices, tour_id=None):
    conn = get_db()
    cursor = conn.cursor(dictionary=True)
    
    try:
        cursor.execute("SELECT id FROM place WHERE id = %s", (place_id,))
        if not cursor.fetchone():
            raise ValueError("Place not found")

        if artist_ids:
            placeholders = ','.join(['%s'] * len(artist_ids))
            cursor.execute(f"SELECT id FROM artist WHERE id IN ({placeholders})", tuple(artist_ids))
            existing_artists = [row['id'] for row in cursor.fetchall()]
            if len(existing_artists) != len(artist_ids):
                missing = set(artist_ids) - set(existing_artists)
                raise ValueError(f"Artists not found: {missing}")

        image_filename = None
        if image_file and image_file.filename:
            cursor.execute("""
                INSERT INTO concert (title, date, time, place_id, tour_id, record_company_id)
                VALUES (%s, %s, %s, %s, %s, %s)
            """, (title, date, time, place_id, tour_id, record_company_id))
            concert_id = cursor.lastrowid

            file_ext = os.path.splitext(image_file.filename)[1].lower()
            image_filename = f"{concert_id}{file_ext}"
            save_path = os.path.join(current_app.root_path, 'static', 'images', 'concerts', image_filename)

            image_file.save(save_path)

            cursor.execute("""
                UPDATE concert SET image = %s WHERE id = %s
            """, (image_filename, concert_id))
        else:
            cursor.execute("""
                INSERT INTO concert (title, date, time, place_id, tour_id, record_company_id)
                VALUES (%s, %s, %s, %s, %s, %s)
            """, (title, date, time, place_id, tour_id, record_company_id))
            concert_id = cursor.lastrowid

        cursor.execute("""
            SELECT s.id, s.sector_id 
            FROM seat s
            JOIN sector sec ON s.sector_id = sec.id
            WHERE sec.place_id = %s
        """, (place_id,))
        seats = cursor.fetchall()

        tickets_created = 0
        for seat in seats:
            price = sector_prices[str(seat['sector_id'])] if str(seat['sector_id']) in sector_prices else 0
            if price <= 0:
                raise ValueError(f"Invalid price for sector {seat['sector_id']}")
            cursor.execute("""
                INSERT INTO ticket (price, validated, concert_id, seat_id)
                VALUES (%s, %s, %s, %s)
            """, (price, False, concert_id, seat['id']))
            tickets_created += 1

        for artist_id in artist_ids:
            cursor.execute("""
                INSERT INTO artist_concert (artist_id, concert_id)
                VALUES (%s, %s)
            """, (artist_id, concert_id))

        conn.commit()
        
        return True, {
            "message": "Concert created successfully"
        }

    except Exception as e:
        conn.rollback()
        
        if 'concert_id' in locals() and image_filename:
            try:
                image_path = os.path.join(current_app.root_path, 'static', 'images', 'concerts', image_filename)
                if os.path.exists(image_path):
                    os.remove(image_path)
            except Exception as img_error:
                current_app.logger.error(f"Failed to delete concert image after failed creation: {str(img_error)}")
        
        current_app.logger.error(f"Error in concert creation: {str(e)}")
        return False, f"Error during concert creation: {str(e)}"

    finally:
        conn.close()

# Automatic Login
@protected_record_bp.route('/protected_record/automatic_login', methods=['POST'])
def protected_record_automatic_login():
    record_company_id, status_code = verify_token()
    if status_code != 200:
        return jsonify({'message': 'Token is invalid'}), 401

    conn = get_db()
    cursor = conn.cursor(dictionary=True)
    cursor.execute("SELECT * FROM RECORD_COMPANY WHERE id = %s", (record_company_id,))
    company = cursor.fetchone()

    if not company:
        return jsonify({'message': 'Record company not found'}), 404

    conn.commit()
    conn.close()

    token = create_token(company['id'])

    return jsonify({'token': token}), 200

# Login
@protected_record_bp.route('/protected_record/login', methods=['POST'])
def protected_record_login():
    data = request.get_json()
    required = ['email', 'password']
    if not all(k in data for k in required):
        return jsonify({'message': 'Missing fields'}), 400

    conn = get_db()
    cursor = conn.cursor(dictionary=True)

    cursor.execute("SELECT * FROM RECORD_COMPANY WHERE email = %s", (data['email'],))
    
    company = cursor.fetchone()

    if not company or not bcrypt.checkpw(data['password'].encode('utf-8'), company['password'].encode('utf-8')):
        return jsonify({'message': 'Invalid credentials'}), 401

    conn.commit()
    conn.close()

    token = create_token(company['id'])

    return jsonify({'token': token}), 200

# Logout
@protected_record_bp.route('/protected_record/logout', methods=['POST'])
def protected_record_logout():
    record_company_id, status_code = verify_token()
    if status_code != 200:
        return jsonify({'message': 'Token is invalid'}), 401

    conn = get_db()
    cursor = conn.cursor(dictionary=True)
    cursor.execute("UPDATE RECORD_COMPANY SET session_token = NULL WHERE id = %s", (record_company_id,))
    conn.commit()
    conn.close()

    return jsonify({'message': 'Logged out successfully'}), 200

# Get record company data
@protected_record_bp.route('/protected_record/record_company', methods=['GET'])
def protected_record_get_record_company_data():
    auth_header = request.headers.get('Authorization')
    if not auth_header:
        return jsonify({'message': 'Token is missing'}), 401

    try:
        record_company_id = verify_token()[0]
    except Exception:
        return jsonify({'message': 'Invalid token'}), 401

    conn = get_db()
    cursor = conn.cursor(dictionary=True)

    cursor.execute("SELECT email FROM RECORD_COMPANY WHERE id = %s", (record_company_id,))
    record_company = cursor.fetchone()

    if not record_company:
        return jsonify({'message': 'Record company not found'}), 404

    conn.close()

    return jsonify(record_company), 200

# Update record company data
@protected_record_bp.route('/protected_record/update', methods=['PUT'])
def protected_record_update_record_company_data():
    auth_header = request.headers.get('Authorization')
    if not auth_header:
        return jsonify({'message': 'Token is missing'}), 401

    try:
        record_company_id = verify_token()[0]
    except Exception:
        return jsonify({'message': 'Invalid token'}), 401

    data = request.get_json()
    allowed_fields = ['email', 'password']

    updates = {k: data[k] for k in allowed_fields if k in data}
    if not updates:
        return jsonify({'message': 'No valid fields provided'}), 400

    if 'password' in updates:
        updates['password'] = bcrypt.hashpw(updates['password'].encode('utf-8'), bcrypt.gensalt()).decode('utf-8')

    set_clause = ', '.join(f"{key} = %s" for key in updates)
    values = list(updates.values())

    conn = get_db()
    cursor = conn.cursor(dictionary=True)

    try:
        cursor.execute(f"""
            UPDATE RECORD_COMPANY SET {set_clause} WHERE id = %s
        """, values + [record_company_id])
        conn.commit()
        return jsonify({'message': 'Record company updated successfully'}), 200
    except Exception as e:
        return jsonify({'message': f'Server error: {str(e)}'}), 500
    finally:
        conn.close()

# Get all events
@protected_record_bp.route('/protected_record/all_events', methods=['GET'])
def get_record_company_events():
    auth_header = request.headers.get('Authorization')
    if not auth_header:
        return jsonify({'message': 'Token is missing'}), 401
    
    try:
        record_company_id = verify_token()[0]
    except Exception as e:
        return jsonify({'message': 'Invalid token'}), 401

    conn = get_db()
    cursor = conn.cursor(dictionary=True)

    try:
        cursor.execute("""
            SELECT 
                c.id, 
                c.image, 
                c.title, 
                DATE_FORMAT(c.date, '%Y-%m-%d') as c_date,
                p.name as place_name
            FROM concert c
            JOIN place p ON c.place_id = p.id
            WHERE c.record_company_id = %s AND c.tour_id IS NULL
            ORDER BY c.date
        """, (record_company_id,))
        concerts = cursor.fetchall()

        for concert in concerts:
            cursor.execute("""
                SELECT a.id, a.name, a.image 
                FROM artist_concert ac
                JOIN artist a ON ac.artist_id = a.id
                WHERE ac.concert_id = %s
                LIMIT 1
            """, (concert['id'],))
            artist = cursor.fetchone()
            concert['artist'] = artist if artist else None

        cursor.execute("""
            SELECT t.id, t.image, t.title
            FROM tour t
            WHERE t.record_company_id = %s
            ORDER BY t.title
        """, (record_company_id,))
        tours = cursor.fetchall()

        for tour in tours:
            cursor.execute("""
                SELECT a.id, a.name, a.image 
                FROM concert c
                JOIN artist_concert ac ON c.id = ac.concert_id
                JOIN artist a ON ac.artist_id = a.id
                WHERE c.tour_id = %s
                LIMIT 1
            """, (tour['id'],))
            artist = cursor.fetchone()
            tour['artist'] = artist if artist else None

            cursor.execute("""
                SELECT COUNT(*) as concert_count 
                FROM concert 
                WHERE tour_id = %s
            """, (tour['id'],))
            tour['concert_count'] = cursor.fetchone()['concert_count']

            cursor.execute("""
                SELECT DATE_FORMAT(date, '%Y-%m-%d') as date 
                FROM concert 
                WHERE tour_id = %s 
                ORDER BY 
                    CASE WHEN date >= CURDATE() THEN 0 ELSE 1 END,
                    ABS(DATEDIFF(date, CURDATE()))
                LIMIT 1
            """, (tour['id'],))
            date_result = cursor.fetchone()
            tour['relevant_date'] = date_result['date'] if date_result else None

        return jsonify({
            'concerts': concerts,
            'tours': tours
        }), 200

    except Exception as e:
        return jsonify({'message': f'Server error: {str(e)}'}), 500
    finally:
        conn.close()

# Get tour details
@protected_record_bp.route('/protected_record/tour/<int:tour_id>', methods=['GET'])
def protected_record_tour_details(tour_id):
    conn = get_db()
    cursor = conn.cursor(dictionary=True)

    try:
        cursor.execute("""
            SELECT id, title, image
            FROM tour
            WHERE id = %s
        """, (tour_id,))
        tour = cursor.fetchone()

        if not tour:
            return jsonify({'error': 'Tour not found'}), 404

        cursor.execute("""
            SELECT DISTINCT
                a.id AS artist_id,
                a.name AS artist_name,
                a.image AS artist_image
            FROM 
                artist a
            JOIN 
                artist_concert ac ON a.id = ac.artist_id
            JOIN 
                concert c ON ac.concert_id = c.id
            WHERE 
                c.tour_id = %s
        """, (tour_id,))
        artists = cursor.fetchall()

        cursor.execute("""
            SELECT 
                c.id AS concert_id,
                c.title AS concert_title,
                DATE_FORMAT(c.date, '%Y-%m-%d') AS concert_date,
                p.name AS place_name
            FROM 
                concert c
            JOIN 
                place p ON c.place_id = p.id
            WHERE 
                c.tour_id = %s AND c.date >= CURDATE()
            ORDER BY 
                c.date, c.time
        """, (tour_id,))
        concerts = cursor.fetchall()

        result = {
            'tour': tour,
            'artists': artists,
            'concerts': concerts
        }

        return jsonify(result), 200

    except Exception as e:
        current_app.logger.error(f"Error fetching tour details {tour_id}: {str(e)}")
        return jsonify({'error': str(e)}), 500

    finally:
        conn.close()

# Delete tour
@protected_record_bp.route('/protected_record/tour/delete/<int:tour_id>', methods=['DELETE'])
def protected_record_delete_tour(tour_id):
    auth_header = request.headers.get('Authorization')
    if not auth_header:
        return jsonify({'message': 'Token is missing'}), 401

    try:
        record_company_id = verify_token()[0]
    except Exception:
        return jsonify({'message': 'Invalid token'}), 401

    conn = get_db()
    cursor = conn.cursor(dictionary=True)

    try:
        cursor.execute(
            "SELECT id FROM tour WHERE id = %s AND record_company_id = %s",
            (tour_id, record_company_id)
        )

        if cursor.fetchone() is None:
            return jsonify({'message': 'Tour not found or not authorized'}), 404

        cursor.execute("SELECT id FROM concert WHERE tour_id = %s", (tour_id,))

        concert_ids = cursor.fetchall()
        concert_ids = [row['id'] for row in concert_ids]
        user_ids_set = set()

        for concert_id in concert_ids:
            cursor.execute("""
                SELECT DISTINCT u.id AS user_id
                FROM ticket t
                JOIN user u ON u.id = t.user_id
                WHERE t.concert_id = %s AND t.user_id IS NOT NULL
            """, (concert_id,))
            users_for_concert = cursor.fetchall()
            user_ids_set.update([u['user_id'] for u in users_for_concert])

            success, message = handle_concert_deletion(concert_id)
            if not success:
                raise Exception(f"Failed to delete concert {concert_id}: {message}")

        cursor.execute("SELECT image FROM tour WHERE id = %s", (tour_id,))
        tour = cursor.fetchone()
        tour_image = tour['image'] if tour else None

        if tour_image:
            try:
                image_path = os.path.join(current_app.root_path, 'static', 'images', 'tours', tour_image)
                if os.path.exists(image_path):
                    os.remove(image_path)
                    current_app.logger.info(f"Deleted concert image: {image_path}")
            except Exception as e:
                current_app.logger.error(f"Failed to delete concert image {tour_image}: {str(e)}")

        for user_id in user_ids_set:
            notification_title = "Tour Canceled"
            notification_message = (
                "The tour you had a ticket for has been canceled. "
                "A refund has been issued to your account."
            )
            cursor.execute("""
                INSERT INTO notification (title, description, is_read, user_id)
                VALUES (%s, %s, 0, %s)
            """, (notification_title, notification_message, user_id))

        cursor.execute("DELETE FROM tour WHERE id = %s", (tour_id,))
        if cursor.rowcount == 0:
            raise Exception("Failed to delete the tour")

        conn.commit()
        return jsonify({'message': 'Tour and all associated concerts deleted successfully'}), 200

    except Exception as e:
        conn.rollback()
        current_app.logger.error(f"Error deleting tour {tour_id}: {str(e)}")
        return jsonify({'message': f'Error deleting tour: {str(e)}'}), 500

    finally:
        conn.close()

# Create tour
@protected_record_bp.route('/protected_record/tour/create', methods=['POST'])
def protected_record_create_tour():
    auth_header = request.headers.get('Authorization')
    if not auth_header:
        return jsonify({'message': 'Token is missing'}), 401

    try:
        record_company_id = verify_token()[0]
    except Exception:
        return jsonify({'message': 'Invalid token'}), 401

    try:
        data = request.form
        title = data.get('title')
        image_file = request.files.get('image')
        artist_ids = request.form.getlist('artist_ids') 
        concerts_json = data.get('concerts')  

        if not title or not image_file or not artist_ids or not concerts_json:
            return jsonify({'message': 'Missing required fields'}), 400

        artist_ids = [int(aid) for aid in artist_ids]
        concerts = json.loads(concerts_json)

        conn = get_db()
        cursor = conn.cursor(dictionary=True)

        cursor.execute("""
            INSERT INTO tour (title, record_company_id)
            VALUES (%s, %s)
        """, (title, record_company_id))
        tour_id = cursor.lastrowid

        file_ext = os.path.splitext(image_file.filename)[1].lower()
        image_filename = f"{tour_id}{file_ext}"
        save_path = os.path.join(current_app.root_path, 'static', 'images', 'tours', image_filename)
        image_file.save(save_path)

        cursor.execute("""
            UPDATE tour SET image = %s WHERE id = %s
        """, (image_filename, tour_id))

        conn.commit() 

        for concert in concerts:
            success, result = handle_concert_creation(
                record_company_id=record_company_id,
                title=concert['title'],
                image_file=None,
                date=concert['date'],
                time=concert['time'],
                artist_ids=artist_ids,
                place_id=concert['place_id'],
                sector_prices=concert['sector_prices'],
                tour_id=tour_id
            )
            if not success:
                return jsonify({'error': f"Failed to create concert: {result}"}), 400

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
            """, (
                "New Tour!",
                f"A new tour has been created that might interest you: {title}",
                user['user_id']
            ))

        conn.commit()

        return jsonify({'message': 'Tour and concerts created successfully', 'tour_id': tour_id}), 201

    except Exception as e:
        conn.rollback()
        current_app.logger.error(f"Error creating tour: {str(e)}")
        return jsonify({'error': str(e)}), 500
    finally:
        conn.close()

# Get concert details
@protected_record_bp.route('/protected_record/concert/<int:concert_id>', methods=['GET'])
def protected_record_concert_details(concert_id):
    conn = get_db()
    cursor = conn.cursor(dictionary=True)

    try:
        cursor.execute("""
            SELECT 
                c.title AS concert_title,
                c.image AS concert_image,
                DATE_FORMAT(c.date, '%Y-%m-%d') AS concert_date,
                TIME_FORMAT(c.time, '%H:%i') AS concert_time,
                p.name AS place_name,
                p.address AS place_address,
                p.id AS place_id,
                t.id AS tour_id,
                t.title AS tour_title,
                t.image AS tour_image
            FROM 
                concert c
            JOIN 
                place p ON c.place_id = p.id
            LEFT JOIN  -- Usiamo LEFT JOIN per includere concerti senza tour
                tour t ON c.tour_id = t.id
            WHERE 
                c.id = %s
        """, (concert_id,))
        
        concert_data = cursor.fetchone()
        if not concert_data:
            return jsonify({'message': 'Concert not found'}), 404

        if concert_data['tour_id'] and not concert_data['concert_image']:
            concert_data['concert_image'] = concert_data['tour_image']

        cursor.execute("""
            SELECT 
                a.id AS artist_id,
                a.name AS artist_name,
                a.image AS artist_image
            FROM 
                artist a
            JOIN 
                artist_concert ac ON a.id = ac.artist_id
            WHERE 
                ac.concert_id = %s
        """, (concert_id,))
        artists = cursor.fetchall()

        cursor.execute("""
            SELECT 
                COUNT(*) AS total_tickets,
                SUM(CASE WHEN t.user_id IS NOT NULL THEN 1 ELSE 0 END) AS sold_tickets,
                SUM(CASE WHEN t.user_id IS NOT NULL THEN t.price ELSE 0 END) AS total_earnings
            FROM 
                ticket t
            WHERE 
                t.concert_id = %s
        """, (concert_id,))
        ticket_stats = cursor.fetchone()

        total_tickets = ticket_stats['total_tickets'] or 0
        sold_tickets = ticket_stats['sold_tickets'] or 0
        total_earnings = ticket_stats['total_earnings'] or 0.0
        sold_percentage = (sold_tickets / total_tickets * 100) if total_tickets > 0 else 0

        response = {
            'concert_info': {
                **concert_data,
                'effective_image': concert_data['concert_image'] or concert_data.get('tour_image'),
                'is_part_of_tour': concert_data['tour_id'] is not None,
                'total_tickets': total_tickets,
                'sold_percentage': round(sold_percentage, 2),
                'total_earnings': round(total_earnings, 2)
            },
            'artists': artists
        }

        if concert_data['tour_id']:
            response['tour_info'] = {
                'id': concert_data['tour_id'],
                'title': concert_data['tour_title'],
                'image': concert_data['tour_image']
            }

        return jsonify(response), 200
    
    except Exception as e:
        return jsonify({'message': f'Server error: {str(e)}'}), 500
    finally:
        conn.close()

# Delete concert
@protected_record_bp.route('/protected_record/concert/delete/<int:concert_id>', methods=['DELETE'])
def protected_record_delete_concert(concert_id):
    auth_header = request.headers.get('Authorization')
    if not auth_header:
        return jsonify({'message': 'Token is missing'}), 401

    try:
        record_company_id = verify_token()[0]
    except Exception:
        return jsonify({'message': 'Invalid token'}), 401

    conn = get_db()
    cursor = conn.cursor(dictionary=True)

    try:
        cursor.execute(
            "SELECT id FROM concert WHERE id = %s AND record_company_id = %s",
            (concert_id, record_company_id)
        )
        if cursor.fetchone() is None:
            return jsonify({'message': 'Concert not found or not authorized'}), 404

        cursor.execute("""
            SELECT DISTINCT u.id AS user_id
            FROM ticket t
            JOIN user u ON u.id = t.user_id
            WHERE t.concert_id = %s AND t.user_id IS NOT NULL
        """, (concert_id,))
        users = cursor.fetchall()

        success, message = handle_concert_deletion(concert_id)
        if not success:
            raise Exception(f"Failed to delete concert {concert_id}: {message}")

        for user in users:
            notification_title = "Concert Canceled"
            notification_message = (
                "The concert you had a ticket for has been canceled. "
                "A refund has been issued to your account."
            )
            cursor.execute("""
                INSERT INTO notification (title, description, is_read, user_id)
                VALUES (%s, %s, 0, %s)
            """, (notification_title, notification_message, user['user_id']))

        conn.commit()
        return jsonify({'message': 'Concert deleted and users notified successfully'}), 200

    except Exception as e:
        conn.rollback()
        current_app.logger.error(f"Error deleting concert {concert_id}: {str(e)}")
        return jsonify({'message': f'Error deleting concert: {str(e)}'}), 500

    finally:
        conn.close()

# Create concert
@protected_record_bp.route('/protected_record/concert/create', methods=['POST'])
def protected_record_create_concert():
    auth_header = request.headers.get('Authorization')
    if not auth_header:
        return jsonify({'message': 'Token is missing'}), 401

    try:
        record_company_id = verify_token()[0]
    except Exception:
        return jsonify({'message': 'Invalid token'}), 401
    
    try:
        data = request.form
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
            return jsonify({'error': result}), 400

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

        conn.commit()
        conn.close()

        return jsonify(result), 201
    except Exception as e:
        current_app.logger.error(f"API concert creation error: {str(e)}")
        return jsonify({'error': str(e)}), 500

# Get artists
@protected_record_bp.route('/protected_record/artists', methods=['GET'])
def protected_record_get_artists():
    auth_header = request.headers.get('Authorization')
    if not auth_header:
        return jsonify({'message': 'Token is missing'}), 401

    try:
        record_company_id = verify_token()[0]
    except Exception:
        return jsonify({'message': 'Invalid token'}), 401

    try:
        conn = get_db()
        cursor = conn.cursor(dictionary=True)

        cursor.execute("""
            SELECT 
                a.id AS artist_id,
                a.name AS artist_name,
                a.image AS artist_image,
                COUNT(DISTINCT l.id) AS likes_count,
                COALESCE(AVG(DISTINCT r.rate), 0) AS average_rating
            FROM 
                artist a
            LEFT JOIN 
                likes l ON a.id = l.artist_id
            LEFT JOIN 
                (
                    SELECT ac.artist_id, r.rate
                    FROM review r
                    JOIN concert c ON r.concert_id = c.id
                    JOIN artist_concert ac ON c.id = ac.concert_id
                ) AS r ON a.id = r.artist_id
            WHERE 
                a.record_company_id = %s
            GROUP BY 
                a.id, a.name, a.image
        """, (record_company_id,))

        artists = cursor.fetchall()
        conn.close()

        return jsonify(artists), 200
    except Exception as e:
        current_app.logger.error(f"API all artists error: {str(e)}")
        return jsonify({'error': str(e)}), 500

# Get artists details
@protected_record_bp.route('/protected_record/artist/<int:artist_id>', methods=['GET'])
def protected_record_artist_details(artist_id):

    conn = get_db()
    cursor = conn.cursor(dictionary=True)

    try:
        cursor.execute("""
            SELECT 
                a.id,
                a.name,
                a.image,
                (SELECT COUNT(*) FROM likes WHERE artist_id = a.id) AS likes_count,
                (SELECT COALESCE(AVG(r.rate), 0) 
                 FROM review r
                 JOIN concert c ON r.concert_id = c.id
                 JOIN artist_concert ac ON c.id = ac.concert_id
                 WHERE ac.artist_id = a.id) AS average_rating
            FROM artist a
            WHERE a.id = %s
        """, (artist_id,))
        
        artist_data = cursor.fetchone()
        if not artist_data:
            return jsonify({'message': 'Artist not found'}), 404

        cursor.execute("""
            SELECT 
                c.id,
                c.title,
                c.image,
                DATE_FORMAT(c.date, '%Y-%m-%d') AS date,
                p.name AS place_name
            FROM concert c
            JOIN artist_concert ac ON c.id = ac.concert_id
            JOIN place p ON c.place_id = p.id
            WHERE ac.artist_id = %s
            AND c.tour_id IS NULL
            AND c.date >= CURDATE()
            ORDER BY c.date
        """, (artist_id,))
        
        concerts = cursor.fetchall()

        cursor.execute("""
            SELECT 
                t.id,
                t.title,
                t.image,
                COUNT(c.id) AS concerts_count
            FROM tour t
            JOIN concert c ON t.id = c.tour_id
            JOIN artist_concert ac ON c.id = ac.concert_id
            WHERE ac.artist_id = %s
            AND c.date >= CURDATE()
            GROUP BY t.id
            HAVING COUNT(c.id) > 0
            ORDER BY t.title
        """, (artist_id,))
        
        tours = cursor.fetchall()

        cursor.execute("""
            SELECT 
                r.id,
                r.rate,
                r.description,
                c.title AS concert_title,
                DATE_FORMAT(c.date, '%Y-%m-%d') AS concert_date,
                u.username,
                u.image AS user_image
            FROM review r
            JOIN concert c ON r.concert_id = c.id
            JOIN artist_concert ac ON c.id = ac.concert_id
            JOIN user u ON r.user_id = u.id
            WHERE ac.artist_id = %s
            ORDER BY r.id DESC
        """, (artist_id,))
        
        reviews = cursor.fetchall()

        response = {
            'artist': {
                'id': artist_data['id'],
                'name': artist_data['name'],
                'image': artist_data['image'],
                'likes_count': artist_data['likes_count'],
                'average_rating': float(artist_data['average_rating'])
            },
            'concerts': concerts,
            'tours': tours,
            'reviews': reviews
        }

        return jsonify(response), 200

    except Exception as e:
        return jsonify({'message': f'Server error: {str(e)}'}), 500
    finally:
        conn.close()

# Create artist
@protected_record_bp.route('/protected_record/artist/create', methods=['POST'])
def protected_record_create_artist():
    auth_header = request.headers.get('Authorization')
    if not auth_header:
        return jsonify({'message': 'Token is missing'}), 401

    try:
        record_company_id = verify_token()[0]
    except Exception:
        return jsonify({'message': 'Invalid token'}), 401

    data = request.form
    name = data.get('name')
    image_file = request.files.get('image')

    if not name or not image_file:
        return jsonify({'message': 'Missing required fields'}), 400

    conn = get_db()
    cursor = conn.cursor(dictionary=True)

    try:
        cursor.execute("""
            INSERT INTO artist (name, record_company_id)
            VALUES (%s, %s)
        """, (name, record_company_id))
        artist_id = cursor.lastrowid

        file_ext = os.path.splitext(image_file.filename)[1].lower()
        image_filename = f"{artist_id}{file_ext}"
        save_path = os.path.join(current_app.root_path, 'static', 'images', 'artists', image_filename)

        image_file.save(save_path)

        cursor.execute("""
            UPDATE artist SET image = %s WHERE id = %s
        """, (image_filename, artist_id))

        conn.commit()

        return jsonify({'message': 'Artist created successfully'}), 201

    except Exception as e:
        conn.rollback()
        current_app.logger.error(f"Error creating artist: {str(e)}")
        return jsonify({'error': str(e)}), 500
    finally:
        conn.close()

# Delete artist
@protected_record_bp.route('/protected_record/artist/delete/<int:artist_id>', methods=['DELETE'])
def protected_record_delete_artist(artist_id):
    auth_header = request.headers.get('Authorization')
    if not auth_header:
        return jsonify({'message': 'Token is missing'}), 401

    try:
        record_company_id = verify_token()[0]
    except Exception:
        return jsonify({'message': 'Invalid token'}), 401

    conn = get_db()
    cursor = conn.cursor(dictionary=True)

    try:
        cursor.execute("""
            SELECT id, image FROM artist 
            WHERE id = %s AND record_company_id = %s
        """, (artist_id, record_company_id))
        artist = cursor.fetchone()

        if not artist:
            return jsonify({'message': 'Artist not found or unauthorized'}), 404

        cursor.execute("""
            SELECT c.id FROM concert c
            JOIN artist_concert ac ON c.id = ac.concert_id
            WHERE ac.artist_id = %s AND c.date > CURDATE()
        """, (artist_id,))
        future_concerts = cursor.fetchall()

        if future_concerts:
            return jsonify({'message': 'Cannot delete artist with future concerts'}), 400

        cursor.execute("""
            DELETE FROM likes WHERE artist_id = %s
        """, (artist_id,))

        cursor.execute("""
            DELETE FROM artist_concert WHERE artist_id = %s
        """, (artist_id,))

        if artist['image']:
            try:
                image_path = os.path.join(current_app.root_path, 'static', 'images', 'artists', artist['image'])
                if os.path.exists(image_path):
                    os.remove(image_path)
            except Exception as e:
                current_app.logger.error(f"Failed to delete artist image {artist['image']}: {str(e)}")

        cursor.execute("""
            DELETE FROM artist WHERE id = %s
        """, (artist_id,))

        conn.commit()
        return jsonify({'message': 'Artist deleted successfully'}), 200

    except Exception as e:
        conn.rollback()
        current_app.logger.error(f"Error deleting artist {artist_id}: {str(e)}")
        return jsonify({'error': str(e)}), 500

    finally:
        conn.close()

# Get all places
@protected_record_bp.route('/protected_record/places', methods=['GET'])
def protected_record_get_places():
    try:
        conn = get_db()
        cursor = conn.cursor(dictionary=True)

        cursor.execute("""
            SELECT id, name, address
            FROM place
        """)

        places = cursor.fetchall()
        conn.close()

        return jsonify(places), 200

    except Exception as e:
        current_app.logger.error(f"Error fetching places: {str(e)}")
        return jsonify({'error': str(e)}), 500

# Get place details
@protected_record_bp.route('/protected_record/place/<int:place_id>', methods=['GET'])
def protected_record_place_details(place_id):
    try:
        conn = get_db()
        cursor = conn.cursor(dictionary=True)

        cursor.execute("""
            SELECT id, name, address 
            FROM place 
            WHERE id = %s
        """, (place_id,))
        place = cursor.fetchone()
        
        if not place:
            return jsonify({'error': 'Place not found'}), 404

        cursor.execute("""
            SELECT * 
            FROM sector 
            WHERE place_id = %s
        """, (place_id,))
        sectors = cursor.fetchall()

        for sector in sectors:
            cursor.execute("""
                SELECT * 
                FROM seat 
                WHERE sector_id = %s
            """, (sector['id'],))
            seats = cursor.fetchall()
            sector['seats'] = seats

        place['sectors'] = sectors

        return jsonify(place)
    except Exception as e:
        current_app.logger.error(f"Error searching place {place_id}: {str(e)}")
        return jsonify({'error': str(e)}), 500
    finally:
        conn.close()

# Get company analytics
@protected_record_bp.route('/protected_record/analytics', methods=['GET'])
def protected_record_get_analytics():
    auth_header = request.headers.get('Authorization')
    if not auth_header:
        return jsonify({'message': 'Token is missing'}), 401

    try:
        record_company_id = verify_token()[0]
    except Exception:
        return jsonify({'message': 'Invalid token'}), 401

    conn = get_db()
    cursor = conn.cursor(dictionary=True)

    try:
        cursor.execute("""
            SELECT 
                COUNT(t.id) AS total_tickets_sold,
                SUM(t.price) AS total_earnings
            FROM concert c
            LEFT JOIN ticket t ON c.id = t.concert_id
            WHERE c.record_company_id = %s AND t.user_id IS NOT NULL
        """, (record_company_id,))
        ticket_data = cursor.fetchone()

        cursor.execute("""
            SELECT COUNT(*) AS past_concerts
            FROM concert
            WHERE record_company_id = %s AND date < NOW()
        """, (record_company_id,))
        past_concerts = cursor.fetchone()['past_concerts']

        cursor.execute("""
            SELECT COUNT(*) AS upcoming_concerts
            FROM concert
            WHERE record_company_id = %s AND date >= NOW()
        """, (record_company_id,))
        upcoming_concerts = cursor.fetchone()['upcoming_concerts']

        analytics = {
            'total_tickets_sold': ticket_data['total_tickets_sold'] or 0,
            'total_earnings': float(ticket_data['total_earnings'] or 0),
            'past_concerts': past_concerts,
            'upcoming_concerts': upcoming_concerts
        }

        return jsonify(analytics), 200

    except Exception as e:
        current_app.logger.error(f"Error fetching analytics: {str(e)}")
        return jsonify({'error': str(e)}), 500
    finally:
        conn.close()

# Ticket verification
@protected_record_bp.route('/protected_record/ticket/validate', methods=['POST'])
def protected_record_validate_ticket():
    auth_header = request.headers.get('Authorization')
    if not auth_header:
        return jsonify({'message': 'Token is missing'}), 401

    try:
        record_company_id = verify_token()[0]
    except Exception:
        return jsonify({'message': 'Invalid token'}), 401

    data = request.get_json()
    required_fields = ['ticket_id', 'concert_id', 'user_id']
    if not all(field in data for field in required_fields):
        return jsonify({'message': 'Missing required fields'}), 400

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
            SELECT id FROM ticket 
            WHERE id = %s AND user_id = %s AND concert_id = %s
        """, (ticket_id, user_id, concert_id))
        ticket = cursor.fetchone()
        if not ticket:
            return jsonify({'message': 'Ticket not found or does not belong to the specified user'}), 404

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

# Get all notifications
@protected_record_bp.route('/protected_record/notifications', methods=['GET'])
def protected_record_get_notifications():
    auth_header = request.headers.get('Authorization')
    if not auth_header:
        return jsonify({'message': 'Token is missing'}), 401
    
    try:
        record_company_id = verify_token()[0]
    except Exception:
        return jsonify({'message': 'Invalid token'}), 401

    conn = get_db()
    cursor = conn.cursor(dictionary=True)

    cursor.execute("""
        SELECT 
            id,
            title,
            description,
            is_read
        FROM 
            notification
        WHERE
            record_company_id = %s
    """, (record_company_id,))

    notifications = cursor.fetchall()
    conn.close()

    return jsonify(notifications), 200

# Read notification
@protected_record_bp.route('/protected_record/notification/read/<int:notification_id>', methods=['POST'])
def protected_record_read_notification(notification_id):
    auth_header = request.headers.get('Authorization')
    if not auth_header:
        return jsonify({'message': 'Token is missing'}), 401
    
    try:
        record_company_id = verify_token()[0]
    except Exception:
        return jsonify({'message': 'Invalid token'}), 401

    conn = get_db()
    cursor = conn.cursor(dictionary=True)

    cursor.execute("""
        UPDATE notification 
        SET is_read = 1 
        WHERE id = %s AND record_company_id = %s
    """, (notification_id, record_company_id))

    conn.commit()
    conn.close()

    return jsonify({'message': 'Notification marked as read'}), 200
