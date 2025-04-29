from flask import Blueprint, request, jsonify
from db import get_db
from datetime import timedelta

protected_record_api = Blueprint('recordAPI', __name__)

# Login (token generation)
# Automatic Login via token
# Concerts Get by Record ID
    # Concert details (guarda schermata)
# Tours Get by Record ID
    # Tour details
# Artists Get by Record ID
    # Artist details
# Artists, Tours, Concerts Delete
    # Cancellando un artista, vanno cancellati anche i concerti e i tour in cui resta solo lui
    # Cancellando un tour, vanno cancellati anche i concerti delle tappe
    # Cancellando un concerto, vanno cancellati i biglietti con conseguente rimborso
# Event and Tour creation
# Place getter
# Ticket generetor with place prices
# Record data get and update
# Analytics (vedere schermata)
# validazioni biglietto
# Notification Get 