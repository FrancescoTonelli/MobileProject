import React, { useEffect, useRef, useState } from "react";
import jsQR from "jsqr";

const ViewExpQr = ({ isNavbarOpen }) => {
  const [recordCompanies, setRecordCompanies] = useState([]);
  const [selectedCompany, setSelectedCompany] = useState("");
  const [scanResult, setScanResult] = useState(null);
  const [message, setMessage] = useState("");
  const [messageStatus, setMessageStatus] = useState("");
  const videoRef = useRef(null);
  const canvasRef = useRef(null);
  const streamRef = useRef(null);

  useEffect(() => {
    fetch("http://localhost:5000/experimental/record_companies")
      .then((res) => res.json())
      .then(setRecordCompanies)
      .catch(() => {
        setMessage("Failed to load record companies.");
        setMessageStatus("error");
      });
  }, []);
  
  useEffect(() => {
    if (!selectedCompany) {
      stopCamera();
      return;
    }

    if (!scanResult && !isNavbarOpen) {
      startCamera();
    } else {
      stopCamera();
    }

    return () => stopCamera();
  }, [selectedCompany, scanResult, isNavbarOpen]);

  const startCamera = async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({
        video: { facingMode: "environment" },
      });

      videoRef.current.srcObject = stream;
      streamRef.current = stream;

      videoRef.current.setAttribute("playsinline", true);
      videoRef.current.play().catch(() => {});

      requestAnimationFrame(tick);
      setMessage("Point the camera at a QR code.");
      setMessageStatus("info");
    } catch (err) {
      setMessage("Camera access error: " + err.message);
      setMessageStatus("error");
    }
  };

  const stopCamera = () => {
    if (streamRef.current) {
      streamRef.current.getTracks().forEach((track) => track.stop());
      streamRef.current = null;
    }
  };

  const tick = () => {
    const video = videoRef.current;
    const canvas = canvasRef.current;

    if (video && video.readyState === video.HAVE_ENOUGH_DATA && !isNavbarOpen) {
      canvas.width = video.videoWidth;
      canvas.height = video.videoHeight;
      const ctx = canvas.getContext("2d");
      ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
      const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height);
      const code = jsQR(imageData.data, canvas.width, canvas.height, {
        inversionAttempts: "attemptBoth",
      });

      if (code) {
        try {
          const json = JSON.parse(code.data);
          setScanResult(json);
          setMessage("QR code successfully scanned.");
          setMessageStatus("success");
          stopCamera();
          return;
        } catch {
          if (code.data) {
            setMessage("Invalid QR code format: " + code.data);
            setMessageStatus("error");
          }
        }
      }
    }

    if (!scanResult) {
      requestAnimationFrame(tick);
    }
  };

  const validateTicket = () => {
    if (!selectedCompany || !scanResult) {
      setMessage("Select a record company and scan a valid QR code.");
      setMessageStatus("error");
      return;
    }

    const { user_id, ticket_id, concert_id } = scanResult;

    fetch("http://localhost:5000/experimental/ticket/validate", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        record_company_id: selectedCompany,
        user_id,
        ticket_id,
        concert_id,
      }),
    })
      .then(async (res) => {
        const data = await res.json();
        if (res.ok) {
          setMessage("Ticket validated: " + data.message);
          setMessageStatus("success");
        } else {
          setMessage("Validation failed: " + data.message);
          setMessageStatus("error");
        }
      })
      .catch(() => {
        setMessage("Ticket validation request failed.");
        setMessageStatus("error");
      });
  };

  const refreshScan = () => {
    setScanResult(null);
    setMessage("");
    setMessageStatus("");
    startCamera();
  };

  return (
    <div className="body white-text">
      <h2>Ticket Validator</h2>

      <div>
        <select
          id="recordCompanySelect"
          className="qr-select"
          value={selectedCompany}
          onChange={(e) => {
            setSelectedCompany(e.target.value);
            setScanResult(null);
            setMessage("");
            setMessageStatus("");
          }}
        >
          <option value="">-- Select Record Company --</option>
          {recordCompanies.map((rc) => (
            <option key={rc.id} value={rc.id}>
              {rc.email}
            </option>
          ))}
        </select>
      </div>

      {selectedCompany && !isNavbarOpen && (
        <>
          <div className="video-wrapper">
            <video ref={videoRef} className="video" />
            <canvas ref={canvasRef} className="canvas" />
          </div>

          {message && messageStatus && <div className={`qr-message-box qr-message-${messageStatus}`}>{message}</div>}

          {scanResult ? (
            <div className="validation">
              <button className="btn btn-danger my-button" onClick={validateTicket}>
                Validate Ticket
              </button>
              <button
                className="btn btn-info my-button"
                onClick={refreshScan}
              >
                Scan Again
              </button>
            </div>
          ) : null}
        </>
      )}
    </div>
  );
};

export default ViewExpQr;
