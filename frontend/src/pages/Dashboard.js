import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import AddOrganization from './AddOrganization';
import AddHR from './AddHR';
import ViewHRs from './ViewHRs';
import '../css/Dashboard.css';

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

  useEffect(() => {
    const token = localStorage.getItem('user');
    if (token) {
      fetchOrganizations(token);
    } else {
      navigate('/login');
    }
  }, [navigate]);

  const fetchOrganizations = async (token) => {
    try {
      const response = await axios.get('http://localhost:9192/api/v1/organizations/getAll', {
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
        const response = await axios.get(`http://localhost:9192/api/v1/organizations/getByName/${query}`, {
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
      const response = await axios.delete(`http://localhost:9192/api/v1/organizations/delete/${organizationToDelete}`, {
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

  return (
    <div className="dashboard">
      <h1>Dashboard</h1>
      
      <div className="actions">
        <button onClick={() => setShowAddOrganization(!showAddOrganization)}>Add Organization</button>
        <input
          type="text"
          placeholder="Search Organization"
          value={searchQuery}
          onChange={handleSearchChange}
          className="search-bar"
        />
      </div>

      {showAddOrganization ? (
        <AddOrganization onAddOrganization={handleOrganizationAdded} />
      ) : showAddHR ? (
        <AddHR onAddHR={() => setShowAddHR(false)} organizationId={selectedOrganizationId} />
      ) : showViewHRs ? (
        <ViewHRs onViewHRs={() => setShowViewHRs(false)} organizationId={selectedOrganizationId} />
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
