import React, { useEffect, useRef } from "react";
import NavButton from "./NavButton";
import {
    DEFAULT,
    USER,
    CONCERTS,
    TOURS,
    RECORD_COMPANIES,
    PLACES,
    ARTISTS,
    EXP_QR,
    EXP_CONCERT,
    EXP_TOUR
  } from "../views/ViewsIndex"; 

function Navbar( {setView, setIsNavbarOpen } ) {

    const offcanvasRef = useRef(null);

    useEffect(() => {
        const offcanvasElement = offcanvasRef.current;

        const handleShow = () => setIsNavbarOpen(true);
        const handleHide = () => setIsNavbarOpen(false);

        if (offcanvasElement) {
        offcanvasElement.addEventListener("show.bs.offcanvas", handleShow);
        offcanvasElement.addEventListener("hide.bs.offcanvas", handleHide);
        }

        return () => {
        if (offcanvasElement) {
            offcanvasElement.removeEventListener("show.bs.offcanvas", handleShow);
            offcanvasElement.removeEventListener("hide.bs.offcanvas", handleHide);
        }
        };
    }, [setIsNavbarOpen]);


    const closeOffcanvas = () => {
        const offcanvasElement = offcanvasRef.current;
        if (offcanvasElement) {
            const offcanvasInstance = bootstrap.Offcanvas.getInstance(offcanvasElement) 
                || new bootstrap.Offcanvas(offcanvasElement);
            offcanvasInstance.hide();
        }
    };

    const handleNavClick = (view) => {
        setView(view);
        closeOffcanvas();
    };

    return (
        <div style={{ zIndex: 1000 }}>
            <button
                className="btn btn-primary nav-button"
                type="button"
                data-bs-toggle="offcanvas"
                data-bs-target="#offcanvasWithBothOptions"
                aria-controls="offcanvasWithBothOptions"
            >
                <svg
                xmlns="http://www.w3.org/2000/svg"
                width="24"
                height="24"
                fill="currentColor"
                className="bi bi-list"
                viewBox="0 0 16 16"
                >
                <path
                    fillRule="evenodd"
                    d="M2.5 12.5a.5.5 0 010-1h11a.5.5 0 010 1h-11zm0-4a.5.5 0 010-1h11a.5.5 0 010 1h-11zm0-4a.5.5 0 010-1h11a.5.5 0 010 1h-11z"
                />
                </svg>
            </button>

            

            <div ref={offcanvasRef} className="offcanvas offcanvas-start table-nav" data-bs-scroll="true" tabIndex="-1" id="offcanvasWithBothOptions" aria-labelledby="offcanvasWithBothOptionsLabel">
                <div className="offcanvas-header nav-header d-flex justify-content-between">
                    <p className="offcanvas-title white-text thin-text" id="offcanvasWithBothOptionsLabel">Select Table</p>
                    <button type="button" className="nav-button" data-bs-dismiss="offcanvas" aria-label="Close">
                        <svg width="28px" height="28px" viewBox="0 0 24 24" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
                            <path fillRule="evenodd" clipRule="evenodd" d="M5.29289 5.29289C5.68342 4.90237 6.31658 4.90237 6.70711 5.29289L12 10.5858L17.2929 5.29289C17.6834 4.90237 18.3166 4.90237 18.7071 5.29289C19.0976 5.68342 19.0976 6.31658 18.7071 6.70711L13.4142 12L18.7071 17.2929C19.0976 17.6834 19.0976 18.3166 18.7071 18.7071C18.3166 19.0976 17.6834 19.0976 17.2929 18.7071L12 13.4142L6.70711 18.7071C6.31658 19.0976 5.68342 19.0976 5.29289 18.7071C4.90237 18.3166 4.90237 17.6834 5.29289 17.2929L10.5858 12L5.29289 6.70711C4.90237 6.31658 4.90237 5.68342 5.29289 5.29289Z"/>
                        </svg>
                    </button>
                </div>
                <div className="offcanvas-body d-flex flex-column align-items-center"> 
                    <NavButton title={"User"} onClick={() => handleNavClick(USER)}/>
                    <NavButton title={"Concerts"} onClick={() => handleNavClick(CONCERTS)}/>
                    <NavButton title={"Tours"} onClick={() => handleNavClick(TOURS)} /> 
                    <NavButton title={"Record Companies"} onClick={() => handleNavClick(RECORD_COMPANIES)} /> 
                    <NavButton title={"Places"} onClick={() => handleNavClick(PLACES)} />
                    <NavButton title={"Artists"} onClick={() => handleNavClick(ARTISTS)} />

                    <br className="w-100 my-3" />
                    <p className="white-text thin-text text-center">⚠ Experimental Section ⚠</p>
                    
                    <NavButton title={"Experimental QR"} onClick={() => handleNavClick(EXP_QR)} />
                    <NavButton title={"Experimental Concert"} onClick={() => handleNavClick(EXP_CONCERT)} />
                    <NavButton title={"Experimental Tour"} onClick={() => handleNavClick(EXP_TOUR)} />
                </div>
            </div>
        </div>
    );
}

export default Navbar;