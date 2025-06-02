import React, { useState } from "react";
import { Routes, Route } from 'react-router-dom';
import Navbar from "./components/Navbar";
import logo from './assets/logo.svg'; 
import Login from "./components/Login";
import ViewDefault from "./views/ViewDefault";
import ViewUsers from "./views/ViewUsers";
import ViewRecords from "./views/ViewRecords";
import ViewArtists from "./views/ViewArtists";
import ViewPlaces from "./views/ViewPlaces";
import ViewConcerts from "./views/ViewConcerts";
import ViewTours from "./views/ViewTours";
import ViewExpQr from "./views/ViewExpQr";
import ViewExpConcert from "./views/ViewExpConcert";
import ViewExpTour from "./views/ViewExpTour";
import {
  USER,
  CONCERTS,
  TOURS,
  RECORD_COMPANIES,
  PLACES,
  ARTISTS,
  EXP_QR,
  EXP_CONCERT,
  EXP_TOUR,
  DEFAULT
} from "./views/ViewsIndex"; 

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false); 
  const [view, setView] = useState(DEFAULT);
  const [isNavbarOpen, setIsNavbarOpen] = useState(false);

  const handleLogin = () => {
    setIsLoggedIn(true);
  };

  const renderView = () => {
    switch (view) {
      case USER:
        return <ViewUsers />;
      case CONCERTS:
        return <ViewConcerts/>;
      case TOURS:
        return <ViewTours/>;
      case RECORD_COMPANIES:
        return <ViewRecords />;
      case PLACES:
        return <ViewPlaces />;
      case ARTISTS:
        return <ViewArtists />;
      case EXP_QR:
        return <ViewExpQr isNavbarOpen={isNavbarOpen} />;
      case EXP_CONCERT:
        return <ViewExpConcert />;
      case EXP_TOUR:
        return <ViewExpTour />;
      default:
        return <ViewDefault />;
    }
  };

  return (
    <main className="app-container">
      {!isLoggedIn ? (
        <Login onLogin={handleLogin} />
      ) : (
        <>
          <header className="header">
            <div className="header-content">
              <Navbar setView={setView} setIsNavbarOpen={setIsNavbarOpen} />
            </div>
            <div className="header-content">
              <p className="bold-text purple-text">Admin Panel</p>
              <img src={logo} alt="Icona" width="50" height="50" />
            </div>
          </header>

          <div className="over-body">
            <Routes>
              <Route path="/" element={renderView()} />
            </Routes>
          </div>
        </>
      )}
    </main>
  );
}

export default App;
