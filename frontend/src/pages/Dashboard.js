import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import AddOrganization from './AddOrganization';
import AddHR from './AddHR';
import ViewHRs from './ViewHRs';
import '../css/Dashboard.css';
import AddEmployee from './AddEmployee';  
import { jwtDecode } from "jwt-decode";
import { BASEURL } from "../helper/helper.js";

const Dashboard = () => {
  const navigate = useNavigate();
  const [organizations, setOrganizations] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [showAddOrganization, setShowAddOrganization] = useState(false);
  const [showAddHR, setShowAddHR] = useState(false);
  const [showViewHRs, setShowViewHRs] = useState(false);
  const [selectedOrganizationId, setSelectedOrganizationId] = useState(null);

  const [showDeleteConfirmation, setShowDeleteConfirmation] = useState(false);
  const [organizationToDelete, setOrganizationToDelete] = useState(null);
  const [showAddEmployee, setShowAddEmployee] = useState(false); 
  const [userTitle, setUserTitle] = useState('');
  const [userRole, setUserRole] = useState('');

  useEffect(() => {
    const token = localStorage.getItem('user');
    const role = localStorage.getItem('role');
    if (token && role) {
      setUserRole(role);
      fetchUserInfo(token);
      fetchOrganizations(token);
    } else {
      navigate('/login');
    }
  }, [navigate]);

  const fetchUserInfo = async (token) => {
    try {
      const decoded = jwtDecode(token);
      const email = decoded.sub;

      const response = await axios.get(`${BASEURL}/api/v1/employees/getEmployeeInfo/${email}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      localStorage.setItem('username',response.data.first_name);
      setUserTitle(response.data.title);  
    } catch (error) {
      console.error('Failed to fetch user info:', error);
    }
  };

  const fetchOrganizations = async (token) => {
    try {
      const response = await axios.get(`${BASEURL}/api/v1/organizations/getAll`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setOrganizations(response.data);
    } catch (error) {
      console.error('Failed to fetch organizations:', error);
    }
  };

  const handleSearchChange = async (e) => {
    const query = e.target.value.trim();
    setSearchQuery(query);
  
    const token = localStorage.getItem('user');
  
    if (query.length >= 1) {
      try {
        const response = await axios.get(`${BASEURL}/api/v1/organizations/getByName/${query}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setOrganizations(response.data); 
      } catch (error) {
        console.error('Failed to fetch organizations:', error);
      }
    } else if (query.length === 0) {
      fetchOrganizations(token);
    } else {
      setOrganizations([]);
    }
  };

  const handleDeleteClick = (id) => {
    setOrganizationToDelete(id);
    setShowDeleteConfirmation(true);
  };

  const handleConfirmDelete = async () => {
    const token = localStorage.getItem('user');
    try {
      const response = await axios.delete(`${BASEURL}/api/v1/organizations/delete/${organizationToDelete}`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      if (response.status === 200) {
        fetchOrganizations(token);
      }
    } catch (error) {
      console.error('Failed to delete organization:', error);
      alert('Failed to delete organization. Please try again.');
    } finally {
      setShowDeleteConfirmation(false);
      setOrganizationToDelete(null);
    }
  };

  const handleCancelDelete = () => {
    setShowDeleteConfirmation(false);
    setOrganizationToDelete(null);
  };

  const handleOrganizationAdded = () => {
    const token = localStorage.getItem('user');
    fetchOrganizations(token);
    setShowAddOrganization(false);
  };

  const handleAddHR = (organizationId) => {
    setSelectedOrganizationId(organizationId);
    setShowAddHR(true);
  };

  const handleViewHRs = (organizationId) => {
    setSelectedOrganizationId(organizationId);
    setShowViewHRs(true);
  };

  const handleAddEmployee = () => {
    setShowAddEmployee(true); 
  };

  const handleEmployeeAdded = () => {
    setShowAddEmployee(false); 
    fetchOrganizations(localStorage.getItem('user')); 
  };

  return (
    <div className="dashboard">
      <h1>Dashboard - Welcome {localStorage.getItem('username')}</h1>
      <div className="role-info">
        <span className="role-badge">{userRole}</span>
      </div>
      
      {/* Role-based action buttons */}
      {!showAddHR && !showViewHRs && !showAddOrganization && !showAddEmployee && (
        <div className="actions">
          {/* Super Admin can do everything */}
          {userRole === 'SUPER_ADMIN' && (
            <>
              <button onClick={() => setShowAddOrganization(!showAddOrganization)}>Add Organization</button>
              <button onClick={handleAddEmployee}>Add Employee</button>
              <button onClick={() => navigate('/admin/users')}>Manage Users</button>
              <button onClick={() => navigate('/security')}>Security Settings</button>
            </>
          )}
          
          {/* Admin can add organizations and employees */}
          {userRole === 'ADMIN' && (
            <>
              <button onClick={() => setShowAddOrganization(!showAddOrganization)}>Add Organization</button>
              <button onClick={handleAddEmployee}>Add Employee</button>
            </>
          )}
          
          {/* HR can only add employees */}
          {userRole === 'HR' && (
            <button onClick={handleAddEmployee}>Add Employee</button>
          )}
          
          {/* Search bar for all roles */}
          <input
            type="text"
            placeholder="Search Organization"
            value={searchQuery}
            onChange={handleSearchChange}
            className="search-bar"
          />
        </div>
      )}

      {showAddOrganization ? (
        <AddOrganization onAddOrganization={handleOrganizationAdded} />
      ) : showAddHR ? (
        <AddHR onAddHR={() => setShowAddHR(false)} organizationId={selectedOrganizationId} />
      ) : showViewHRs ? (
        <ViewHRs onViewHRs={() => setShowViewHRs(false)} organizationId={selectedOrganizationId} />
      ) : showAddEmployee ? (
        <AddEmployee onAddEmployee={handleEmployeeAdded} />
      ) : (
        <div className="organizations">
          <h2>Organizations</h2>
          <table className="organization-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Address</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {organizations.length > 0 ? (
                organizations.map((org) => (
                  <tr key={org.id}>
                    <td>{org.name}</td>
                    <td>{org.address}</td>
                    <td>
                      <button onClick={() => handleAddHR(org.id)}>Add HR</button>
                      <button onClick={() => handleViewHRs(org.id)}>View HRs</button>
                      <button className="delete-btn-org" onClick={() => handleDeleteClick(org.id)}>Delete</button>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="3">No Organizations found</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      )}

      {/* Confirmation Modal */}
      {showDeleteConfirmation && (
        <div className="delete-confirmation-modal">
          <div className="modal-content">
            <h3>Are you sure you want to delete this organization?</h3>
            <div className="modal-actions">
              <button onClick={handleConfirmDelete}>Yes</button>
              <button onClick={handleCancelDelete}>No</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Dashboard;
