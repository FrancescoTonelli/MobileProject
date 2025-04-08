import React, { useState } from 'react';
import logo from '../assets/logo.svg';

function Login({ onLogin }) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);

  const handleSubmit = async (event) => {
    event.preventDefault();
    
    if (email && password) {
      try {
        const response = await fetch('http://localhost:5000/admin_login', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            email: email,
            password: password,
          }),
        });

        const data = await response.json();

        if (response.ok) {
          onLogin();
        } else {
          setError(data.message);
        }
      } catch (err) {
        setError('An error occurred. Please try again later.');
      }
    } else {
      setError('Please fill in both fields!');
    }
  };

  return (
    <div className="login-container">
      <img src={logo} alt="Icona" width="100" height="100" />
      <h2 className="login-title">Admin Panel</h2>
      <form onSubmit={handleSubmit} className='login-form'>
        <div className="mb-3">
          <label htmlFor="email" className="form-label login-label">Email</label>
          <input
            type="email"
            className="form-control login-input"
            id="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>
        <div className="mb-3">
          <label htmlFor="password" className="form-label login-label">Password</label>
          <input
            type="password"
            className="form-control login-input"
            id="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>

        {error && <p className="white-text">{error}</p>}

        <button type="submit" className="login-button">Login</button>
      </form>
    </div>
  );
}

export default Login;
