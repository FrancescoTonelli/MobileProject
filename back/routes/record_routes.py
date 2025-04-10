from flask import Blueprint, request, jsonify
import hashlib
from db import get_db

record_bp = Blueprint('record', __name__)

@record_bp.route('/admin/records', methods=['GET'])
def admin_get_records():
    conn = get_db()
    cursor = conn.cursor(dictionary=True)
    cursor.execute("SELECT * FROM RECORD_COMPANY")
    users = cursor.fetchall()
    conn.close()
    return jsonify(users), 200

@record_bp.route('/admin/records', methods=['POST'])
def admin_create_record():
    data = request.get_json()
    required = ['email', 'password']
    if not all(k in data for k in required):
        return jsonify({'message': 'Missing fields'}), 400

    hashed_password = hashlib.sha256(data['password'].encode()).hexdigest()

    conn = get_db()
    cursor = conn.cursor()
    cursor.execute(
        "INSERT INTO RECORD_COMPANY (email, password) VALUES (%s, %s)",
        (data['email'], hashed_password)
    )
    conn.commit()
    conn.close()
    return jsonify({'message': 'Record company created'}), 201

@record_bp.route('/admin/records/<int:company_id>', methods=['DELETE'])
def admin_delete_record(company_id):
    conn = get_db()
    cursor = conn.cursor()
    cursor.execute("SELECT id FROM RECORD_COMPANY WHERE id = %s", (company_id,))
    company = cursor.fetchone()
    if not company:
        conn.close()
        return jsonify({'message': 'Record company not found'}), 404
    
    cursor.execute("DELETE FROM RECORD_COMPANY WHERE id = %s", (company_id,))
    conn.commit()
    conn.close()
    return jsonify({'message': 'Record company deleted'}), 200
