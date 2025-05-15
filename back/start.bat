@echo off

pip install --upgrade pip
pip install flask flask-cors bcrypt PyJWT pillow mysql-connector-python werkzeug

python app.py
