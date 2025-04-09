from flask import Blueprint, request, jsonify
from db import get_db

notification_bp = Blueprint('notification', __name__)

@notification_bp.route('/admin/notify_user', methods=['POST'])
def admin_send_notification_user():
    data = request.get_json()
    required = ['title', 'description', 'user_id']
    if not all(k in data for k in required):
        return jsonify({'message': 'Missing fields'}), 400

    conn = get_db()
    cursor = conn.cursor()
    cursor.execute("""
        INSERT INTO NOTIFICATION (title, description, user_id, record_company_id)
        VALUES (%s, %s, %s, %s)
    """, (
        data['title'], data['description'], data['user_id'], data.get('record_company_id')
    ))
    conn.commit()
    conn.close()
    return jsonify({'message': 'Notification sent'}), 201

@notification_bp.route('/admin/notify_record', methods=['POST'])
def admin_send_notification_record():
    data = request.get_json()
    required = ['title', 'description', 'record_company_id']
    if not all(k in data for k in required):
        return jsonify({'message': 'Missing fields'}), 400

    conn = get_db()
    cursor = conn.cursor()
    cursor.execute("""
        INSERT INTO NOTIFICATION (title, description, user_id, record_company_id)
        VALUES (%s, %s, %s, %s)
    """, (
        data['title'], data['description'], data['user_id'], data.get('record_company_id')
    ))
    conn.commit()
    conn.close()
    return jsonify({'message': 'Notification sent'}), 201