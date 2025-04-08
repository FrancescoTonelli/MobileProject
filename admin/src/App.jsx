import React, { useState } from "react";
import Navbar from "./components/Navbar";
import logo from './assets/logo.svg'; 
import Login from "./components/Login";
import ViewDefault from "./views/ViewDefault";
import {
  USER,
  CONCERTS,
  TOURS,
  RECORD_COMPANIES,
  PLACES,
  ARTISTS,
  DEFAULT
} from "./views/ViewsIndex"; 

function App() {

  const [isLoggedIn, setIsLoggedIn] = useState(false); 
  const [view, setView] = useState(DEFAULT);

  const handleLogin = () => {
    setIsLoggedIn(true);
  };


  const renderView = () => {
    switch (view) {
      case USER:
        return <div className="body white-text">User View</div>;
      case CONCERTS:
        return <div className="body white-text">Concerts View</div>;
      case TOURS:
        return <div className="body white-text">Tours View</div>;
      case RECORD_COMPANIES:
        return <div className="body white-text">Record Companies View</div>;
      case PLACES:
        return <div className="body white-text">Places View</div>;
      case ARTISTS:
        return <div className="body white-text">Artists View</div>;
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
              <Navbar setView={setView} />
              
              <button
                className="btn btn-primary nav-button"
                type="button"
              >
                {/* SVG dentro al bottone */}
                <svg xmlns="http://www.w3.org/2000/svg" xmlnsXlink="http://www.w3.org/1999/xlink" fill="currentColor" height="24px" width="24px" version="1.1" id="Capa_1" viewBox="0 0 489.533 489.533" xmlSpace="preserve">
                  <g>
                    <path d="M268.175,488.161c98.2-11,176.9-89.5,188.1-187.7c14.7-128.4-85.1-237.7-210.2-239.1v-57.6c0-3.2-4-4.9-6.7-2.9   l-118.6,87.1c-2,1.5-2,4.4,0,5.9l118.6,87.1c2.7,2,6.7,0.2,6.7-2.9v-57.5c87.9,1.4,158.3,76.2,152.3,165.6   c-5.1,76.9-67.8,139.3-144.7,144.2c-81.5,5.2-150.8-53-163.2-130c-2.3-14.3-14.8-24.7-29.2-24.7c-17.9,0-31.9,15.9-29.1,33.6   C49.575,418.961,150.875,501.261,268.175,488.161z"/>
                  </g>
                </svg>
              </button>
              
            </div>
            <div className="header-content">
              <p className="bold-text purple-text">Admin Panel</p>
              <img src={logo} alt="Icona" width="50" height="50" />
            </div>
          </header>

          <div className="content">
            {renderView()}
          </div>
        </>
      )}
    </main>
  );
}

export default App;
