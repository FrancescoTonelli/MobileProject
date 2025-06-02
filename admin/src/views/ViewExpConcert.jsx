import React, { useEffect, useState } from "react";
import ArtistSelector from "../components/ArtistSelector";

const ViewExpConcert = () => {
  const [recordCompanies, setRecordCompanies] = useState([]);
  const [selectedCompany, setSelectedCompany] = useState("");
  const [artists, setArtists] = useState([]);
  const [places, setPlaces] = useState([]);
  const [selectedPlace, setSelectedPlace] = useState("");
  const [sectors, setSectors] = useState([]);
  const [sectorPrices, setSectorPrices] = useState({});
  const [formData, setFormData] = useState({
    title: "",
    date: "",
    time: "",
    artist_ids: [],
    image: null,
    tour_id: "", 
  });
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
    if (!formData.date) return;
    fetch("http://localhost:5000/experimental/places_for_creation", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ date: formData.date }),
    })
      .then((res) => res.json())
      .then(setPlaces)
      .catch(() => {
        setMessage("Error loading places.");
        setMessageStatus("error");
      });
  }, [formData.date]);

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

  useEffect(() => {
    const place = places.find((p) => p.id === parseInt(selectedPlace));
    if (place) {
      setSectors(place.sectors || []);
      const initialPrices = {};
      place.sectors.forEach((s) => {
        initialPrices[s.id] = "";
      });
      setSectorPrices(initialPrices);
    }
  }, [selectedPlace]);

  const handleSectorPriceChange = (sectorId, price) => {
    setSectorPrices((prev) => ({ ...prev, [sectorId]: price }));
  };

  const handleInputChange = (e) => {
    const { name, value, files } = e.target;
    if (name === "image") {
      setFormData((prev) => ({ ...prev, image: files[0] }));
    } else {
      setFormData((prev) => ({ ...prev, [name]: value }));
    }
  };

  const isFormComplete = () => {
    const requiredFields = [
      selectedCompany,
      formData.title,
      formData.date,
      formData.time,
      selectedPlace,
      formData.image,
    ];
    const allPricesFilled = Object.values(sectorPrices).every((p) => p !== "");
    return (
      requiredFields.every(Boolean) &&
      formData.artist_ids.length > 0 &&
      allPricesFilled
    );
  };

  const handleSubmit = () => {
    const payload = new FormData();
    payload.append("record_company_id", selectedCompany);
    payload.append("title", formData.title);
    payload.append("date", formData.date);
    payload.append("time", formData.time);
    payload.append("place_id", selectedPlace);
    payload.append("tour_id", formData.tour_id); 
    payload.append("image", formData.image);
    formData.artist_ids.forEach((id) => payload.append("artist_ids", id));
    payload.append("sector_prices", JSON.stringify(sectorPrices));

    fetch("http://localhost:5000/experimental/concert/create", {
      method: "POST",
      body: payload,
    })
      .then(async (res) => {
        const data = await res.json();
        if (res.ok) {
          setMessage("Concert created successfully!");
          setMessageStatus("success");
            setFormData({
                title: "",
                date: "",
                time: "",
                artist_ids: [],
                image: null,
                tour_id: "",
            });
            setArtists([]);
            setPlaces([]);
            setSelectedPlace("");
            setSectors([]);
            setSectorPrices({});

        } else {
          setMessage("Error: " + data.message);
          setMessageStatus("error");
        }
      })
      .catch(() => {
        setMessage("Network error during creation.");
        setMessageStatus("error");
      });
  };

  return (
    <div className="body white-text concert-view">
      <h2>Create Concert</h2>

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
          <div  className="concert-view-div">
            <h5>Title:</h5>
            <input
              className="qr-select"
              name="title"
              value={formData.title}
              onChange={handleInputChange}
            />
          </div>

          <div  className="concert-view-div">
            <h5>Date:</h5>
            <input
              className="qr-select"
              type="date"
              name="date"
              value={formData.date}
              onChange={handleInputChange}
            />
          </div>

          <div className="concert-view-div">
            <h5>Time:</h5>
            <input
              className="qr-select"
              type="time"
              name="time"
              value={formData.time}
              onChange={handleInputChange}
            />
          </div>

          <div className="concert-view-div">
            <h5>Image:</h5>
            <input
              className="qr-select"
              type="file"
              name="image"
              accept="image/*"
              onChange={handleInputChange}
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

          <div className="concert-view-div">
            <h5>Place:</h5>
            <select
              className="qr-select"
              value={selectedPlace}
              onChange={(e) => setSelectedPlace(e.target.value)}
            >
              <option value="">-- Select --</option>
              {places.map((p) => (
                <option key={p.id} value={p.id}>
                  {p.name}
                </option>
              ))}
            </select>
          </div>

          {sectors.length > 0 && (
            <div className="concert-view-div">
                <h5>Sector Prices</h5>
              <div className="sector-prices-column">
                {sectors.map((sector) => (
                <div key={sector.id} className="sector-price-item">
                  <h5>{sector.name} (€):</h5>
                  <input
                    className="qr-select"
                    type="number"
                    value={sectorPrices[sector.id] || ""}
                    onChange={(e) =>
                      handleSectorPriceChange(sector.id, e.target.value)
                    }
                    placeholder="0.00"
                  />
                </div>
              ))}
              </div>

            </div>
          )}

          {message && messageStatus && (
            <div className={`qr-message-box qr-message-${messageStatus}`}>
              {message}
            </div>
          )}

          <button
              className="btn btn-success my-button"
              onClick={handleSubmit}
              disabled={!isFormComplete()}
            >
              Create Concert
        </button>
        </>
      )}

    
    </div>
  );
};

export default ViewExpConcert;