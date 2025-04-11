import React, { useEffect, useState } from 'react';

const ViewConcerts = () => {
  const [concerts, setConcerts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedConcert, setSelectedConcert] = useState(null);

  const fetchConcerts = () => {
    setLoading(true);
    fetch('http://localhost:5000/admin/concerts/no-tour')
      .then((res) => res.json())
      .then((data) => {
        setConcerts(data);
        setLoading(false);
      })
      .catch(() => {
        alert('Error fetching concerts.');
        setLoading(false);
      });
  };

  const fetchConcertDetails = (id) => {
    fetch(`http://localhost:5000/admin/concerts/${id}`)
      .then((res) => res.json())
      .then((data) => {
        setSelectedConcert(data);
      })
      .catch(() => {
        alert('Error fetching concert details.');
      });
  };

  const deleteConcert = (id) => {
    if (window.confirm('Are you sure you want to delete this concert?')) {
      fetch(`http://localhost:5000/admin/concerts/${id}`, { method: 'DELETE' })
        .then((res) => {
          if (!res.ok) throw new Error();
          return res.json();
        })
        .then(() => {
          setConcerts((prev) => prev.filter((c) => c.id !== id));
          alert('Concert successfully deleted!');
          setSelectedConcert(null);
        })
        .catch(() => {
          alert('Error deleting the concert.');
        });
    }
  };

  useEffect(() => {
    fetchConcerts();
  }, []);

  if (loading) return <p>Loading concerts...</p>;

  return (
    <div className="body white-text">
      <div className="header-content">
        <h3>Concerts (No Tour)</h3>
        <button onClick={fetchConcerts} className="btn btn-primary nav-button">
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
                  <button className="btn btn-danger my-button" onClick={() => deleteConcert(concert.id)}>Delete</button>
                  <button className="btn btn-info my-button" onClick={() => fetchConcertDetails(concert.id)}>Details</button>
                </td>
              </tr>
            ))
          ) : (
            <tr><td colSpan="5">No concerts found</td></tr>
          )}
        </tbody>
      </table>

      {selectedConcert && (
        <div className="notification-form" style={{ marginTop: '30px' }}>
          <h4>Concert Details</h4>
          <form className="notification-form">
            <label className="notification-label">
              Image:
              <img className='concert-img' src={`http://localhost:5000/static/images/concerts/${selectedConcert.image}`} alt={selectedConcert.title} width="200" />
            </label>
            <label className="notification-label">
              Place:
              <input type="text" className="login-input" value={`${selectedConcert.place_name} - ${selectedConcert.address}`} readOnly />
            </label>
            <label className="notification-label">
              Record Company Email:
              <input type="text" className="login-input" value={selectedConcert.record_company_email || ''} readOnly />
            </label>
            <label className="notification-label">
              Artist(s):
              <ul>
                {selectedConcert.artists.length > 0 ? (
                  selectedConcert.artists.map((artist) => (
                    <li className='li-no-dot' key={artist.id}>
                      <img className='artist-img' src={`http://localhost:5000/static/images/artists/${ artist.image }`} alt={artist.name} width="50" /> {artist.name} 
                    </li>
                  ))
                ) : (
                  <li>No artists associated</li>
                )}
              </ul>
            </label>
          </form>
        </div>
      )}
    </div>
  );
};

export default ViewConcerts;
