from firebase_admin import messaging
from flask import current_app
from db import get_db 

def send_push_notification(dest_id: int, is_user: bool, title: str, body: str):
    conn = get_db()
    cursor = conn.cursor(dictionary=True)

    table_name = "USER" if is_user else "RECORD_COMPANY"

    cursor.execute(f"SELECT fcm_token FROM {table_name} WHERE id = %s", (dest_id,))
    result = cursor.fetchone()

    if not result or not result['fcm_token']:
        current_app.logger.warning(f"No FCM token found for {table_name} {dest_id}")
        return False

    fcm_token = result['fcm_token']

    message = messaging.Message(
        notification=messaging.Notification(
            title=title,
            body=body
        ),
        token=fcm_token
    )

    try:
        response = messaging.send(message)
        current_app.logger.info(f"Notification sent to {table_name} {dest_id}: {response}")
        return True
    except Exception as e:
        current_app.logger.error(f"Failed to send notification to user {table_name} {dest_id}: {e}")
        return False
