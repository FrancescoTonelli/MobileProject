import React, { useEffect, useState } from 'react';

const ViewTours = () => {
  const [tours, setTours] = useState([]);
  const [selectedTour, setSelectedTour] = useState(null);
  const [concerts, setConcerts] = useState([]);
  const [selectedConcert, setSelectedConcert] = useState(null);

  const fetchTours = () => {
    fetch('http://localhost:5000/admin/tours')
      .then((res) => res.json())
      .then(setTours)
      .catch(() => alert('Errore nel recupero dei tour.'));
  };

  const deleteTour = (id) => {
    if (window.confirm('Vuoi davvero eliminare questo tour e i relativi concerti?')) {
      fetch(`http://localhost:5000/admin/tours/${id}`, { method: 'DELETE' })
        .then((res) => {
          if (!res.ok) throw new Error();
          setTours((prev) => prev.filter((t) => t.id !== id));
          if (selectedTour?.id === id) {
            setSelectedTour(null);
            setConcerts([]);
            setSelectedConcert(null);
          }
          alert('Tour eliminato con successo!');
        })
        .catch(() => alert('Errore nella cancellazione del tour.'));
    }
  };

  const fetchConcerts = (tour) => {
    setSelectedTour(tour);
    fetch(`http://localhost:5000/admin/tours/${tour.id}/concerts`)
      .then((res) => res.json())
      .then(setConcerts)
      .catch(() => alert('Errore nel recupero delle tappe.'));
  };

  const deleteConcert = (id) => {
    if (window.confirm('Vuoi davvero eliminare questa tappa?')) {
      fetch(`http://localhost:5000/admin/concerts/${id}`, { method: 'DELETE' })
        .then(() => {
          setConcerts((prev) => prev.filter((c) => c.id !== id));
          setSelectedConcert(null);
          alert('Tappa eliminata con successo!');
        })
        .catch(() => alert('Errore nella cancellazione della tappa.'));
    }
  };

  const fetchConcertDetails = (id) => {
    fetch(`http://localhost:5000/admin/concerts/${id}`)
      .then((res) => res.json())
      .then(setSelectedConcert)
      .catch(() => alert('Errore nel recupero dei dettagli del concerto.'));
  };

  useEffect(() => {
    fetchTours();
  }, []);

  return (
    <div className="body white-text">
      <div className="header-content">
        <h3>Tour</h3>
        <button onClick={fetchTours} className="btn btn-primary nav-button">
        <svg
            xmlns="http://www.w3.org/2000/svg"
            fill="currentColor"
            height="24px"
            width="24px"
            viewBox="0 0 489.533 489.533"
          >
            <path
              d="M268.175,488.161c98.2-11,176.9-89.5,188.1-187.7c14.7-128.4-85.1-237.7-210.2-239.1v-57.6c0-3.2-4-4.9-6.7-2.9
              l-118.6,87.1c-2,1.5-2,4.4,0,5.9l118.6,87.1c2.7,2,6.7,0.2,6.7-2.9v-57.5c87.9,1.4,158.3,76.2,152.3,165.6
              c-5.1,76.9-67.8,139.3-144.7,144.2c-81.5,5.2-150.8-53-163.2-130c-2.3-14.3-14.8-24.7-29.2-24.7c-17.9,0-31.9,15.9-29.1,33.6
              C49.575,418.961,150.875,501.261,268.175,488.161z"
            />
          </svg>
        </button>
      </div>

      <table className="view-table" border="1">
        <thead className="bold-text">
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {tours.length > 0 ? (
            tours.map((tour) => (
              <tr key={tour.id}>
                <td>{tour.id}</td>
                <td>{tour.title}</td>
                <td>
                  <button className="btn btn-danger my-button" onClick={() => deleteTour(tour.id)}>Elimina</button>
                  <button className="btn btn-info my-button" onClick={() => fetchConcerts(tour)}>Visualizza Tappe</button>
                </td>
              </tr>
            ))
          ) : (
            <tr><td colSpan="3">Nessun tour trovato</td></tr>
          )}
        </tbody>
      </table>

      {selectedTour && (
        <>
          <h4 style={{ marginTop: '30px' }}>Tappe di: {selectedTour.name}</h4>
          <table className="view-table" border="1">
            <thead className="bold-text">
              <tr>
                <th>ID</th>
                <th>Title</th>
                <th>Date</th>
                <th>Time</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {concerts.length > 0 ? (
                concerts.map((concert) => (
                  <tr key={concert.id}>
                    <td>{concert.id}</td>
                    <td>{concert.title}</td>
                    <td>{concert.date}</td>
                    <td>{concert.time}</td>
                    <td>
                      <button className="btn btn-danger my-button" onClick={() => deleteConcert(concert.id)}>Elimina</button>
                      <button className="btn btn-info my-button" onClick={() => fetchConcertDetails(concert.id)}>Dettagli</button>
                    </td>
                  </tr>
                ))
              ) : (
                <tr><td colSpan="5">Nessuna tappa trovata</td></tr>
              )}
            </tbody>
          </table>
        </>
      )}

      {selectedConcert && (
        <div className="notification-form" style={{ marginTop: '30px' }}>
          <h4>Dettagli Tappa</h4>
          <form className="notification-form">
            <label className="notification-label">
              Immagine Tour:
              <img className="concert-img" src={`http://localhost:5000/static/images/tours/${selectedTour.image}`} alt={selectedTour.name} width="200" />
            </label>
            <label className="notification-label">
              Luogo:
              <input type="text" className="login-input" value={`${selectedConcert.place_name || ''} - ${selectedConcert.address || ''}`} readOnly />
            </label>
            <label className="notification-label">
              Email Etichetta:
              <input type="text" className="login-input" value={selectedConcert.record_company_email || ''} readOnly />
            </label>
            <label className="notification-label">
              Artisti:
              <ul>
                {selectedConcert.artists.length > 0 ? (
                  selectedConcert.artists.map((artist) => (
                    <li className="li-no-dot" key={artist.id}>
                      <img className="artist-img" src={`http://localhost:5000/static/images/artists/${artist.image}`} alt={artist.name} width="50" /> {artist.name}
                    </li>
                  ))
                ) : (
                  <li>Nessun artista associato</li>
                )}
              </ul>
            </label>
          </form>
        </div>
      )}
    </div>
  );
};

export default ViewTours;
