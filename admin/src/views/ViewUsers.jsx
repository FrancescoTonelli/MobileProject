import React, { useEffect, useState } from 'react';

const UsersPage = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [notificationTitle, setNotificationTitle] = useState('');
  const [notificationDescription, setNotificationDescription] = useState('');
  const [selectedUserId, setSelectedUserId] = useState(null);

  const fetchUsers = () => {
    setLoading(true);
    setUsers([]);

    fetch('http://localhost:5000/admin/users')
      .then((response) => response.json())
      .then((data) => {
        setUsers(data);
        setLoading(false);
      })
      .catch((error) => {
        console.error('Error fetching users:', error);
        setLoading(false);
      });
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const refreshUsers = () => {
    fetchUsers();
  };

  const deleteUser = (userId) => {
    fetch(`http://localhost:5000/admin/users/${userId}`, {
      method: 'DELETE',
    })
      .then((response) => {
        if (response.ok) {
          refreshUsers();
          alert('User deleted successfully');
        } else {
          alert('Error deleting user');
        }
      })
      .catch((error) => {
        console.error('Error deleting user:', error);
        alert('Error deleting user');
      });
  };

  const sendNotification = () => {
    if (!notificationTitle || !notificationDescription || !selectedUserId) {
      alert('Please fill out all fields.');
      return;
    }

    const notificationData = {
      title: notificationTitle,
      description: notificationDescription,
      user_id: selectedUserId,
      record_company_id: null,
    };

    fetch('http://localhost:5000/admin/notify_user', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(notificationData),
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error('Failed to send notification');
        }
        return response.json();
      })
      .then(() => {
        alert('Notification sent successfully');
        setNotificationTitle('');
        setNotificationDescription('');
        setSelectedUserId(null);
      })
      .catch((error) => {
        console.error('Error sending notification:', error);
        alert('Error sending notification');
      });
  };

  if (loading) return <p>Loading users...</p>;

  return (
    <div className="body white-text">
      <div className="header-content">
        <h3>Users</h3>
        <button
          className="btn btn-primary nav-button"
          type="button"
          onClick={refreshUsers}
          title="Refresh list"
        >
          <svg xmlns="http://www.w3.org/2000/svg" fill="currentColor" height="24px" width="24px" viewBox="0 0 489.533 489.533">
            <g>
              <path d="M268.175,488.161c98.2-11,176.9-89.5,188.1-187.7c14.7-128.4-85.1-237.7-210.2-239.1v-57.6c0-3.2-4-4.9-6.7-2.9   l-118.6,87.1c-2,1.5-2,4.4,0,5.9l118.6,87.1c2.7,2,6.7,0.2,6.7-2.9v-57.5c87.9,1.4,158.3,76.2,152.3,165.6   c-5.1,76.9-67.8,139.3-144.7,144.2c-81.5,5.2-150.8-53-163.2-130c-2.3-14.3-14.8-24.7-29.2-24.7c-17.9,0-31.9,15.9-29.1,33.6   C49.575,418.961,150.875,501.261,268.175,488.161z"/>
            </g>
          </svg>
        </button>
      </div>

      <table className='view-table' border="1">
        <thead className="bold-text">
          <tr>
            <th>ID</th>
            <th>Username</th>
            <th>Email</th>
            <th>First Name</th>
            <th>Last Name</th>
            <th>Birthdate</th>
            <th>Refunds</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {users.map((user, index) => (
            <tr key={index}>
              <td>{user.id}</td>
              <td>{user.username}</td>
              <td>{user.email}</td>
              <td>{user.name}</td>
              <td>{user.surname}</td>
              <td>{user.birthdate}</td>
              <td>{user.refunds}</td>
              <td>
                <button
                  onClick={() => deleteUser(user.id)}
                  className="btn btn-danger my-button"
                  title="Delete User"
                >
                  Delete
                </button>
                <button
                  onClick={() => setSelectedUserId(user.id)}
                  className="btn btn-info my-button"
                  title="Prepare Notification"
                >
                  Select User
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {/* Notification Form */}
      <div className="notification-form" style={{ marginTop: '30px' }}>
        <h4>Send Notification to Selected User</h4>
        <form
          onSubmit={(e) => {
            e.preventDefault();
            sendNotification();
          }}
          className='notification-form'
        >
          <label className='notification-label'>
            Selected User ID:{' '}
            <strong>{selectedUserId !== null ? selectedUserId : 'None selected'}</strong>
          </label>
          <br />
          <label className='notification-label'>
            Title:
            <input
              type="text"
              value={notificationTitle}
              onChange={(e) => setNotificationTitle(e.target.value)}
              className='login-input'
              required
            />
          </label>
          <br />
          <label className='notification-label'>
            Description:
            <textarea
              value={notificationDescription}
              onChange={(e) => setNotificationDescription(e.target.value)}
              required
              className='login-input'
            />
          </label>
          <br />
          <button type="submit" className="btn btn-success my-button" disabled={!selectedUserId}>
            Send Notification
          </button>
        </form>
      </div>
    </div>
  );
};

export default UsersPage;
