import React, { useEffect, useState } from 'react';

const ViewArtists = () => {
  const [artists, setArtists] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchArtists = () => {
    setLoading(true);
    fetch('http://localhost:5000/admin/artists')
      .then((res) => res.json())
      .then((data) => {
        setArtists(data);
        setLoading(false);
      })
      .catch((err) => {
        console.error('Error fetching artists:', err);
        setLoading(false);
      });
  };

  useEffect(() => {
    fetchArtists();
  }, []);

  const deleteArtist = (id) => {
    fetch(`http://localhost:5000/admin/artists/${id}`, {
      method: 'DELETE',
    })
      .then((res) => {
        if (!res.ok) throw new Error('Failed to delete');
        alert('Artist deleted');
        fetchArtists();
      })
      .catch((err) => {
        console.error(err);
        alert('Error deleting artist');
      });
  };

  if (loading) return <p>Loading...</p>;

  return (
    <div className="body white-text">
      <div className="header-content">
        <h3>Artists</h3>
        <button onClick={fetchArtists} className="btn btn-primary nav-button">
            <svg xmlns="http://www.w3.org/2000/svg" fill="currentColor" height="24px" width="24px" viewBox="0 0 489.533 489.533">
                <g>
                <path d="M268.175,488.161c98.2-11,176.9-89.5,188.1-187.7c14.7-128.4-85.1-237.7-210.2-239.1v-57.6c0-3.2-4-4.9-6.7-2.9   l-118.6,87.1c-2,1.5-2,4.4,0,5.9l118.6,87.1c2.7,2,6.7,0.2,6.7-2.9v-57.5c87.9,1.4,158.3,76.2,152.3,165.6   c-5.1,76.9-67.8,139.3-144.7,144.2c-81.5,5.2-150.8-53-163.2-130c-2.3-14.3-14.8-24.7-29.2-24.7c-17.9,0-31.9,15.9-29.1,33.6   C49.575,418.961,150.875,501.261,268.175,488.161z"/>
                </g>
            </svg>
        </button>
      </div>

      <table className="view-table" border="1">
        <thead className="bold-text">
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Image</th>
            <th>Record Company</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {artists.map((artist) => (
            <tr key={artist.id}>
              <td>{artist.id}</td>
              <td>{artist.name}</td>
              <td>
                <img
                  src={`http://localhost:5000/static/images/artists/${artist.image}`}
                  alt={artist.name}
                  className='artist-img'
                />
              </td>
              <td>{artist.company_email}</td>
              <td>
                <button
                  className="btn btn-danger my-button"
                  onClick={() => deleteArtist(artist.id)}
                >
                  Delete
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ViewArtists;
