from flask import Flask
from flask_cors import CORS
from routes.admin_routes import admin_bp
from routes.user_routes import user_bp
from routes.notification_routes import notification_bp
from routes.record_routes import record_bp
from routes.artists_routes import artist_bp
from routes.places_routes import place_bp
from routes.concerts_routes import concert_bp
from routes.tours_routes import tour_bp

app = Flask(__name__)
CORS(app)

# Register blueprints
app.register_blueprint(admin_bp)
app.register_blueprint(user_bp)
app.register_blueprint(notification_bp)
app.register_blueprint(record_bp)
app.register_blueprint(artist_bp)
app.register_blueprint(place_bp)
app.register_blueprint(concert_bp)
app.register_blueprint(tour_bp)

if __name__ == '__main__':
    app.run(debug=True)
