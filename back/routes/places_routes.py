from flask import Blueprint, request, jsonify
from db import get_db

place_bp = Blueprint('place', __name__)

# -------- PLACES -------- #

@place_bp.route('/admin/places', methods=['GET'])
def get_places():
    conn = get_db()
    cursor = conn.cursor(dictionary=True)
    cursor.execute("SELECT id, name, address, email, telephone FROM place")
    places = cursor.fetchall()
    conn.close()
    return jsonify(places), 200


@place_bp.route('/admin/places', methods=['POST'])
def create_place():
    data = request.get_json()
    name = data.get('name')
    address = data.get('address')
    email = data.get('email')
    telephone = data.get('telephone')

    if not all([name, address, email, telephone]):
        return jsonify({'message': 'Missing required fields'}), 400

    conn = get_db()
    cursor = conn.cursor()
    cursor.execute("""
        INSERT INTO place (name, address, email, telephone)
        VALUES (%s, %s, %s, %s)
    """, (name, address, email, telephone))
    conn.commit()
    conn.close()
    return jsonify({'message': 'Place created'}), 201


@place_bp.route('/admin/places/<int:place_id>', methods=['DELETE'])
def delete_place(place_id):
    conn = get_db()
    cursor = conn.cursor()

    # Check existence
    cursor.execute("SELECT id FROM place WHERE id = %s", (place_id,))
    if not cursor.fetchone():
        conn.close()
        return jsonify({'message': 'Place not found'}), 404

    # Get sector ids
    cursor.execute("SELECT id FROM sector WHERE place_id = %s", (place_id,))
    sector_ids = [row[0] for row in cursor.fetchall()]

    if sector_ids:
        cursor.executemany("DELETE FROM seat WHERE sector_id = %s", [(sid,) for sid in sector_ids])

    # Delete sectors
    cursor.execute("DELETE FROM sector WHERE place_id = %s", (place_id,))
    # Delete place
    cursor.execute("DELETE FROM place WHERE id = %s", (place_id,))
    conn.commit()
    conn.close()
    return jsonify({'message': 'Place deleted'}), 200

# -------- SECTORS -------- #

@place_bp.route('/admin/places/<int:place_id>/sectors', methods=['GET'])
def get_sectors(place_id):
    conn = get_db()
    cursor = conn.cursor(dictionary=True)
    cursor.execute("""
        SELECT id, name, x_sx, y_sx, x_dx, y_dx
        FROM sector
        WHERE place_id = %s
    """, (place_id,))
    sectors = cursor.fetchall()
    conn.close()
    return jsonify(sectors), 200


@place_bp.route('/admin/places/<int:place_id>/sectors', methods=['POST'])
def create_sector(place_id):
    data = request.get_json()
    name = data.get('name')
    x_sx = data.get('x_sx')
    y_sx = data.get('y_sx')
    x_dx = data.get('x_dx')
    y_dx = data.get('y_dx')

    conn = get_db()
    cursor = conn.cursor()
    cursor.execute("""
        INSERT INTO sector (name, x_sx, y_sx, x_dx, y_dx, place_id)
        VALUES (%s, %s, %s, %s, %s, %s)
    """, (name, x_sx, y_sx, x_dx, y_dx, place_id))
    conn.commit()
    conn.close()
    return jsonify({'message': 'Sector created'}), 201


@place_bp.route('/admin/sectors/<int:sector_id>', methods=['DELETE'])
def delete_sector(sector_id):
    conn = get_db()
    cursor = conn.cursor()

    # Check existence
    cursor.execute("SELECT id FROM sector WHERE id = %s", (sector_id,))
    if not cursor.fetchone():
        conn.close()
        return jsonify({'message': 'Sector not found'}), 404

    # Delete seats first
    cursor.execute("DELETE FROM seat WHERE sector_id = %s", (sector_id,))
    # Then delete sector
    cursor.execute("DELETE FROM sector WHERE id = %s", (sector_id,))
    conn.commit()
    conn.close()
    return jsonify({'message': 'Sector deleted'}), 200


# -------- SEATS -------- #

@place_bp.route('/admin/sectors/<int:sector_id>/seats', methods=['GET'])
def get_seats(sector_id):
    conn = get_db()
    cursor = conn.cursor(dictionary=True)
    cursor.execute("""
        SELECT id, description, x, y
        FROM seat
        WHERE sector_id = %s
    """, (sector_id,))
    seats = cursor.fetchall()
    conn.close()
    return jsonify(seats), 200


@place_bp.route('/admin/sectors/<int:sector_id>/seats', methods=['POST'])
def create_seat(sector_id):
    data = request.get_json()
    description = data.get('description')
    x = data.get('x')
    y = data.get('y')

    conn = get_db()
    cursor = conn.cursor()
    cursor.execute("""
        INSERT INTO seat (description, x, y, sector_id)
        VALUES (%s, %s, %s, %s)
    """, (description, x, y, sector_id))
    conn.commit()
    conn.close()
    return jsonify({'message': 'Seat created'}), 201


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
