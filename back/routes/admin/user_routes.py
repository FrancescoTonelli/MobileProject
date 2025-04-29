from flask import Blueprint, request, jsonify
import hashlib
from db import get_db

user_bp = Blueprint('user', __name__)

@user_bp.route('/admin/users', methods=['GET'])
def admin_get_users():
    conn = get_db()
    cursor = conn.cursor(dictionary=True)
    cursor.execute("SELECT * FROM USER")
    users = cursor.fetchall()
    conn.close()
    return jsonify(users), 200

@user_bp.route('/admin/users', methods=['POST'])
def admin_add_user():
    data = request.get_json()
    required = ['username', 'email', 'password', 'name', 'surname', 'birthdate']
    if not all(k in data for k in required):
        return jsonify({'message': 'Missing fields'}), 400

    hashed_password = hashlib.sha256(data['password'].encode()).hexdigest()
    conn = get_db()
    cursor = conn.cursor()
    cursor.execute("""
        INSERT INTO USER (username, email, password, name, surname, birthdate, refunds, session_token)
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
    """, (
        data['username'], data['email'], hashed_password, data['name'],
        data['surname'], data['birthdate'], data.get('refunds', 0), data.get('session_token')
    ))
    conn.commit()
    conn.close()
    return jsonify({'message': 'User added'}), 201

@user_bp.route('/admin/users/<int:user_id>', methods=['DELETE'])
def admin_delete_user_by_id(user_id):
    conn = get_db()
    cursor = conn.cursor()
    cursor.execute("DELETE FROM USER WHERE id = %s", (user_id,))
    conn.commit()
    conn.close()
    return jsonify({'message': f'User {user_id} deleted'}), 200
