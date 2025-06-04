from flask import Blueprint, request, jsonify
from db import get_db
from datetime import timedelta
from ..protected_record import handle_concert_deletion
from ..protected_fcm import send_push_notification

concert_bp = Blueprint('concert', __name__)

def convert_timedelta_to_time_string(timedelta_obj):
    if isinstance(timedelta_obj, timedelta):
        total_seconds = int(timedelta_obj.total_seconds())
        hours = total_seconds // 3600
        minutes = (total_seconds % 3600) // 60
        return f"{hours:02}:{minutes:02}"
    return None

@concert_bp.route('/admin/concerts/no-tour', methods=['GET'])
def get_concerts_without_tour():
    try:
        conn = get_db()
        cursor = conn.cursor(dictionary=True)
        cursor.execute("""
            SELECT id, title, image, date, time
            FROM concert
            WHERE tour_id IS NULL
        """)
        concerts = cursor.fetchall()
        for concert in concerts:
            concert['date'] = concert['date'].strftime('%Y-%m-%d')
            concert['time'] = convert_timedelta_to_time_string(concert['time'])
        conn.close()
        return jsonify(concerts), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@concert_bp.route('/admin/concerts/<int:concert_id>', methods=['GET'])
def get_concert_details(concert_id):
    try:
        conn = get_db()     
        cursor = conn.cursor(dictionary=True)
        cursor.execute("""
            SELECT c.id, c.title, c.date, c.time, c.image,
                   p.name AS place_name, p.address,
                   rc.email AS record_company_email
            FROM concert c
            LEFT JOIN place p ON c.place_id = p.id
            LEFT JOIN record_company rc ON c.record_company_id = rc.id
            WHERE c.id = %s
        """, (concert_id,))
        concert = cursor.fetchone()
        if not concert:
            conn.close()
            return jsonify({'message': 'Concert not found'}), 404
        concert['date'] = concert['date'].strftime('%Y-%m-%d')
        concert['time'] = convert_timedelta_to_time_string(concert['time'])
        concert['image_url'] = f"/static/images/concerts/{concert['image']}"
        cursor.execute("""
            SELECT a.id, a.name, a.image
            FROM artist_concert ac
            JOIN artist a ON ac.artist_id = a.id
            WHERE ac.concert_id = %s
        """, (concert_id,))
        artists = cursor.fetchall()
        for artist in artists:
            artist['image_url'] = f"/static/images/artists/{artist['image']}"
        concert['artists'] = artists
        conn.close()
        return jsonify(concert), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@concert_bp.route('/admin/concerts/<int:concert_id>', methods=['DELETE'])
def delete_concert(concert_id):
    conn = get_db()
    cursor = conn.cursor(dictionary=True)

    try:
        cursor.execute(
            "SELECT id FROM concert WHERE id = %s",
            (concert_id,)
        )
        if cursor.fetchone() is None:
            return jsonify({'message': 'Concert not found'}), 404

        cursor.execute("""
            SELECT DISTINCT u.id AS user_id
            FROM ticket t
            JOIN user u ON u.id = t.user_id
            JOIN concert c ON c.id = t.concert_id
            WHERE t.user_id IS NOT NULL
            AND c.date >= CURRENT_DATE
            AND t.concert_id = %s
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

            send_push_notification(
                user['user_id'], True, notification_title, notification_message
            )

        conn.commit()
        return jsonify({'message': 'Concert deleted and users notified successfully'}), 200

    except Exception as e:
        conn.rollback()
        return jsonify({'message': f'Error deleting concert: {str(e)}'}), 500

    finally:
        conn.close()
