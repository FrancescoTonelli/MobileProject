import React from 'react';

const NavButton = ({ title, onClick }) => {
    return (
        <button onClick={onClick} className='nav-indexers' type='button'>
            {title}
        </button>
    );
};

export default NavButton;