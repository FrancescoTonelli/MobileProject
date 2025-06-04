from flask import Blueprint, request, jsonify, current_app
from db import get_db
from datetime import timedelta
from ..protected_record import handle_concert_deletion
import os

tour_bp = Blueprint('tour', __name__)

def convert_timedelta_to_time_string(timedelta_obj):
    if isinstance(timedelta_obj, timedelta):
        total_seconds = int(timedelta_obj.total_seconds())
        hours = total_seconds // 3600
        minutes = (total_seconds % 3600) // 60
        return f"{hours:02}:{minutes:02}"
    return None

@tour_bp.route('/admin/tours', methods=['GET'])
def get_all_tours():
    try:
        conn = get_db()
        cursor = conn.cursor(dictionary=True)
        cursor.execute("SELECT id, title, image FROM tour")
        tours = cursor.fetchall()
        conn.close()
        return jsonify(tours), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@tour_bp.route('/admin/tours/<int:tour_id>/concerts', methods=['GET'])
def get_concerts_by_tour(tour_id):
    try:
        conn = get_db()
        cursor = conn.cursor(dictionary=True)

        cursor.execute("SELECT id FROM tour WHERE id = %s", (tour_id,))
        if not cursor.fetchone():
            conn.close()
            return jsonify({'message': 'Tour not found'}), 404

        cursor.execute("""
            SELECT id, title, date, time, image
            FROM concert
            WHERE tour_id = %s
        """, (tour_id,))
        concerts = cursor.fetchall()
        for concert in concerts:
            concert['date'] = concert['date'].strftime('%Y-%m-%d')
            concert['time'] = convert_timedelta_to_time_string(concert['time'])
        conn.close()
        return jsonify(concerts), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@tour_bp.route('/admin/tours/<int:tour_id>', methods=['DELETE'])
def delete_tour(tour_id):
    conn = get_db()
    cursor = conn.cursor(dictionary=True)

    try:
        cursor.execute(
            "SELECT id FROM tour WHERE id = %s",
            (tour_id,)
        )

        if cursor.fetchone() is None:
            return jsonify({'message': 'Tour not found'}), 404

        cursor.execute("SELECT id FROM concert WHERE tour_id = %s", (tour_id,))

        concert_ids = cursor.fetchall()
        concert_ids = [row['id'] for row in concert_ids]
        user_ids_set = set()

        for concert_id in concert_ids:
            cursor.execute("""
                SELECT DISTINCT u.id AS user_id
                FROM ticket t
                JOIN user u ON u.id = t.user_id
                JOIN concert c ON c.id = t.concert_id
                WHERE t.user_id IS NOT NULL
                AND c.date >= CURRENT_DATE
                AND t.concert_id = %s
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
