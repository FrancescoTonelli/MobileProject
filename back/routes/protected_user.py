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
    if not all(k in data for k in required) or not ('email' in data or 'username' in data) or data['password'] == '':
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

# Logout (token deletion)
@protected_user_bp.route('/protected_user/logout', methods=['POST'])
def protected_user_logout():
    auth_header = request.headers.get('Authorization')
    if not auth_header:
        return jsonify({'message': 'Token is missing'}), 401
    
    try:
        user_id = verify_token()[0]
    except Exception as e:
        return jsonify({'message': 'Invalid token'}), 401

    conn = get_db()
    cursor = conn.cursor()

    try:
        cursor.execute("""
            UPDATE user 
            SET session_token = NULL 
            WHERE id = %s
        """, (user_id,))
        conn.commit()

        return jsonify({'message': 'Logout successful'}), 200

    except Exception as e:
        conn.rollback()
        return jsonify({'message': f'Server error: {str(e)}'}), 500
    finally:
        conn.close()

# Delete User
@protected_user_bp.route('/protected_user/delete', methods=['DELETE'])
def protected_user_delete_account():
    auth_header = request.headers.get('Authorization')
    if not auth_header:
        return jsonify({'message': 'Token is missing'}), 401

    try:
        user_id = verify_token()[0]
    except Exception:
        return jsonify({'message': 'Invalid token'}), 401

    conn = get_db()
    cursor = conn.cursor()

    try:
        cursor.execute("SELECT image FROM user WHERE id = %s", (user_id,))
        result = cursor.fetchone()
        if result and result[0]:
            image_path = result[0]
            full_image_path = os.path.join(current_app.root_path, 'static', 'images', 'users', image_path)
            if os.path.exists(full_image_path):
                os.remove(full_image_path)

        cursor.execute("DELETE FROM notification WHERE user_id = %s", (user_id,))
        cursor.execute("DELETE FROM likes WHERE user_id = %s", (user_id,))
        cursor.execute("DELETE FROM review WHERE user_id = %s", (user_id,))
        cursor.execute("UPDATE ticket SET user_id = NULL WHERE user_id = %s", (user_id,))
        cursor.execute("DELETE FROM user WHERE id = %s", (user_id,))

        conn.commit()
        return jsonify({'message': 'Account and image deleted successfully'}), 200

    except Exception as e:
        conn.rollback()
        return jsonify({'message': f'Server error: {str(e)}'}), 500
    finally:
        conn.close()

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
            concert.tour_id AS tour_id,
            place.name AS place_name,
            place.latitude AS place_latitude,
            place.longitude AS place_longitude,
            artist.name AS artist_name,
            artist.image AS artist_image
        FROM concert
        INNER JOIN place ON concert.place_id = place.id
        LEFT JOIN artist_concert ON concert.id = artist_concert.concert_id
        LEFT JOIN artist ON artist_concert.artist_id = artist.id
        WHERE concert.date >= CURDATE()
        GROUP BY concert.id
    """)

    concerts = cursor.fetchall()

    def calculate_distance(lat1, lon1, lat2, lon2):
        R = 6371
        phi1 = math.radians(lat1)
        phi2 = math.radians(lat2)
        delta_phi = math.radians(lat2 - lat1)
        delta_lambda = math.radians(lon2 - lon1)
        a = math.sin(delta_phi / 2.0) ** 2 + \
            math.cos(phi1) * math.cos(phi2) * math.sin(delta_lambda / 2.0) ** 2
        c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
        return R * c

    for concert in concerts:
        concert['distance'] = calculate_distance(user_lat, user_lon, concert['place_latitude'], concert['place_longitude'])

    nearest_concerts = sorted(concerts, key=lambda x: x['distance'])[:3]

    for concert in nearest_concerts:
        if not concert['tour_id']:
            concert['final_image'] = concert['concert_image']
            concert['tour_title'] = ""
        else:
            cursor.execute("SELECT image, title FROM tour WHERE id = %s", (concert['tour_id'],))
            tour = cursor.fetchone()
            concert['final_image'] = tour['image'] if tour and tour['image'] else ''
            concert['tour_title'] = tour['title'] if tour and tour['title'] else ""

    conn.close()

    response = []
    for concert in nearest_concerts:
        response.append({
            'id': concert['concert_id'],
            'title': concert['concert_title'],
            'tour_title': concert['tour_title'],
            'image': concert['final_image'],
            'artist': concert['artist_name'] if concert['artist_name'] else 'Unknown',
            'artist_image': concert['artist_image'] if concert['artist_image'] else '',
            'place_name': concert['place_name'] if concert['place_name'] else 'Unknown',
            'date': concert['concert_date'].strftime('%Y-%m-%d'),
            'distance': round(concert['distance'], 1)
        })

    return jsonify(response), 200


# Top 3 artists 
@protected_user_bp.route('/protected_user/popular_artists_events', methods=['POST'])
def protected_user_popular_artists_events():
    conn = get_db()
    cursor = conn.cursor(dictionary=True)

    cursor.execute("""
        SELECT a.id, a.name, a.image
        FROM artist a
        LEFT JOIN likes l ON a.id = l.artist_id
        WHERE EXISTS (
            SELECT 1
            FROM artist_concert ac
            JOIN concert c ON c.id = ac.concert_id
            WHERE ac.artist_id = a.id AND c.date >= CURDATE()
        )
        GROUP BY a.id
        ORDER BY COUNT(l.id) DESC
        LIMIT 3;
    """)
    top_artists = cursor.fetchall()

    response = []

    for artist in top_artists:
        artist_id = artist['id']
        artist_name = artist['name']
        artist_image = artist['image']

        cursor.execute("""
            SELECT c.id AS concert_id, c.title AS concert_title, c.image AS concert_image, 
                   DATE_FORMAT(c.date, '%Y-%m-%d') AS concert_date, c.place_id, c.tour_id, p.name AS place_name
            FROM concert c
            JOIN artist_concert ac ON c.id = ac.concert_id
            LEFT JOIN place p ON c.place_id = p.id
            WHERE ac.artist_id = %s AND c.date >= CURDATE()
            ORDER BY c.date ASC
            LIMIT 1;
        """, (artist_id,))
        concert = cursor.fetchone()

        if concert:
            if concert['tour_id']:
                cursor.execute("""
                    SELECT t.id, t.title, t.image,
                           (SELECT COUNT(*) FROM concert WHERE tour_id = t.id) AS concert_count
                    FROM tour t
                    WHERE t.id = %s;
                """, (concert['tour_id'],))
                tour = cursor.fetchone()

                response.append({
                    'id': tour['id'],
                    'isTour': True,
                    'title': tour['title'],
                    'image': tour['image'],
                    'artistName': artist_name,
                    'artistImage': artist_image,
                    'placeName': None,
                    'date': None,
                    'concertCount': tour['concert_count']
                })
            else:
                response.append({
                    'id': concert['concert_id'],
                    'isTour': False,
                    'title': concert['concert_title'],
                    'image': concert['concert_image'],
                    'artistName': artist_name,
                    'artistImage': artist_image,
                    'placeName': concert['place_name'],
                    'date': concert['concert_date'],
                    'concertCount': 0
                })

    conn.close()
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
            DATE_FORMAT(concert.date, '%Y-%m-%d') AS concert_date,
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
                t.id AS ticket_id,
                t.price AS ticket_price,
                s.id AS sector_id,
                s.name AS tour_name,  -- Corretto da sector_name a tour_name
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
            'concert_info': {
                **concert_data,  
                'effective_image': concert_data['concert_image'] or concert_data.get('tour_image'), 
                'is_part_of_tour': concert_data['tour_id'] is not None
            },
            'artists': artists,
            'available_tickets': available_tickets,
            'sectors': sectors
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
@protected_user_bp.route('/protected_user/liked_artists', methods=['GET'])
def protected_user_liked_artists():
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
                 WHERE ac.artist_id = a.id) AS average_rating
            FROM artist a
            JOIN likes l ON a.id = l.artist_id
            WHERE l.user_id = %s
            ORDER BY a.name
        """, (user_id,))
        
        artists = cursor.fetchall()

        for artist in artists:
            artist['average_rating'] = float(artist['average_rating'])

        return jsonify(artists), 200

    except Exception as e:
        return jsonify({'message': f'Server error: {str(e)}'}), 500
    finally:
        conn.close()

# Add or remove likes
@protected_user_bp.route('/protected_user/like/<int:artist_id>', methods=['POST'])
def protected_user_like_artist(artist_id):

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
            SELECT * FROM likes 
            WHERE artist_id = %s AND user_id = %s
        """, (artist_id, user_id))
        
        if cursor.fetchone():
            cursor.execute("""
                DELETE FROM likes 
                WHERE artist_id = %s AND user_id = %s
            """, (artist_id, user_id))
            conn.commit()
            return jsonify({'message': 'Artist unliked'}), 200
        else:
            cursor.execute("""
                INSERT INTO likes (user_id, artist_id) 
                VALUES (%s, %s)
            """, (user_id, artist_id))
            conn.commit()
            return jsonify({'message': 'Artist liked'}), 200

    except Exception as e:
        conn.rollback()
        return jsonify({'message': f'Error liking artist: {str(e)}'}), 500
    finally:
        conn.close()

@protected_user_bp.route('/protected_user/tickets', methods=['GET'])
def protected_user_tickets():
    auth_header = request.headers.get('Authorization')
    if not auth_header:
        return jsonify({'message': 'Token is missing'}), 401

    try:
        user_id = verify_token()[0]
    except Exception:
        return jsonify({'message': 'Invalid token'}), 401

    conn = get_db()
    cursor = conn.cursor(dictionary=True)

    try:
        cursor.execute("""
            SELECT 
                t.id AS ticket_id,
                c.title AS concert_title,
                COALESCE(c.image, tour.image) AS concert_image,
                DATE_FORMAT(c.date, '%Y-%m-%d') AS concert_date,
                tour.title AS tour_title,
                a.name AS artist_name,
                a.image AS artist_image,
                place.name AS place_name
            FROM 
                ticket t
            JOIN concert c ON t.concert_id = c.id
            LEFT JOIN tour ON c.tour_id = tour.id
            LEFT JOIN place ON c.place_id = place.id
            LEFT JOIN (
                SELECT ac.concert_id, a.id, a.name, a.image
                FROM artist_concert ac
                JOIN artist a ON ac.artist_id = a.id
                GROUP BY ac.concert_id
            ) a ON a.concert_id = c.id
            WHERE 
                t.user_id = %s
            ORDER BY 
                c.date DESC, c.time DESC
        """, (user_id,))
        rows = cursor.fetchall()

        formatted_tickets = []
        for row in rows:
            formatted_ticket = {
                'ticket_id': row['ticket_id'],
                'concert_title': row['concert_title'],
                'concert_image': row['concert_image'],
                'concert_date': row['concert_date'],
                'tour_title': row['tour_title'],
                'artist_name': row['artist_name'],
                'artist_image': row['artist_image'],
                'place_name': row['place_name']
            }
            formatted_tickets.append(formatted_ticket)

        return jsonify(formatted_tickets), 200

    except Exception as e:
        return jsonify({'message': f'Server error: {str(e)}'}), 500
    finally:
        conn.close()

# Get Ticket Details
@protected_user_bp.route('/protected_user/ticket/<int:ticket_id>', methods=['GET'])
def protected_user_ticket_detail(ticket_id):
    auth_header = request.headers.get('Authorization')
    if not auth_header:
        return jsonify({'message': 'Token is missing'}), 401

    try:
        user_id = verify_token()[0]
    except Exception:
        return jsonify({'message': 'Invalid token'}), 401

    conn = get_db()
    cursor = conn.cursor(dictionary=True)

    try:
        cursor.execute("""
            SELECT 
                t.id AS ticket_id,
                t.validated,
                c.id AS concert_id,
                c.title AS concert_title,
                COALESCE(c.image, tour.image) AS concert_image,
                DATE_FORMAT(c.date, '%Y-%m-%d') AS concert_date,
                TIME_FORMAT(c.time, '%H:%i') AS concert_time,
                tour.title AS tour_title,
                place.name AS place_name,
                place.address AS place_address,
                sector.name AS sector_name,
                seat.description AS seat_description
            FROM 
                ticket t
            JOIN concert c ON t.concert_id = c.id
            LEFT JOIN tour ON c.tour_id = tour.id
            LEFT JOIN place ON c.place_id = place.id
            LEFT JOIN seat ON t.seat_id = seat.id
            LEFT JOIN sector ON seat.sector_id = sector.id
            WHERE 
                t.id = %s AND t.user_id = %s
        """, (ticket_id, user_id))

        row = cursor.fetchone()

        if not row:
            return jsonify({'message': 'Ticket not found or unauthorized'}), 404

        ticket_info = {
            'ticket_id': row['ticket_id'],
            'validated': row['validated'],
            'concert_id': row['concert_id'],
            'concert_title': row['concert_title'],
            'concert_image': row['concert_image'],
            'concert_date': row['concert_date'],
            'concert_time': row['concert_time'],
            'tour_title': row['tour_title'],
            'place_name': row['place_name'],
            'place_address': row['place_address'],
            'sector_name': row['sector_name'],
            'seat_description': row['seat_description']
        }

        return jsonify(ticket_info), 200

    except Exception as e:
        return jsonify({'message': f'Server error: {str(e)}'}), 500
    finally:
        conn.close()

# Check and get existing review on a concert
@protected_user_bp.route('/protected_user/review/check/<int:concert_id>', methods=['GET'])
def protected_user_check_review(concert_id):
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
            SELECT id, rate, description 
            FROM review 
            WHERE user_id = %s AND concert_id = %s
            LIMIT 1
        """, (user_id, concert_id))
        
        review = cursor.fetchone()

        if review:
            return jsonify({
                'has_reviewed': True,
                'review': {
                    'id': review['id'],
                    'rate': review['rate'],
                    'description': review['description']
                }
            }), 200
        else:
            return jsonify({
                'has_reviewed': False
            }), 200

    except Exception as e:
        return jsonify({'message': f'Server error: {str(e)}'}), 500
    finally:
        conn.close()

# Post Review (ticket validated and concert passed)
@protected_user_bp.route('/protected_user/review', methods=['POST'])
def protected_user_add_review():
    auth_header = request.headers.get('Authorization')
    if not auth_header:
        return jsonify({'message': 'Token is missing'}), 401
    
    try:
        user_id = verify_token()[0]
    except Exception as e:
        return jsonify({'message': 'Invalid token'}), 401

    data = request.get_json()
    if not data or 'ticket_id' not in data or 'rate' not in data:
        return jsonify({'message': 'Missing required fields (ticket_id and rate are required)'}), 400

    ticket_id = data['ticket_id']
    rate = data['rate']
    description = data.get('description', '')

    if not isinstance(rate, int) or rate < 1 or rate > 5:
        return jsonify({'message': 'Rating must be an integer between 1 and 5'}), 400

    conn = get_db()
    cursor = conn.cursor(dictionary=True)

    try:
        cursor.execute("""
            SELECT t.id, t.concert_id, c.date 
            FROM ticket t
            JOIN concert c ON t.concert_id = c.id
            WHERE t.id = %s 
            AND t.user_id = %s
            AND t.validated = 1
            LIMIT 1
        """, (ticket_id, user_id))
        
        ticket = cursor.fetchone()
        
        if not ticket:
            return jsonify({'message': 'Ticket not found, not validated or not owned by user'}), 403

        concert_id = ticket['concert_id']
        
        if ticket['date'] >= datetime.date.today():
            return jsonify({'message': 'Cannot review future concerts'}), 403

        cursor.execute("""
            SELECT id FROM review 
            WHERE user_id = %s AND concert_id = %s
            LIMIT 1
        """, (user_id, concert_id))
        
        if cursor.fetchone():
            return jsonify({'message': 'You already reviewed this concert'}), 409

        cursor.execute("""
            INSERT INTO review (rate, description, user_id, concert_id)
            VALUES (%s, %s, %s, %s)
        """, (rate, description, user_id, concert_id))
        
        conn.commit()

        return jsonify({
            'message': 'Review added successfully',
        }), 201

    except Exception as e:
        conn.rollback()
        return jsonify({'message': f'Server error: {str(e)}'}), 500
    finally:
        conn.close()

# Get User Details
@protected_user_bp.route('/protected_user/details', methods=['GET'])
def protected_user_get_details():
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
                id,
                username,
                email,
                name,
                surname,
                DATE_FORMAT(birthdate, '%Y-%m-%d') AS birthdate,
                ROUND(refunds, 2) AS refunds,
                image
            FROM user
            WHERE id = %s
        """, (user_id,))
        
        user_data = cursor.fetchone()

        if not user_data:
            return jsonify({'message': 'User not found'}), 404

        return jsonify(user_data), 200

    except Exception as e:
        return jsonify({'message': f'Server error: {str(e)}'}), 500
    finally:
        conn.close()

# Update User Details
@protected_user_bp.route('/protected_user/update', methods=['PUT'])
def protected_user_update():
    auth_header = request.headers.get('Authorization')
    if not auth_header:
        return jsonify({'message': 'Token is missing'}), 401

    try:
        user_id = verify_token()[0]
    except Exception:
        return jsonify({'message': 'Invalid token'}), 401

    data = request.get_json()
    allowed_fields = ['name', 'surname', 'birthdate', 'username', 'email', 'password']

    updates = {k: data[k] for k in allowed_fields if k in data}
    if not updates:
        return jsonify({'message': 'No valid fields provided'}), 400

    if 'birthdate' in updates:
        try:
            birthdate = datetime.datetime.strptime(updates['birthdate'], '%Y-%m-%d').date()
            today = datetime.date.today()
            age = today.year - birthdate.year - ((today.month, today.day) < (birthdate.month, birthdate.day))
            if age < 18:
                return jsonify({'message': 'You must be at least 18 years old'}), 403
        except ValueError:
            return jsonify({'message': 'Invalid birthdate format. Use YYYY-MM-DD'}), 400

    if 'password' in updates:
        updates['password'] = bcrypt.hashpw(updates['password'].encode('utf-8'), bcrypt.gensalt()).decode('utf-8')

    set_clause = ', '.join(f"{key} = %s" for key in updates)
    values = list(updates.values())

    conn = get_db()
    cursor = conn.cursor(dictionary=True)

    try:
        cursor.execute(f"""
            UPDATE USER SET {set_clause} WHERE id = %s
        """, values + [user_id])
        conn.commit()
        return jsonify({'message': 'User updated successfully'}), 200
    except Exception as e:
        return jsonify({'message': f'Server error: {str(e)}'}), 500
    finally:
        conn.close()

# Update User Image
@protected_user_bp.route('/protected_user/update_image', methods=['PUT'])
def protected_user_update_image():
    auth_header = request.headers.get('Authorization')
    if not auth_header:
        return jsonify({'message': 'Token is missing'}), 401
    
    try:
        user_id = verify_token()[0]
    except Exception as e:
        return jsonify({'message': 'Invalid token'}), 401

    if 'image' not in request.files:
        return jsonify({'message': 'No image provided'}), 400

    file = request.files['image']
    if file.filename == '':
        return jsonify({'message': 'No selected file'}), 400

    try:
        Image.open(file.stream).verify()
        file.stream.seek(0)
    except Exception as e:
        return jsonify({'message': 'File is not a valid image'}), 400

    conn = get_db()
    cursor = conn.cursor(dictionary=True)

    try:
        cursor.execute("SELECT username, image FROM user WHERE id = %s", (user_id,))
        user_data = cursor.fetchone()
        
        if not user_data:
            return jsonify({'message': 'User not found'}), 404

        username = user_data['username']
        current_image = user_data['image']

        if current_image:
            old_image_path = os.path.join(current_app.root_path, 'static', 'images', 'users', current_image)
            if os.path.exists(old_image_path):
                os.remove(old_image_path)

        file_ext = os.path.splitext(file.filename)[1].lower()
        if not file_ext:
            file_ext = '.jpg'
        
        new_filename = f"{username}{file_ext}"
        save_path = os.path.join(current_app.root_path, 'static', 'images', 'users', new_filename)
        
        try:
            img = Image.open(file.stream)
            img.save(save_path)
        except Exception as e:
            return jsonify({'message': f'Error saving image: {str(e)}'}), 500

        cursor.execute("UPDATE user SET image = %s WHERE id = %s", (new_filename, user_id))
        conn.commit()

        return jsonify({
            'message': 'User image updated successfully',
            'image_path': new_filename
        }), 200

    except Exception as e:
        conn.rollback()
        current_app.logger.error(f"Error updating user image: {str(e)}")
        return jsonify({'message': f'Server error: {str(e)}'}), 500
    finally:
        conn.close()

# Get User Reviews
@protected_user_bp.route('/protected_user/reviews', methods=['GET'])
def protected_user_get_reviews():
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
                r.id AS review_id,
                r.rate AS rating,
                r.description AS comment,
                c.title AS concert_title,
                DATE_FORMAT(c.date, '%Y-%m-%d') AS concert_date,
                a.name AS artist_name,
                a.image AS artist_image
            FROM 
                review r
            JOIN 
                concert c ON r.concert_id = c.id
            JOIN 
                artist_concert ac ON c.id = ac.concert_id
            JOIN 
                artist a ON ac.artist_id = a.id
            WHERE 
                r.user_id = %s
            GROUP BY 
                r.id
            ORDER BY 
                c.date DESC
        """, (user_id,))
        
        reviews = cursor.fetchall()

        return jsonify(reviews), 200

    except Exception as e:
        return jsonify({'message': f'Server error: {str(e)}'}), 500
    finally:
        conn.close()

# Delete User Review
@protected_user_bp.route('/protected_user/review/delete/<int:review_id>', methods=['DELETE'])
def protected_user_delete_review(review_id):
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
            SELECT id FROM review 
            WHERE id = %s AND user_id = %s
            LIMIT 1
        """, (review_id, user_id))
        
        if not cursor.fetchone():
            return jsonify({'message': 'Review not found or not owned by user'}), 404

        cursor.execute("DELETE FROM review WHERE id = %s", (review_id,))
        conn.commit()

        return jsonify({'message': 'Review deleted successfully'}), 200

    except Exception as e:
        conn.rollback()
        return jsonify({'message': f'Server error: {str(e)}'}), 500
    finally:
        conn.close()

# Get all notifications
@protected_user_bp.route('/protected_user/notifications', methods=['GET'])
def protected_user_get_notifications():
    auth_header = request.headers.get('Authorization')
    if not auth_header:
        return jsonify({'message': 'Token is missing'}), 401
    
    try:
        user_id = verify_token()[0]
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
            user_id = %s
        ORDER BY id DESC
    """, (user_id,))

    notifications = cursor.fetchall()
    conn.close()

    return jsonify(notifications), 200

# Read notification
@protected_user_bp.route('/protected_user/notification/read/<int:notification_id>', methods=['POST'])
def protected_user_read_notification(notification_id):
    auth_header = request.headers.get('Authorization')
    if not auth_header:
        return jsonify({'message': 'Token is missing'}), 401
    
    try:
        user_id = verify_token()[0]
    except Exception:
        return jsonify({'message': 'Invalid token'}), 401

    conn = get_db()
    cursor = conn.cursor(dictionary=True)

    cursor.execute("""
        UPDATE notification 
        SET is_read = 1 
        WHERE id = %s AND user_id = %s
    """, (notification_id, user_id))

    conn.commit()
    conn.close()

    return jsonify({'message': 'Notification marked as read'}), 200