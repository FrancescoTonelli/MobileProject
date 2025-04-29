from flask import Blueprint, request, jsonify
from db import get_db
import requests

place_bp = Blueprint('place', __name__)

@place_bp.route('/admin/places', methods=['GET'])
def get_places():
    conn = get_db()
    cursor = conn.cursor(dictionary=True)
    cursor.execute("SELECT id, name, address, latitude, longitude, email, telephone FROM place")
    places = cursor.fetchall()
    conn.close()
    return jsonify(places), 200

def geocode_address(address):
    url = 'https://nominatim.openstreetmap.org/search'
    params = {
        'q': address,
        'format': 'json'
    }
    headers = {
        'User-Agent': 'Admin Panel'  
    }
    response = requests.get(url, params=params, headers=headers)
    data = response.json()
    if data:
        latitude = float(data[0]['lat'])
        longitude = float(data[0]['lon'])
        return latitude, longitude
    return None, None

@place_bp.route('/admin/places', methods=['POST'])
def create_place():
    data = request.get_json()
    name = data.get('name')
    address = data.get('address')
    email = data.get('email')
    telephone = data.get('telephone')

    if not all([name, address, email, telephone]):
        return jsonify({'message': 'Missing required fields'}), 400

    latitude, longitude = geocode_address(address)
    if latitude is None or longitude is None:
        return jsonify({'message': 'Failed to geocode address'}), 400

    conn = get_db()
    cursor = conn.cursor()
    cursor.execute("""
        INSERT INTO place (name, address, latitude, longitude, email, telephone)
        VALUES (%s, %s, %s, %s, %s, %s)
    """, (name, address, latitude, longitude, email, telephone))
    conn.commit()
    conn.close()

    return jsonify({'message': 'Place created', 'latitude': latitude, 'longitude': longitude}), 201

@place_bp.route('/admin/places/<int:place_id>', methods=['DELETE'])
def delete_place(place_id):
    conn = get_db()
    cursor = conn.cursor()

    cursor.execute("SELECT id FROM place WHERE id = %s", (place_id,))
    if not cursor.fetchone():
        conn.close()
        return jsonify({'message': 'Place not found'}), 404

    cursor.execute("SELECT id FROM sector WHERE place_id = %s", (place_id,))
    sector_ids = [row[0] for row in cursor.fetchall()]

    if sector_ids:
        cursor.executemany("DELETE FROM seat WHERE sector_id = %s", [(sid,) for sid in sector_ids])

    cursor.execute("DELETE FROM sector WHERE place_id = %s", (place_id,))
    cursor.execute("DELETE FROM place WHERE id = %s", (place_id,))

    conn.commit()
    conn.close()
    return jsonify({'message': 'Place deleted successfully'}), 200

@place_bp.route('/admin/places/<int:place_id>/sectors', methods=['GET'])
def get_sectors(place_id):
    conn = get_db()
    cursor = conn.cursor(dictionary=True)
    cursor.execute("""
        SELECT id, name, x_sx, y_sx, x_dx, y_dx, is_stage
        FROM sector
        WHERE place_id = %s
    """, (place_id,))
    sectors = cursor.fetchall()

    for sector in sectors:
        cursor.execute("""
            SELECT id, description, x, y
            FROM seat
            WHERE sector_id = %s
        """, (sector['id'],))
        sector['seats'] = cursor.fetchall()

    conn.close()
    return jsonify(sectors), 200

@place_bp.route('/admin/places/<int:place_id>/sectors', methods=['POST'])
def save_sectors(place_id):
    data = request.get_json()

    conn = get_db()
    cursor = conn.cursor()

    cursor.execute("DELETE FROM seat WHERE sector_id IN (SELECT id FROM sector WHERE place_id = %s)", (place_id,))
    cursor.execute("DELETE FROM sector WHERE place_id = %s", (place_id,))
    
    for sector_data in data['sectors']:
        cursor.execute("""
            INSERT INTO sector (name, x_sx, y_sx, x_dx, y_dx, place_id, is_stage)
            VALUES (%s, %s, %s, %s, %s, %s, %s)
        """, (sector_data['name'], sector_data['x_sx'], sector_data['y_sx'], sector_data['x_dx'], sector_data['y_dx'], place_id, sector_data['is_stage']))
        
        sector_id = cursor.lastrowid
        
        for seat_data in sector_data['seats']:
            cursor.execute("""
                INSERT INTO seat (description, x, y, sector_id)
                VALUES (%s, %s, %s, %s)
            """, (seat_data.get('description', ''), seat_data['x'], seat_data['y'], sector_id))
    
    conn.commit()
    conn.close()
    return jsonify({'message': 'Mappa aggiornata con successo'}), 200

@place_bp.route('/admin/seats/<int:seat_id>', methods=['DELETE'])
def delete_seat(seat_id):
    conn = get_db()
    cursor = conn.cursor()
    cursor.execute("SELECT id FROM seat WHERE id = %s", (seat_id,))
    if not cursor.fetchone():
        conn.close()
        return jsonify({'message': 'Seat not found'}), 404

    cursor.execute("DELETE FROM seat WHERE id = %s", (seat_id,))
    conn.commit()
    conn.close()
    return jsonify({'message': 'Seat deleted'}), 200
