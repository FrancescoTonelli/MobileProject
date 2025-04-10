import React, { useEffect, useState } from 'react';

const ViewRecords = () => {
  const [companies, setCompanies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedCompanyId, setSelectedCompanyId] = useState(null);
  const [newEmail, setNewEmail] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [notificationTitle, setNotificationTitle] = useState('');
  const [notificationDescription, setNotificationDescription] = useState('');

  const fetchCompanies = () => {
    setLoading(true);
    setCompanies([]);
    fetch('http://localhost:5000/admin/records')
      .then((res) => res.json())
      .then((data) => {
        setCompanies(data);
        setLoading(false);
      })
      .catch((err) => {
        console.error('Error fetching companies:', err);
        setLoading(false);
      });
  };

  useEffect(() => {
    fetchCompanies();
  }, []);

  const createCompany = () => {
    if (!newEmail || !newPassword) {
      alert('Please provide email and password');
      return;
    }

    fetch('http://localhost:5000/admin/records', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: newEmail, password: newPassword }),
    })
      .then((res) => {
        if (!res.ok) throw new Error('Failed to create');
        alert('Record company created');
        setNewEmail('');
        setNewPassword('');
        fetchCompanies();
      })
      .catch((err) => {
        console.error(err);
        alert('Error creating record company');
      });
  };

  const deleteCompany = (id) => {
    fetch(`http://localhost:5000/admin/records/${id}`, {
      method: 'DELETE',
    })
      .then((res) => {
        if (!res.ok) throw new Error('Failed to delete');
        alert('Deleted');
        fetchCompanies();
      })
      .catch((err) => {
        console.error(err);
        alert('Error deleting');
      });
  };

  const sendNotification = () => {
    if (!notificationTitle || !notificationDescription || !selectedCompanyId) {
      alert('Fill all fields');
      return;
    }

    const data = {
      title: notificationTitle,
      description: notificationDescription,
      record_company_id: selectedCompanyId,
    };

    fetch('http://localhost:5000/admin/notify_record', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    })
      .then((res) => {
        if (!res.ok) throw new Error('Failed');
        alert('Notification sent');
        setNotificationTitle('');
        setNotificationDescription('');
        setSelectedCompanyId(null);
      })
      .catch((err) => {
        console.error(err);
        alert('Error sending notification');
      });
  };

  if (loading) return <p>Loading...</p>;

  return (
    <div className="body white-text">
      <div className="header-content">
        <h3>Record Companies</h3>
        <button onClick={fetchCompanies} className="btn btn-primary nav-button">
            <svg xmlns="http://www.w3.org/2000/svg" fill="currentColor" height="24px" width="24px" viewBox="0 0 489.533 489.533">
            <g>
              <path d="M268.175,488.161c98.2-11,176.9-89.5,188.1-187.7c14.7-128.4-85.1-237.7-210.2-239.1v-57.6c0-3.2-4-4.9-6.7-2.9   l-118.6,87.1c-2,1.5-2,4.4,0,5.9l118.6,87.1c2.7,2,6.7,0.2,6.7-2.9v-57.5c87.9,1.4,158.3,76.2,152.3,165.6   c-5.1,76.9-67.8,139.3-144.7,144.2c-81.5,5.2-150.8-53-163.2-130c-2.3-14.3-14.8-24.7-29.2-24.7c-17.9,0-31.9,15.9-29.1,33.6   C49.575,418.961,150.875,501.261,268.175,488.161z"/>
            </g>
            </svg>
        </button>
      </div>

      <table className="view-table" border="1">
        <thead className="bold-text">
          <tr>
            <th>ID</th>
            <th>Email</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {companies.map((company) => (
            <tr key={company.id}>
              <td>{company.id}</td>
              <td>{company.email}</td>
              <td>
                <button
                  className="btn btn-danger my-button"
                  onClick={() => deleteCompany(company.id)}
                >
                  Delete
                </button>
                <button
                  className="btn btn-info  my-button"
                  onClick={() => setSelectedCompanyId(company.id)}
                >
                  Select for Notification
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {/* Create new company */}
      <div className="notification-form" style={{ marginTop: '30px' }}>
        <h4>Create Record Company</h4>
        <form
          onSubmit={(e) => {
            e.preventDefault();
            createCompany();
          }}
          className='notification-form'
        >
          <label className='notification-label'>
            Email:
            <input
                type="email"
                className="login-input"
                value={newEmail}
                onChange={(e) => setNewEmail(e.target.value)}
                required
            />
          </label>
          <br />
          <label className='notification-label'>
            Password:
            <input
              type="password"
              className="login-input"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              required
            />
          </label>
          <br />
          <button type="submit" className="btn btn-success  my-button">
            Create
          </button>
        </form>
      </div>

      {/* Notification Form */}
      <div className='notification-form' style={{ marginTop: '30px' }}>
        <h4>Send Notification</h4>
        <form
          onSubmit={(e) => {
            e.preventDefault();
            sendNotification();
          }}
          className='notification-form'
        >
          <label className='notification-label'>
            Selected Record Company:{' '}
            <strong>{selectedCompanyId ?? 'None selected'}</strong>
          </label>
          <br />
          <label className='notification-label'>
            Title:
            <input
              type="text"
              className="login-input"
              value={notificationTitle}
              onChange={(e) => setNotificationTitle(e.target.value)}
              required
            />
          </label>
          <br />
          <label className='notification-label'>
            Description:
            <textarea
              className="login-input"
              value={notificationDescription}
              onChange={(e) => setNotificationDescription(e.target.value)}
              required
            />
          </label>
          <br />
          <button type="submit" className="btn btn-success my-button" disabled={!selectedCompanyId}>
            Send Notification
          </button>
        </form>
      </div>
    </div>
  );
};

export default ViewRecords;
