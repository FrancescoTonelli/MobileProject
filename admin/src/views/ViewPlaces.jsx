import React, { useEffect, useState } from 'react';
import EditorPlace from '../components/EditorPlace';

const ViewPlaces = () => {
  const [places, setPlaces] = useState([]);
  const [loading, setLoading] = useState(true);
  const [newPlace, setNewPlace] = useState({
    name: '',
    address: '',
    email: '',
    telephone: '',
  });
  const [selectedPlace, setSelectedPlace] = useState(null);
  const [placeName, setPlaceName] = useState("");

  const fetchPlaces = () => {
    setLoading(true);
    fetch('http://localhost:5000/admin/places')
      .then((res) => res.json())
      .then((data) => {
        setPlaces(data);
        setLoading(false);
      })
      .catch((err) => {
        console.error('Error loading places:', err);
        setLoading(false);
      });
  };

  const deletePlace = (id) => {
    if (window.confirm('Are you sure you want to delete this place?')) {
      fetch(`http://localhost:5000/admin/places/${id}`, {
        method: 'DELETE',
      })
        .then((res) => {
          if (!res.ok) {
            throw new Error('Error deleting the place');
          }
          return res.json();
        })
        .then(() => {
          setPlaces((prevPlaces) => prevPlaces.filter((place) => place.id !== id));
          alert('Place successfully deleted!');
        })
        .catch((err) => {
          console.error('Error deleting:', err);
          alert('Error deleting the place');
        });
    }
  };

  const openEditor = (id, name) => {
    setSelectedPlace(id);
    setPlaceName(name);
  };

  const createPlace = () => {
    const { name, address, email, telephone } = newPlace;
    if (!name || !address || !email || !telephone) {
      alert('Fill in all fields');
      return;
    }

    fetch('http://localhost:5000/admin/places', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(newPlace),
    })
      .then((res) => {
        if (!res.ok) throw new Error('Creation failed');
        alert('Place successfully created!');
        setNewPlace({ name: '', address: '', email: '', telephone: '' });
        fetchPlaces();
      })
      .catch((err) => {
        console.error(err);
        alert('Error during creation');
      });
  };

  useEffect(() => {
    fetchPlaces();
  }, []);

  if (loading) return <p>Loading places...</p>;

  return (
    <div className="body white-text">
      <div className="header-content">
        <h3>Places</h3>
        <button onClick={fetchPlaces} className="btn btn-primary nav-button">
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
            <th>Address</th>
            <th>Email</th>
            <th>Telephone</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {places.map((place) => (
            <tr key={place.id}>
              <td>{place.id}</td>
              <td>{place.name}</td>
              <td>{place.address}</td>
              <td>{place.email}</td>
              <td>{place.telephone}</td>
              <td>
                <button
                  className="btn btn-danger my-button"
                  onClick={() => deletePlace(place.id)}
                >
                  Delete
                </button>
                <button
                  className="btn btn-info my-button"
                  onClick={() => openEditor(place.id, place.name)}
                >
                  Edit Map
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {selectedPlace && <EditorPlace placeId={selectedPlace} placeName={placeName} />}

      <div className="notification-form" style={{ marginTop: '30px' }}>
        <h4>Create Place</h4>
        <form
          onSubmit={(e) => {
            e.preventDefault();
            createPlace();
          }}
          className="notification-form"
        >
          <label className="notification-label">
            Name:
            <input
              type="text"
              className="login-input"
              value={newPlace.name}
              onChange={(e) =>
                setNewPlace({ ...newPlace, name: e.target.value })
              }
              required
            />
          </label>
          <br />
          <label className="notification-label">
            Address:
            <input
              type="text"
              className="login-input"
              value={newPlace.address}
              onChange={(e) =>
                setNewPlace({ ...newPlace, address: e.target.value })
              }
              required
            />
          </label>
          <br />
          <label className="notification-label">
            Email:
            <input
              type="email"
              className="login-input"
              value={newPlace.email}
              onChange={(e) =>
                setNewPlace({ ...newPlace, email: e.target.value })
              }
              required
            />
          </label>
          <br />
          <label className="notification-label">
            Telephone:
            <input
              type="text"
              className="login-input"
              value={newPlace.telephone}
              onChange={(e) =>
                setNewPlace({ ...newPlace, telephone: e.target.value })
              }
              required
            />
          </label>
          <br />
          <button type="submit" className="btn btn-success my-button">
            Create Place
          </button>
        </form>
      </div>
    </div>
  );
};

export default ViewPlaces;
