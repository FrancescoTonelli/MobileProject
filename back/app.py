from flask import Flask
from flask_cors import CORS
from routes.admin_routes import admin_bp
from routes.user_routes import user_bp
from routes.notification_routes import notification_bp

app = Flask(__name__)
CORS(app)

# Register blueprints
app.register_blueprint(admin_bp)
app.register_blueprint(user_bp)
app.register_blueprint(notification_bp)

if __name__ == '__main__':
    app.run(debug=True)
