import React, { useEffect, useState } from "react";
import ArtistSelector from "../components/ArtistSelector";

const ViewExpTour = () => {
  const [recordCompanies, setRecordCompanies] = useState([]);
  const [selectedCompany, setSelectedCompany] = useState("");
  const [artists, setArtists] = useState([]);
  const [tourTitle, setTourTitle] = useState("");
  const [tourImage, setTourImage] = useState(null);
  const [formData, setFormData] = useState({ artist_ids: [] });
  const [tourId, setTourId] = useState(null);
  const [concerts, setConcerts] = useState([]);
  const [message, setMessage] = useState("");
  const [messageStatus, setMessageStatus] = useState("");

  useEffect(() => {
    fetch("http://localhost:5000/experimental/record_companies")
      .then((res) => res.json())
      .then(setRecordCompanies)
      .catch(() => {
        setMessage("Error loading record companies.");
        setMessageStatus("error");
      });
  }, []);

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
        setMessage("Error loading artists.");
        setMessageStatus("error");
      });
  }, [selectedCompany]);

  const handleAddConcert = () => {
    setConcerts([
      ...concerts,
      {
        title: "",
        date: "",
        time: "",
        place_id: "",
        sector_prices: {},
        places: [],
        sectors: [],
      },
    ]);
  };

  const handleRemoveConcert = (index) => {
    const updated = [...concerts];
    updated.splice(index, 1);
    setConcerts(updated);
  };

  const handleConcertChange = (index, key, value) => {
    const updated = [...concerts];
    updated[index][key] = value;
    setConcerts(updated);
  };

  const handleSectorPriceChange = (index, sectorId, price) => {
    const updated = [...concerts];
    updated[index].sector_prices[sectorId] = price;
    setConcerts(updated);
  };

  const loadPlacesAndSectors = async (index, date) => {
    const res = await fetch("http://localhost:5000/experimental/places_for_creation", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ date }),
    });
    const data = await res.json();
    const updated = [...concerts];
    updated[index].places = data;
    setConcerts(updated);
  };

  const handleCreateTour = async () => {
    const payload = new FormData();
    payload.append("record_company_id", selectedCompany);
    payload.append("title", tourTitle);
    payload.append("image", tourImage);
    formData.artist_ids.forEach((id) => payload.append("artist_ids", id));
    payload.append(
      "concerts",
      JSON.stringify(
        concerts.map((c) => ({
          title: c.title || tourTitle,
          date: c.date,
          time: c.time,
          place_id: c.place_id,
          sector_prices: c.sector_prices,
        }))
      )
    );

    const res = await fetch("http://localhost:5000/experimental/tour/create", {
      method: "POST",
      body: payload,
    });

    const data = await res.json();
    if (res.ok) {
      setMessage("Tour created successfully!");
      setMessageStatus("success");
      setTourId(data.tour_id);
      setConcerts([]);
      setTourTitle("");
      setTourImage(null);
      setFormData({ artist_ids: [] });
    } else {
      setMessage("Error: " + data.error);
      setMessageStatus("error");
    }
  };

  return (
    <div className="body white-text concert-view">
      <h2>Create Tour</h2>

      <div className="concert-view-div">
        <h5>Record Company:</h5>
        <select
          className="qr-select"
          value={selectedCompany}
          onChange={(e) => {
            setSelectedCompany(e.target.value);
            setMessage("");
            setMessageStatus("");
          }}
        >
          <option value="">-- Select --</option>
          {recordCompanies.map((rc) => (
            <option key={rc.id} value={rc.id}>
              {rc.email}
            </option>
          ))}
        </select>
      </div>

      {selectedCompany && (
        <>
          <div className="concert-view-div">
            <h5>Tour Title:</h5>
            <input
              className="qr-select"
              value={tourTitle}
              onChange={(e) => setTourTitle(e.target.value)}
            />
          </div>

          <div className="concert-view-div">
            <h5>Image:</h5>
            <input
              className="qr-select"
              type="file"
              accept="image/*"
              onChange={(e) => setTourImage(e.target.files[0])}
            />
          </div>

          <div className="concert-view-div">
            <h5>Artists:</h5>
            <ArtistSelector
              selectedCompany={selectedCompany}
              formData={formData}
              setFormData={setFormData}
            />
          </div>

          <h4>Concerts in the Tour</h4>
          {concerts.map((concert, index) => (
            <div
              key={index}
              className="inner-concert-view"
              style={{
                border: "1px solid #ccc",
                padding: "1rem",
                marginBottom: "1rem",
                borderRadius: "8px",
              }}
            >
              <div>
                <h5>🎤 Concert {index + 1}</h5>
                <button className="btn btn-danger btn-sm" onClick={() => handleRemoveConcert(index)}>
                  Remove
                </button>
              </div>

              <div>
                <label>Concert Title:</label>
                <input
                  className="qr-select"
                  type="text"
                  value={concert.title}
                  onChange={(e) => handleConcertChange(index, "title", e.target.value)}
                />
              </div>

              <div>
                <label>Date:</label>
                <input
                  className="qr-select"
                  type="date"
                  value={concert.date}
                  onChange={(e) => {
                    handleConcertChange(index, "date", e.target.value);
                    loadPlacesAndSectors(index, e.target.value);
                  }}
                />
              </div>

              <div>
                <label>Time:</label>
                <input
                  className="qr-select"
                  type="time"
                  value={concert.time}
                  onChange={(e) => handleConcertChange(index, "time", e.target.value)}
                />
              </div>

              <div>
                <label>Place:</label>
                <select
                  className="qr-select"
                  value={concert.place_id}
                  onChange={(e) => {
                    const selectedPlace = concert.places.find(p => p.id === parseInt(e.target.value));
                    handleConcertChange(index, "place_id", e.target.value);
                    handleConcertChange(index, "sectors", selectedPlace ? selectedPlace.sectors : []);
                    handleConcertChange(index, "sector_prices", {});
                  }}
                >
                  <option value="">-- Select Place --</option>
                  {concert.places.map((p) => (
                    <option key={p.id} value={p.id}>
                      {p.name}
                    </option>
                  ))}
                </select>
              </div>

              {concert.sectors?.length > 0 &&concert.sectors.map((sector) => (
                    <div key={sector.id}>
                      <label>{sector.name} (€):</label>
                      <input
                        className="qr-select"
                        type="number"
                        value={concert.sector_prices[sector.id] || ""}
                        onChange={(e) =>
                          handleSectorPriceChange(index, sector.id, e.target.value)
                        }
                        placeholder="0.00"
                      />
                    </div>
                  ))}
            </div>
          ))}

          <div className="concert-view-single">
            <button className="btn btn-secondary my-button" onClick={handleAddConcert}>
                Add Concert
            </button>
          </div>

          {message && messageStatus && (
            <div className={`qr-message-box qr-message-${messageStatus}`}>
              {message}
            </div>
          )}

          <button
            className="btn btn-success my-button"
            onClick={handleCreateTour}
            disabled={
              !tourTitle || !tourImage || formData.artist_ids.length === 0 || concerts.length === 0
            }
          >
            Create Tour
          </button>

          <hr />
        </>
      )}
    </div>
  );
};

export default ViewExpTour;
