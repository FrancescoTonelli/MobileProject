from flask import Flask, request, jsonify
import mysql.connector
from config import DB_CONFIG
from flask_cors import CORS
import hashlib

app = Flask(__name__)
CORS(app)  

def get_db():
    return mysql.connector.connect(**DB_CONFIG)

@app.route('/tables', methods=['GET'])
def get_table_definitions():
    return jsonify(tables)

@app.route('/all', methods=['GET'])
def get_all_tables():
    conn = get_db()
    cursor = conn.cursor(dictionary=True)
    all_data = {}
    for table_name, columns in tables.items():
        cursor.execute(f"SELECT * FROM {table_name}")
        all_data[table_name] = cursor.fetchall()
    conn.close()
    return jsonify(all_data)


# 
# Funzione per il login dell'admin
# 
@app.route('/admin_login', methods=['POST'])
def admin_login():
    data = request.get_json()

    email = data.get('email')
    password = data.get('password')

    if not email or not password:
        return jsonify({'message': 'Email and password are required'}), 400

    conn = get_db()
    cursor = conn.cursor(dictionary=True)
    
    cursor.execute("SELECT * FROM ADMIN WHERE email = %s", (email,))
    user = cursor.fetchone()

    if user:
        stored_hashed_password = user.get('password')
        hashed_password = hashlib.sha256(password.encode()).hexdigest()
        if stored_hashed_password == hashed_password:
            conn.close()
            return jsonify({'message': 'Login successful'}), 200
        else:
            conn.close()
            return jsonify({'message': 'Invalid password'}), 401
    else:
        conn.close()
        return jsonify({'message': 'Invalid email'}), 401


def generate_endpoints(table_name, columns):
    # POST
    @app.route(f'/{table_name}', methods=['POST'], endpoint=f'add_entry_{table_name}')
    def add_entry(table_name=table_name, columns=columns):
        data = request.json
        placeholders = ', '.join(['%s'] * len(columns))
        cols = ', '.join(columns)
        values = tuple(data.get(col) for col in columns)
        conn = get_db()
        cursor = conn.cursor()
        cursor.execute(f"INSERT INTO {table_name} ({cols}) VALUES ({placeholders})", values)
        conn.commit()
        new_id = cursor.lastrowid
        conn.close()
        return jsonify({'id': new_id}), 201


tables = {
    'ADMIN': ['email', 'password'],
    'ARTIST': ['name', 'image', 'record_company_id'],
    'ARTIST_CONCERT': ['artist_id', 'concert_id'],
    'CONCERT': ['title', 'image', 'date', 'time', 'place_id', 'record_company_id', 'tour_id'],
    'LIKES': ['user_id', 'artist_id'],
    'NOTIFICATION': ['title', 'description', 'user_id', 'record_company_id'],
    'PLACE': ['name', 'address', 'email', 'telephone'],
    'RECORD_COMPANY': ['email', 'password', 'session_token'],
    'REVIEW': ['rate', 'description', 'user_id', 'concert_id'],
    'SEAT': ['description', 'x', 'y', 'sector_id'],
    'SECTOR': ['name', 'x_sx', 'y_sx', 'x_dx', 'y_dx', 'place_id'],
    'TICKET': ['price', 'validated', 'user_id', 'concert_id', 'seat_id'],
    'TOUR': ['title', 'image', 'record_company_id'],
    'USER': ['username', 'email', 'password', 'name', 'surname', 'birthdate', 'refunds', 'session_token']
}

for table, cols in tables.items():
    generate_endpoints(table, cols)

if __name__ == '__main__':
    app.run(debug=True)
