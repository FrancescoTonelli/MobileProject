import React, { useEffect, useState, useRef } from 'react';
import { Stage, Layer, Rect, Text } from 'react-konva';

const EditorPlace = ({ placeId }) => {
  const [sectors, setSectors] = useState([]);
  const [newSectorData, setNewSectorData] = useState(null); // Coordinate del nuovo settore
  const [isSelecting, setIsSelecting] = useState(false); // Stato per selezionare il settore
  const stageRef = useRef();

  // Caricamento dei settori
  useEffect(() => {
    if (!placeId) return;

    fetch(`http://localhost:5000/admin/sectors?place_id=${placeId}`)
      .then((res) => res.json())
      .then((data) => setSectors(data));
  }, [placeId]);

  // Gestione dei click sulla canvas per creare un nuovo settore
  const handleStageClick = (e) => {
    if (isSelecting) {
      if (!newSectorData) {
        // Primo clic: imposta la posizione di partenza
        setNewSectorData({
          x: e.evt.offsetX,
          y: e.evt.offsetY,
        });
        setIsSelecting(false); // Aspetta il secondo clic
      } else {
        // Secondo clic: crea un nuovo settore
        const width = e.evt.offsetX - newSectorData.x;
        const height = e.evt.offsetY - newSectorData.y;
        const newSector = {
          place_id: placeId,
          name: `Sector ${sectors.length + 1}`,
          x_sx: newSectorData.x,
          y_sx: newSectorData.y,
          x_dx: e.evt.offsetX,
          y_dx: e.evt.offsetY,
        };

        // Salvataggio del nuovo settore nel database
        fetch('http://localhost:5000/admin/sectors', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(newSector),
        })
          .then((res) => res.json())
          .then((created) => {
            setSectors((prev) => [...prev, created]); // Aggiungi il settore alla lista
            setNewSectorData(null); // Reset per il nuovo settore
          });
      }
    }
  };

  // Gestire il click destro per eliminare un settore
  const handleRightClick = (e) => {
    e.evt.preventDefault();
    const mousePos = { x: e.evt.offsetX, y: e.evt.offsetY };

    // Trova il settore più vicino al click
    const closestSector = sectors.reduce((closest, sector) => {
      const sectorCenter = {
        x: (sector.x_sx + sector.x_dx) / 2,
        y: (sector.y_sx + sector.y_dx) / 2,
      };
      const distance = Math.hypot(sectorCenter.x - mousePos.x, sectorCenter.y - mousePos.y);
      if (!closest || distance < closest.distance) {
        return { sector, distance };
      }
      return closest;
    }, null);

    // Se il settore è abbastanza vicino, chiedi conferma per eliminarlo
    if (closestSector && closestSector.distance < 20) {
      if (window.confirm(`Vuoi eliminare il settore "${closestSector.sector.name}"?`)) {
        // Elimina il settore dal database
        fetch(`http://localhost:5000/admin/sectors/${closestSector.sector.id}`, {
          method: 'DELETE',
        })
          .then(() => setSectors((prev) => prev.filter((s) => s.id !== closestSector.sector.id)));
      }
    }
  };

  return (
    <div className="body white-text" style={{ marginBottom: '30px' }}>
      <h2>Editor Mappa - {placeId}</h2>

      <Stage
        width={1000}
        height={600}
        ref={stageRef}
        style={{ background: '#f0f0f0', marginTop: '20px' }}
        onClick={handleStageClick}
        onContextMenu={handleRightClick}
      >
        <Layer>
          {/* Disegna i settori */}
          {sectors.map((sector) => (
            <React.Fragment key={sector.id}>
              <Rect
                x={sector.x_sx}
                y={sector.y_sx}
                width={sector.x_dx - sector.x_sx}
                height={sector.y_dx - sector.y_sx}
                fill="lightblue"
                stroke="black"
                strokeWidth={1}
              />
              <Text
                x={sector.x_sx + 5}
                y={sector.y_sx + 5}
                text={sector.name}
                fontSize={14}
                fill="black"
              />
            </React.Fragment>
          ))}
        </Layer>
      </Stage>

      <button
        className="btn btn-success my-button"
        onClick={() => setIsSelecting(!isSelecting)}
      >
        {isSelecting ? 'Annulla Creazione Settore' : 'Crea Settore'}
      </button>
    </div>
  );
};

export default EditorPlace;
