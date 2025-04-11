from flask import Blueprint, request, jsonify
from db import get_db
from datetime import timedelta

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
        print(tours)
        conn.close()
        return jsonify(tours), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@tour_bp.route('/admin/tours/<int:tour_id>/concerts', methods=['GET'])
def get_concerts_by_tour(tour_id):
    try:
        conn = get_db()
        cursor = conn.cursor(dictionary=True)

        # Verifica che il tour esista
        cursor.execute("SELECT id FROM tour WHERE id = %s", (tour_id,))
        if not cursor.fetchone():
            conn.close()
            return jsonify({'message': 'Tour not found'}), 404

        # Prende i concerti associati
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
    try:
        conn = get_db()
        cursor = conn.cursor()

        # Verifica se il tour esiste
        cursor.execute("SELECT id FROM tour WHERE id = %s", (tour_id,))
        if not cursor.fetchone():
            conn.close()
            return jsonify({'message': 'Tour not found'}), 404

        # Recupera tutti i concerti associati
        cursor.execute("SELECT id FROM concert WHERE tour_id = %s", (tour_id,))
        concert_ids = [row[0] for row in cursor.fetchall()]

        # Elimina le associazioni degli artisti per ogni concerto
        for concert_id in concert_ids:
            cursor.execute("DELETE FROM artist_concert WHERE concert_id = %s", (concert_id,))

        # Elimina i concerti associati
        cursor.execute("DELETE FROM concert WHERE tour_id = %s", (tour_id,))

        # Elimina il tour
        cursor.execute("DELETE FROM tour WHERE id = %s", (tour_id,))

        conn.commit()
        conn.close()

        return jsonify({'message': f'Tour {tour_id} and associated concerts deleted successfully'}), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500
