import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { BASEURL } from '../helper/helper';
import '../css/AdminUsers.css';

const AdminUsers = () => {
  const [users, setUsers] = useState([]);
  const [newUser, setNewUser] = useState({
    email: '',
    firstName: '',
    lastName: '',
    role: 'EMPLOYEE',
    password: ''
  });
  const [isLoading, setIsLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [showAddUser, setShowAddUser] = useState(false);
  const [currentUserRole, setCurrentUserRole] = useState('');

  useEffect(() => {
    setCurrentUserRole(localStorage.getItem('role') || '');
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      const token = localStorage.getItem('user');
      const response = await axios.get(`${BASEURL}/api/v1/admin/users`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setUsers(response.data);
    } catch (error) {
      setMessage('Failed to fetch users');
    }
  };

  const handleCreateUser = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    try {
      const token = localStorage.getItem('user');
      await axios.post(`${BASEURL}/api/v1/admin/users`, newUser, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setMessage('User created successfully');
      setNewUser({ email: '', firstName: '', lastName: '', role: 'EMPLOYEE', password: '' });
      setShowAddUser(false);
      fetchUsers();
      setTimeout(() => setMessage(''), 5000);
    } catch (error) {
      let errorMsg = 'Failed to create user';
      if (error.response?.data?.error) {
        errorMsg = error.response.data.error;
      } else if (error.response?.data?.message) {
        errorMsg = error.response.data.message;
      } else if (error.message) {
        errorMsg = error.message;
      }
      setMessage(errorMsg);
    }
    setIsLoading(false);
  };

  const handleRoleChange = async (userId, newRole) => {
    try {
      const token = localStorage.getItem('user');
      await axios.put(`${BASEURL}/api/v1/admin/users/${userId}/role`, 
        { role: newRole },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setMessage('Role updated successfully');
      fetchUsers();
      setTimeout(() => setMessage(''), 5000);
    } catch (error) {
      setMessage('Failed to update role');
    }
  };

  const handleDeleteUser = async (userId) => {
    if (window.confirm('Are you sure you want to delete this user?')) {
      try {
        const token = localStorage.getItem('user');
        await axios.delete(`${BASEURL}/api/v1/admin/users/${userId}`, {
          headers: { Authorization: `Bearer ${token}` }
        });
        setMessage('User deleted successfully');
        fetchUsers();
      } catch (error) {
        setMessage('Failed to delete user');
      }
    }
  };

  const handleToggle2FA = async (userId, currentStatus) => {
    const action = currentStatus ? 'disable' : 'enable';
    if (window.confirm(`Are you sure you want to ${action} 2FA for this user?`)) {
      try {
        const token = localStorage.getItem('user');
        await axios.post(`${BASEURL}/api/v1/admin/users/${userId}/2fa/${action}`, {}, {
          headers: { Authorization: `Bearer ${token}` }
        });
        setMessage(`2FA ${action}d successfully for user`);
        fetchUsers();
      } catch (error) {
        setMessage(`Failed to ${action} 2FA for user`);
      }
    }
  };

  const handleLockUnlockUser = async (userId, currentLockStatus) => {
    const action = currentLockStatus ? 'unlock' : 'lock';
    if (window.confirm(`Are you sure you want to ${action} this user account?`)) {
      try {
        const token = localStorage.getItem('user');
        await axios.post(`${BASEURL}/api/v1/admin/users/${userId}/${action}`, {}, {
          headers: { Authorization: `Bearer ${token}` }
        });
        setMessage(`User account ${action}ed successfully`);
        fetchUsers();
      } catch (error) {
        setMessage(`Failed to ${action} user account`);
      }
    }
  };

  return (
    <div className="admin-users">
      <h2>User Management</h2>
      
      {message && <div className="message">{message}</div>}
      
      <div className="user-actions">
        <button 
          className="add-user-btn"
          onClick={() => setShowAddUser(!showAddUser)}
        >
          {showAddUser ? 'Cancel' : 'Add New User'}
        </button>
      </div>

      {showAddUser && (
        <form onSubmit={handleCreateUser} className="add-user-form">
          <h3>Create New User</h3>
          <div className="form-row">
            <input
              type="email"
              placeholder="Email"
              value={newUser.email}
              onChange={(e) => setNewUser({...newUser, email: e.target.value})}
              required
            />
            <input
              type="text"
              placeholder="First Name"
              value={newUser.firstName}
              onChange={(e) => setNewUser({...newUser, firstName: e.target.value})}
              required
            />
            <input
              type="text"
              placeholder="Last Name"
              value={newUser.lastName}
              onChange={(e) => setNewUser({...newUser, lastName: e.target.value})}
              required
            />
          </div>
          <div className="form-row">
            <select
              value={newUser.role}
              onChange={(e) => setNewUser({...newUser, role: e.target.value})}
            >
              <option value="EMPLOYEE">Employee</option>
              <option value="HR">HR</option>
              {currentUserRole === 'SUPER_ADMIN' && <option value="ADMIN">Admin</option>}
              {currentUserRole === 'SUPER_ADMIN' && <option value="SUPER_ADMIN">Super Admin</option>}
            </select>
            <input
              type="password"
              placeholder="Password"
              value={newUser.password}
              onChange={(e) => setNewUser({...newUser, password: e.target.value})}
              required
            />
            <button type="submit" disabled={isLoading}>
              {isLoading ? 'Creating...' : 'Create User'}
            </button>
          </div>
        </form>
      )}

      <div className="users-table">
        <table>
          <thead>
            <tr>
              <th>Name</th>
              <th>Email</th>
              <th>Role</th>
              <th>Account Status</th>
              <th>2FA Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {users.map((user) => (
              <tr key={user.id}>
                <td>{user.firstName} {user.lastName}</td>
                <td>{user.email}</td>
                <td>
                  <select
                    value={user.role}
                    onChange={(e) => handleRoleChange(user.id, e.target.value)}
                    disabled={user.role === 'SUPER_ADMIN' || (currentUserRole === 'ADMIN' && (user.role === 'ADMIN' || user.role === 'SUPER_ADMIN'))}
                  >
                    <option value="EMPLOYEE">Employee</option>
                    <option value="HR">HR</option>
                    {currentUserRole === 'SUPER_ADMIN' && <option value="ADMIN">Admin</option>}
                    {currentUserRole === 'SUPER_ADMIN' && <option value="SUPER_ADMIN">Super Admin</option>}
                  </select>
                </td>
                <td>
                  <span className={`status ${user.isAccountLocked ? 'locked' : 'active'}`}>
                    {user.isAccountLocked ? 'Locked' : 'Active'}
                  </span>
                </td>
                <td>
                  <span className={`status ${user.isTwoFactorEnabled ? 'enabled' : 'disabled'}`}>
                    {user.isTwoFactorEnabled ? 'Enabled' : 'Disabled'}
                  </span>
                </td>
                <td className="actions-cell">
                  {user.role !== 'SUPER_ADMIN' && (currentUserRole === 'SUPER_ADMIN' || (currentUserRole === 'ADMIN' && user.role !== 'ADMIN')) && (
                    <>
                      <button 
                        className={`toggle-btn ${user.isAccountLocked ? 'unlock' : 'lock'}`}
                        onClick={() => handleLockUnlockUser(user.id, user.isAccountLocked)}
                        title={user.isAccountLocked ? 'Unlock Account' : 'Lock Account'}
                      >
                        {user.isAccountLocked ? 'Unlock' : 'Lock'}
                      </button>
                      
                      <button 
                        className={`toggle-btn ${user.isTwoFactorEnabled ? 'disable' : 'enable'}`}
                        onClick={() => handleToggle2FA(user.id, user.isTwoFactorEnabled)}
                        title={user.isTwoFactorEnabled ? 'Disable 2FA' : 'Enable 2FA'}
                      >
                        {user.isTwoFactorEnabled ? 'Disable 2FA' : 'Enable 2FA'}
                      </button>
                      
                      <button 
                        className="delete-btn"
                        onClick={() => handleDeleteUser(user.id)}
                        title="Delete User"
                      >
                        Delete
                      </button>
                    </>
                  )}
                  {(user.role === 'SUPER_ADMIN' || (currentUserRole === 'ADMIN' && user.role === 'ADMIN')) && (
                    <span className="protected-user">Protected</span>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default AdminUsers;
