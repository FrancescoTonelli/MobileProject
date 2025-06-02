import React, { useEffect, useState } from "react";
import "../index.css";

const ArtistSelector = ({ selectedCompany, formData, setFormData }) => {
  const [artists, setArtists] = useState([]);

  useEffect(() => {
    if (!selectedCompany) return;

    const data = new FormData();
    data.append("record_company_id", selectedCompany);

    fetch("http://localhost:5000/experimental/record_company/artists", {
      method: "POST",
      body: data,
    })
      .then((res) => res.json())
      .then(setArtists)
      .catch(() => {
        console.error("Error loading artists.");
      });
  }, [selectedCompany]);

  const handleCheckboxChange = (artistId) => {
    setFormData((prev) => {
      const isSelected = prev.artist_ids.includes(artistId);
      return {
        ...prev,
        artist_ids: isSelected
          ? prev.artist_ids.filter((id) => id !== artistId)
          : [...prev.artist_ids, artistId],
      };
    });
  };

return (
    <div className="artist-selector">
        {artists.map((artist) => (

            <div
            key={artist.artist_id}
            style={{
                display: 'flex',
                alignItems: 'center',
                padding: '8px',
                borderRadius: '4px',
                cursor: 'pointer',
                backgroundColor: formData.artist_ids.includes(artist.artist_id) ? '#ffffff' : 'transparent',
                width: '100%',
            }}
            onClick={() => handleCheckboxChange(artist.artist_id)}
            >
            <input
                type="checkbox"
                checked={formData.artist_ids.includes(artist.artist_id)}
                readOnly
                style={{ display: 'none' }}
            />

            <img
                src={`http://localhost:5000/static/images/artists/${artist.artist_image}`}
                alt={artist.artist_name}
                style={{
                marginRight: '10px',
                width: '40px',
                height: '40px',
                borderRadius: '50%',
                objectFit: 'cover',
                border: formData.artist_ids.includes(artist.artist_id)
                    ? '3px solid #8c78f9'
                    : '2px solid transparent',
                transition: 'all 0.2s ease-in-out',
                }}
            />

            <span
                style={{
                fontWeight: 500,
                color: formData.artist_ids.includes(artist.artist_id)
                    ? '#8c78f9'
                    : '#ffffff',
                userSelect: 'none',
                transition: 'color 0.2s ease-in-out',
                }}
            >
                {artist.artist_name}
            </span>
            </div>
        ))}
    </div>
);
};

export default ArtistSelector;
