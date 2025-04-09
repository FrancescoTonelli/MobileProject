import React, { useState } from "react";
import Navbar from "./components/Navbar";
import logo from './assets/logo.svg'; 
import Login from "./components/Login";
import ViewDefault from "./views/ViewDefault";
import ViewUsers from "./views/ViewUsers";
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
        return <ViewUsers />;
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
