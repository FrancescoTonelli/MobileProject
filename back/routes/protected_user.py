from flask import Blueprint, request, jsonify
from db import get_db
import bcrypt
import jwt
import datetime
from config import SECRET_FOR_TOKEN
import math
import json

protected_user_bp = Blueprint('protected_user', __name__)

def create_token(user_id):
    token = jwt.encode({
        'user_id': user_id
    }, SECRET_FOR_TOKEN, algorithm='HS256')

    conn = get_db()
    cursor = conn.cursor(dictionary=True)
    cursor.execute("UPDATE USER SET session_token = %s WHERE id = %s", (token, user_id))
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
        user_id = decoded_token['user_id']
    except jwt.ExpiredSignatureError:
        return jsonify({'message': 'Token has expired'}), 401
    except jwt.InvalidTokenError:
        return jsonify({'message': 'Invalid token'}), 401

    conn = get_db()
    cursor = conn.cursor(dictionary=True)
    cursor.execute("SELECT * FROM USER WHERE id = %s", (user_id,))
    user = cursor.fetchone()

    if not user or user['session_token'] != token:
        return jsonify({'message': 'Invalid token'}), 401

    conn.close()
    return user_id, 200

# Login (token generation)
@protected_user_bp.route('/protected_user/login', methods=['POST'])
def protected_user_login():
    data = request.get_json()
    required = ['password']
    if not all(k in data for k in required) or not ('email' in data or 'username' in data):
        return jsonify({'message': 'Missing fields'}), 400

    conn = get_db()
    cursor = conn.cursor(dictionary=True)

    if 'email' in data:
        cursor.execute("SELECT * FROM USER WHERE email = %s", (data['email'],))
    elif 'username' in data:
        cursor.execute("SELECT * FROM USER WHERE username = %s", (data['username'],))
    
    user = cursor.fetchone()

    if not user or not bcrypt.checkpw(data['password'].encode('utf-8'), user['password'].encode('utf-8')):
        return jsonify({'message': 'Invalid credentials'}), 401

    conn.commit()
    conn.close()

    token = create_token(user['id'])

    return jsonify({'token': token}), 200

# Register (token generation)
@protected_user_bp.route('/protected_user/register', methods=['POST'])
def protected_user_register():
    data = request.get_json()
    required = ['name', 'surname', 'birthdate', 'username', 'email', 'password']
    
    if not all(k in data for k in required):
        return jsonify({'message': 'Missing fields'}), 400

    try:
        birthdate = datetime.datetime.strptime(data['birthdate'], '%Y-%m-%d').date()
    except ValueError:
        return jsonify({'message': 'Invalid birthdate format. Use YYYY-MM-DD'}), 400

    today = datetime.date.today()
    age = today.year - birthdate.year - ((today.month, today.day) < (birthdate.month, birthdate.day))

    if age < 18:
        return jsonify({'message': 'You must be at least 18 years old to register'}), 403

    conn = get_db()
    cursor = conn.cursor(dictionary=True)

    cursor.execute("SELECT * FROM USER WHERE email = %s", (data['email'],))
    if cursor.fetchone():
        return jsonify({'message': 'Email already exists'}), 409
    
    cursor.execute("SELECT * FROM USER WHERE username = %s", (data['username'],))
    if cursor.fetchone():
        return jsonify({'message': 'Username already exists'}), 409

    hashed_password = bcrypt.hashpw(data['password'].encode('utf-8'), bcrypt.gensalt()).decode('utf-8')

    cursor.execute("""
        INSERT INTO USER (username, email, password, name, surname, birthdate, refunds)
        VALUES (%s, %s, %s, %s, %s, %s, %s)
    """, (
        data['username'], data['email'], hashed_password,
        data['name'], data['surname'], data['birthdate'], "0"
    ))

    user_id = cursor.lastrowid

    conn.commit()
    conn.close()

    token = create_token(user_id)

    return jsonify({'token': token}), 201

# Automatic Login via token
@protected_user_bp.route('/protected_user/automatic_login', methods=['POST'])
def protected_user_automatic_login():
    user_id, status_code = verify_token()
    if status_code != 200:
        return jsonify({'message': 'Token is invalid'}), 401

    conn = get_db()
    cursor = conn.cursor(dictionary=True)
    cursor.execute("SELECT * FROM USER WHERE id = %s", (user_id,))
    user = cursor.fetchone()

    if not user:
        return jsonify({'message': 'User not found'}), 404

    conn.commit()
    conn.close()

    token = create_token(user['id'])

    return jsonify({'token': token}), 200

# First three nearest concerts
@protected_user_bp.route('/protected_user/nearest_concerts', methods=['POST'])
def protected_user_nearest_concerts():

    data = request.get_json()
    required = ['latitude', 'longitude']
    if not all(k in data for k in required):
        return jsonify({'message': 'Missing fields'}), 400

    user_lat = data['latitude']
    user_lon = data['longitude']

    conn = get_db()
    cursor = conn.cursor(dictionary=True)

    cursor.execute("""
        SELECT 
            concert.id AS concert_id,
            concert.title AS concert_title,
            concert.image AS concert_image,
            concert.date AS concert_date,
            place.name AS place_name,
            place.latitude AS place_latitude,
            place.longitude AS place_longitude,
            tour.title AS tour_title,
            tour.image AS tour_image,
            artist.name AS artist_name,
            artist.image AS artist_image
        FROM concert
        INNER JOIN place ON concert.place_id = place.id
        LEFT JOIN tour ON concert.tour_id = tour.id
        LEFT JOIN artist_concert ON concert.id = artist_concert.concert_id
        LEFT JOIN artist ON artist_concert.artist_id = artist.id
        WHERE concert.date >= CURDATE()
        GROUP BY concert.id
    """)

    concerts = cursor.fetchall()
    conn.close()

    def calculate_distance(lat1, lon1, lat2, lon2):
        R = 6371 
        phi1 = math.radians(lat1)
        phi2 = math.radians(lat2)
        delta_phi = math.radians(lat2 - lat1)
        delta_lambda = math.radians(lon2 - lon1)

        a = math.sin(delta_phi / 2.0) ** 2 + \
            math.cos(phi1) * math.cos(phi2) * \
            math.sin(delta_lambda / 2.0) ** 2
        c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))

        return R * c

    concert_list = []
    for concert in concerts:
        distance = calculate_distance(user_lat, user_lon, concert['place_latitude'], concert['place_longitude'])
        concert_list.append({
            'id': concert['concert_id'],
            'title': concert['concert_title'],
            'tour_title': concert['tour_title'],
            'image': concert['concert_image'] if concert['concert_image'] else concert['tour_image'],
            'artist': concert['artist_name'],
            'artist_image': concert['artist_image'],
            'place_name': concert['place_name'],
            'date': concert['concert_date'].strftime('%Y-%m-%d'),
            'distance': round(distance, 1)
        })

    concert_list = sorted(concert_list, key=lambda x: x['distance'])[:3]

    return jsonify(concert_list), 200

# Top 3 artists 
@protected_user_bp.route('/protected_user/popular_artists_events', methods=['POST'])
def protected_user_popular_artists_events():

    conn = get_db()
    cursor = conn.cursor(dictionary=True)

    cursor.execute("""
        SELECT 
            a.id AS artist_id,
            a.name AS artist_name,
            a.image AS artist_image,
            CASE 
                WHEN t.id IS NOT NULL THEN 
                    JSON_OBJECT(
                        'type', 'tour',
                        'id', t.id,
                        'title', t.title,
                        'image', t.image,
                        'concert_count', (SELECT COUNT(*) FROM concert WHERE tour_id = t.id)
                    )
                WHEN c.id IS NOT NULL THEN 
                    JSON_OBJECT(
                        'type', 'concert',
                        'id', c.id,
                        'title', c.title,
                        'image', c.image,
                        'date', c.date,
                        'place_name', p.name
                    )
                ELSE 
                    JSON_OBJECT('type', 'no_events')
            END AS event_data
        FROM 
            (SELECT a.* 
             FROM artist a
             LEFT JOIN likes l ON a.id = l.artist_id
             GROUP BY a.id
             ORDER BY COUNT(l.id) DESC
             LIMIT 3) AS a
        LEFT JOIN 
            (SELECT ac.artist_id, c.*
             FROM concert c
             JOIN artist_concert ac ON c.id = ac.concert_id
             WHERE c.date >= CURDATE()
             ORDER BY c.date) AS c ON a.id = c.artist_id
        LEFT JOIN 
            tour t ON c.tour_id = t.id
        LEFT JOIN 
            place p ON c.place_id = p.id
        GROUP BY 
            a.id
        ORDER BY 
            (SELECT COUNT(*) FROM likes WHERE artist_id = a.id) DESC;
    """)

    results = cursor.fetchall()
    conn.close()

    response = []
    for row in results:
        event_data = json.loads(row['event_data']) if row['event_data'] else None
        
        item = {
            'artist_id': row['artist_id'],
            'artist_name': row['artist_name'],
            'artist_image': row['artist_image'],
            'event': event_data
        }
        
        if event_data and event_data['type'] != 'no_events':
            event_data['artist_name'] = row['artist_name']
            event_data['artist_image'] = row['artist_image']
        
        response.append(item)

    return jsonify(response), 200

# Get Artists
@protected_user_bp.route('/protected_user/artists', methods=['POST'])
def protected_user_artists():

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
        GROUP BY 
            a.id, a.name, a.image;

    """)

    artists = cursor.fetchall()
    conn.close()

    return jsonify(artists), 200

# Get Concerts without Tour
@protected_user_bp.route('/protected_user/concerts_no_tour', methods=['POST'])
def protected_user_concerts_no_tour():

    conn = get_db()
    cursor = conn.cursor(dictionary=True)

    cursor.execute("""
        SELECT 
            concert.id AS concert_id,
            concert.title AS concert_title,
            concert.image AS concert_image,
            concert.date AS concert_date,
            place.name AS place_name,
            artist.name AS artist_name,
            artist.image AS artist_image
        FROM concert
        INNER JOIN place ON concert.place_id = place.id
        LEFT JOIN artist_concert ON concert.id = artist_concert.concert_id
        LEFT JOIN artist ON artist_concert.artist_id = artist.id
        WHERE concert.date >= CURDATE() AND concert.tour_id IS NULL
        GROUP BY concert.id
    """)

    concerts = cursor.fetchall()
    conn.close()

    return jsonify(concerts), 200

# Get Concerts for map
@protected_user_bp.route('/protected_user/map_concerts', methods=['POST'])
def protected_user_map_concerts():

    conn = get_db()
    cursor = conn.cursor(dictionary=True)

    cursor.execute("""
        SELECT 
            c.id AS concert_id,
            (
                SELECT a.image 
                FROM artist a
                JOIN artist_concert ac ON a.id = ac.artist_id
                WHERE ac.concert_id = c.id
                LIMIT 1
            ) AS artist_image,
            p.latitude AS place_latitude,
            p.longitude AS place_longitude
        FROM 
            concert c
        JOIN 
            place p ON c.place_id = p.id
        WHERE 
            c.date >= CURDATE();
    """)

    concerts = cursor.fetchall()
    conn.close()

    return jsonify(concerts), 200

# Get Tours
@protected_user_bp.route('/protected_user/tours', methods=['POST'])
def protected_user_tours():
    conn = get_db()
    cursor = conn.cursor(dictionary=True)

    cursor.execute("""
        SELECT 
            t.id AS tour_id,
            t.title AS tour_title,
            t.image AS tour_image,
            a.name AS artist_name,
            a.image AS artist_image,
            COUNT(c.id) AS concert_count,
            SUM(CASE WHEN c.date >= CURDATE() THEN 1 ELSE 0 END) AS upcoming_concerts
        FROM 
            tour t
        JOIN 
            concert c ON t.id = c.tour_id
        JOIN 
            artist_concert ac ON c.id = ac.concert_id
        JOIN 
            artist a ON ac.artist_id = a.id
        WHERE 
            EXISTS (
                SELECT 1 FROM concert 
                WHERE tour_id = t.id 
                AND date >= CURDATE()
            )
        GROUP BY 
            t.id, t.title, t.image, a.name, a.image
        ORDER BY 
            t.title;
    """)

    tours = cursor.fetchall()
    conn.close()

    return jsonify(tours), 200

# Get Tour Details
@protected_user_bp.route('/protected_user/tour/<int:tour_id>', methods=['GET'])
def protected_user_tour_details(tour_id):
    conn = get_db()
    cursor = conn.cursor(dictionary=True)

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
            c.image AS concert_image,
            DATE_FORMAT(c.date, '%Y-%m-%d') AS concert_date,
            TIME_FORMAT(c.time, '%H:%i') AS concert_time_str,
            p.name AS place_name,
            p.address AS place_address,
            p.latitude AS place_latitude,
            p.longitude AS place_longitude
        FROM 
            concert c
        JOIN 
            place p ON c.place_id = p.id
        WHERE 
            c.tour_id = %s
            AND c.date >= CURDATE()
        ORDER BY 
            c.date, c.time
    """, (tour_id,))
    
    concerts = cursor.fetchall()

    conn.close()

    response = {
        'artists': artists,
        'concerts': concerts
    }

    return jsonify(response), 200

# Get Concert Details
@protected_user_bp.route('/protected_user/concert/<int:concert_id>', methods=['GET'])
def protected_user_concert_details(concert_id):
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
                p.id AS place_id
            FROM 
                concert c
            JOIN 
                place p ON c.place_id = p.id
            WHERE 
                c.id = %s
        """, (concert_id,))
        
        concert_data = cursor.fetchone()
        if not concert_data:
            jsonify({'message': 'Concert not found'}), 404

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
                t.id AS ticket_id,
                t.price AS ticket_price,
                s.id AS sector_id,
                s.name AS sector_name,
                s.is_stage AS sector_is_stage,
                s.x_sx, s.y_sx, s.x_dx, s.y_dx,
                se.id AS seat_id,
                se.description AS seat_description,
                se.x AS seat_x,
                se.y AS seat_y
            FROM 
                ticket t
            JOIN 
                seat se ON t.seat_id = se.id
            JOIN 
                sector s ON se.sector_id = s.id
            WHERE 
                t.concert_id = %s
                AND t.user_id IS NULL
                AND t.validated = 0
        """, (concert_id,))
        
        available_tickets = cursor.fetchall()

        cursor.execute("""
            SELECT 
                id,
                name,
                is_stage,
                x_sx,
                y_sx,
                x_dx,
                y_dx
            FROM 
                sector
            WHERE 
                place_id = %s
        """, (concert_data['place_id'],))
        
        sectors = cursor.fetchall()

        response = {
            'concert_info': concert_data,
            'artists': artists,
            'available_tickets': available_tickets,
            'sectors': sectors
        }

        return jsonify(response), 200

    except Exception as e:
        return jsonify({'message': f'Server error: {str(e)}'}), 500
    finally:
        conn.close()

# Get Artist Details
@protected_user_bp.route('/protected_user/artist/<int:artist_id>', methods=['GET'])
def protected_user_artist_details(artist_id):
    auth_header = request.headers.get('Authorization')
    if not auth_header:
        return jsonify({'message': 'Token is missing'}), 401
    
    try:
        user_id = verify_token()[0]
    except Exception as e:
        return jsonify({'message': 'Invalid token'}), 401

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
                 WHERE ac.artist_id = a.id) AS average_rating,
                EXISTS(SELECT 1 FROM likes WHERE artist_id = a.id AND user_id = %s) AS is_liked
            FROM artist a
            WHERE a.id = %s
        """, (user_id, artist_id))
        
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
                'average_rating': float(artist_data['average_rating']),
                'is_liked': bool(artist_data['is_liked'])
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

# Buy ticket by id
@protected_user_bp.route('/protected_user/ticket/purchase/<int:ticket_id>', methods=['POST'])
def protected_user_purchase_ticket(ticket_id):
    auth_header = request.headers.get('Authorization')
    if not auth_header:
        return jsonify({'message': 'Token is missing'}), 401
    
    try:
        user_id = verify_token()[0]
    except Exception as e:
        return jsonify({'message': 'Invalid token'}), 401

    conn = get_db()
    cursor = conn.cursor(dictionary=True)

    try:
        cursor.execute("""
            SELECT id FROM ticket 
            WHERE id = %s 
            AND user_id IS NULL 
            AND validated = 0
        """, (ticket_id,))
        
        if not cursor.fetchone():
            return jsonify({'message': 'Ticket not available'}), 400

        cursor.execute("""
            UPDATE ticket 
            SET user_id = %s 
            WHERE id = %s
        """, (user_id, ticket_id))
        
        conn.commit()

        return jsonify({
            'message': 'Purchase successful',
        }), 200

    except Exception as e:
        conn.rollback()
        return jsonify({'message': f'Error during purchase: {str(e)}'}), 500
    finally:
        conn.close()

# Get Liked Artists