from flask import Flask
from flask_cors import CORS
from routes.admin.admin_routes import admin_bp
from routes.admin.user_routes import user_bp
from routes.admin.notification_routes import notification_bp
from routes.admin.record_routes import record_bp
from routes.admin.artists_routes import artist_bp
from routes.admin.places_routes import place_bp
from routes.admin.concerts_routes import concert_bp
from routes.admin.tours_routes import tour_bp
from routes.protected_user import protected_user_bp

app = Flask(__name__)
CORS(app)

app.register_blueprint(admin_bp)
app.register_blueprint(user_bp)
app.register_blueprint(notification_bp)
app.register_blueprint(record_bp)
app.register_blueprint(artist_bp)
app.register_blueprint(place_bp)
app.register_blueprint(concert_bp)
app.register_blueprint(tour_bp)
app.register_blueprint(protected_user_bp)

if __name__ == '__main__':
    app.run(debug=True)
