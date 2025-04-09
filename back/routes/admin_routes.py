from flask import Blueprint, request, jsonify
import hashlib
from db import get_db

admin_bp = Blueprint('admin', __name__)

@admin_bp.route('/admin/login', methods=['POST'])
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

    if user and user['password'] == hashlib.sha256(password.encode()).hexdigest():
        return jsonify({'message': 'Login successful'}), 200
    return jsonify({'message': 'Invalid email or password'}), 401
