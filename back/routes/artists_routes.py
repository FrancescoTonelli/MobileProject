from flask import Blueprint, request, jsonify
from db import get_db

artist_bp = Blueprint('artist', __name__)

# Endpoint per ottenere tutti gli artisti con le informazioni richieste
@artist_bp.route('/admin/artists', methods=['GET'])
def admin_get_artists():
    conn = get_db()
    cursor = conn.cursor(dictionary=True)
    cursor.execute("""
        SELECT artist.id, artist.name, artist.image, record_company.email AS company_email
        FROM artist
        JOIN record_company ON artist.record_company_id = record_company.id
    """)
    artists = cursor.fetchall()
    conn.close()
    return jsonify(artists), 200

# Endpoint per cancellare un artista
@artist_bp.route('/admin/artists/<int:artist_id>', methods=['DELETE'])
def admin_delete_artist(artist_id):
    conn = get_db()
    cursor = conn.cursor()
    cursor.execute("SELECT id FROM artist WHERE id = %s", (artist_id,))
    artist = cursor.fetchone()
    if not artist:
        conn.close()
        return jsonify({'message': 'Artist not found'}), 404
    
    cursor.execute("DELETE FROM artist WHERE id = %s", (artist_id,))
    conn.commit()
    conn.close()
    return jsonify({'message': 'Artist deleted'}), 200
