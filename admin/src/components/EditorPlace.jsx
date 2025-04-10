import React, { useRef, useState, useEffect } from 'react';

const EditorPlace = ({ placeId, placeName }) => {
  const [placeData, setPlaceData] = useState(null);
  const canvasRef = useRef(null);

  const COLORS = {
    stage: '#8c78f9',
    sector: '#c0b6fc',
    seat: '#8c78f9',
    seatOutline: '#000000',
    text: '#000000',
    background: '#f0f0f0'
  };

  const drawCanvas = (context, placeData, translateX = 0, translateY = 0) => {
    context.clearRect(0, 0, context.canvas.width, context.canvas.height);
    context.fillStyle = COLORS.background;
    context.fillRect(0, 0, context.canvas.width, context.canvas.height);

    context.textAlign = 'center';
    context.textBaseline = 'bottom';
    context.font = '14px sans-serif';

    placeData.forEach((sector) => {
      context.fillStyle = sector.is_stage ? COLORS.stage : COLORS.sector;

      const x = sector.x_sx + translateX;
      const y = sector.y_sx + translateY;
      const width = sector.x_dx - sector.x_sx;
      const height = sector.y_dx - sector.y_sx;

      context.fillRect(x, y, width, height);

      context.fillStyle = COLORS.text;
      context.fillText(sector.name, x + width / 2, y - 6);

      if (sector.seats && Array.isArray(sector.seats)) {
        sector.seats.forEach((seat) => {
          context.beginPath();
          context.arc(seat.x + translateX, seat.y + translateY, 5, 0, Math.PI * 2);
          context.fillStyle = COLORS.seat;
          context.fill();
          context.lineWidth = 2;
          context.strokeStyle = COLORS.seatOutline;
          context.stroke();
        });
      }
    });
  };

  const calculateCanvasDimensions = (placeData) => {
    let minX = Infinity, maxX = -Infinity;
    let minY = Infinity, maxY = -Infinity;

    placeData.forEach((sector) => {
      minX = Math.min(minX, sector.x_sx);
      maxX = Math.max(maxX, sector.x_dx);
      minY = Math.min(minY, sector.y_sx);
      maxY = Math.max(maxY, sector.y_dx);

      if (sector.seats) {
        sector.seats.forEach((seat) => {
          minX = Math.min(minX, seat.x);
          maxX = Math.max(maxX, seat.x);
          minY = Math.min(minY, seat.y);
          maxY = Math.max(maxY, seat.y);
        });
      }
    });

    const padding = 50;
    const width = maxX - minX + padding * 2;
    const height = maxY - minY + padding * 2;

    return {
      width,
      height,
      translateX: -minX + padding,
      translateY: -minY + padding
    };
  };

  const fetchPlaceData = async () => {
    try {
      const response = await fetch(`http://localhost:5000/admin/places/${placeId}/sectors`);
      const data = await response.json();
      setPlaceData(data);
    } catch (error) {
      console.error('Errore nel recupero dei dati:', error);
    }
  };

  const handleFileUpload = (event) => {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = async () => {
        try {
          const json = JSON.parse(reader.result);
          setPlaceData(json);
          await updatePlaceData(json);
        } catch (error) {
          alert('Errore nel parsing del file JSON');
        }
      };
      reader.readAsText(file);
    }
  };

  const updatePlaceData = async (data) => {
    try {
      await fetch(`http://localhost:5000/admin/places/${placeId}/sectors`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ sectors: data })
      });
      alert('Mappa aggiornata con successo!');
    } catch (error) {
      alert('Errore nell\'aggiornamento dei dati nel database');
    }
  };

  useEffect(() => {
    if (placeId) {
      fetchPlaceData();
    }
  }, [placeId]);

  useEffect(() => {
    if (placeData && canvasRef.current) {
      const canvas = canvasRef.current;
      const context = canvas.getContext('2d');

      const { width, height, translateX, translateY } = calculateCanvasDimensions(placeData);
      canvas.width = width;
      canvas.height = height;

      drawCanvas(context, placeData, translateX, translateY);
    }
  }, [placeData]);

  return (
    <div className="notification-form" style={{ marginTop: '30px' }}>
      <h4>Seat Chart - {placeName}</h4>

      <div style={{ marginBottom: '20px' }}>
        <label htmlFor="fileUpload" className="my-file-input">
          Carica JSON
        </label>
        <input
          id="fileUpload"
          type="file"
          accept=".json"
          onChange={handleFileUpload}
          style={{ display: 'none' }}
        />
      </div>

      {placeData ? (
        <canvas
          ref={canvasRef}
          style={{
            border: '1px solid #000',
            marginTop: '20px',
            marginBottom: '40px'
          }}
        ></canvas>
      ) : (
        <p>Carica un file JSON per visualizzare la mappa dei posti.</p>
      )}
    </div>
  );
};

export default EditorPlace;
