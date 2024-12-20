import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import '../css/ViewHRs.css'
import { BASEURL } from '../helper/helper';
// import {validateHRForm} from '../utils/ValidationHR'

const ViewHRs = ({ organizationId, onViewHRs }) => {
    const navigate = useNavigate();
    const [searchQuery, setSearchQuery] = useState('');
    const [hrList, setHrList] = useState([]);
    const [organizationName, setOrganizationName] = useState('');
    const [editingHrId, setEditingHrId] = useState(null);
    const [editFormData, setEditFormData] = useState({
        firstName: '',
        lastName: '',
        email: '',
        contactNumber: '',
    });
    const [showDeleteConfirmation, setShowDeleteConfirmation] = useState(false);
    const [hrToDelete, setHrToDelete] = useState(null);
    const token = localStorage.getItem('user');

    useEffect(() => {
        if (!token) {
            navigate('/login');
        }
    
        const fetchOrganizationDetails = async () => {
            try {
                const response = await axios.get(`${BASEURL}/api/v1/organizations/getAll`, {
                    headers: { Authorization: `Bearer ${token}` },
                });
                const organization = response.data.find((org) => org.id === parseInt(organizationId));
                if (organization) {
                    setOrganizationName(organization.name);
                }
            } catch (error) {
                console.error('Failed to fetch organization details:', error);
            }
        };
    
        const fetchHRs = async () => {
            try {
                const response = await axios.get(`${BASEURL}/api/v1/organizations/${organizationId}/hr/getAll`, {
                    headers: { Authorization: `Bearer ${token}` },
                });
                setHrList(response.data);
            } catch (error) {
                console.error('Failed to fetch HRs:', error);
            }
        };
    
        fetchOrganizationDetails();
        fetchHRs();
    }, [organizationId, navigate, token]); 
    

    const fetchHRs = async () => {
        try {
            const response = await axios.get(`${BASEURL}/api/v1/organizations/${organizationId}/hr/getAll`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            setHrList(response.data);
        } catch (error) {
            console.error('Failed to fetch HRs:', error);
        }
    };
    const handleEditClick = (hr) => {
        setEditingHrId(hr.id);
        setEditFormData(hr);
    };

    const handleCancelEdit = () => {
        setEditingHrId(null);
        setEditFormData({
            firstName: '',
            lastName: '',
            email: '',
            contactNumber: '',
        });
    };

    const handleEditFormChange = (e) => {
        const { name, value } = e.target;
        setEditFormData((prevData) => ({
            ...prevData,
            [name]: value,
        }));
    };

    const handleEditFormSubmit = async (e) => {
        e.preventDefault();
        const token = localStorage.getItem('user');
    

    
        try {
            const payload = {
                first_name: editFormData.firstName,
                last_name: editFormData.lastName,
                email: editFormData.email,
                contact_number: editFormData.contactNumber,
            };
    
            const response = await axios.put(
                `${BASEURL}/api/v1/organizations/${organizationId}/hr/update/${editingHrId}`,
                payload,
                { headers: { Authorization: `Bearer ${token}` } }
            );
    
            if (response.status === 200 || response.status === 204) {
                setHrList((prevList) =>
                    prevList.map((hr) => (hr.id === editingHrId ? { ...hr, ...editFormData } : hr))
                );
                handleCancelEdit();
            } else {
                console.error('Unexpected response status:', response.status);
            }
        } catch (error) {
            console.error('Failed to update HR details:', error.response?.data || error.message);
        }
    };

    const handleDeleteClick = (hrId) => {
        setHrToDelete(hrId);
        setShowDeleteConfirmation(true);
    };

    const handleDeleteConfirm = async () => {
        const token = localStorage.getItem('user');
        if (!token) {
            navigate('/login');
            return;
        }
        try {
            const response = await axios.delete(
                `${BASEURL}/api/v1/organizations/${organizationId}/hr/delete/${hrToDelete}`,
                { headers: { Authorization: `Bearer ${token}` } }
            );

            if (response.status === 200) {
                setHrList((prevList) => prevList.filter((hr) => hr.id !== hrToDelete));
            } else {
                console.error('Failed to delete HR:', response.status);
            }
            setShowDeleteConfirmation(false);
        } catch (error) {
            console.error('Error deleting HR:', error.response?.data || error.message);
            setShowDeleteConfirmation(false);
        }
    };

    const handleDeleteCancel = () => {
        setShowDeleteConfirmation(false);
    };

    const handleSearchChange = async (e) => {
        const query = e.target.value.trim();
        setSearchQuery(query);
      
        const token = localStorage.getItem('user');
      
        if (query.length >= 1) {
          try {
            const response = await axios.get(`${BASEURL}/api/v1/organizations/${organizationId}/hr/getByName/${query}`, {
              headers: { Authorization: `Bearer ${token}` },
            });
            setHrList(response.data); 
          } catch (error) {
            console.error('Failed to fetch organizations:', error);
          }
        } else if (query.length === 0) {
            fetchHRs(token);
        } else {
          setHrList([]);
        }
      };
    

    return (
        <div className="view-hrs">
            <h1>HRs for Organization: {organizationName}</h1>
            <button className="back-btn" onClick={() => onViewHRs()}>Back to Dashboard</button>

            <div className="search-bar-hr">
            <input
                type="text"
                placeholder="Search HR"
                value={searchQuery}
                onChange={handleSearchChange}
                className="search-bar"
            />
            </div>

            {hrList.length === 0 ? (
                <h3>No HR records have been found. Please add HR details to continue.</h3>
            ) : (
                <table className="hr-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>First Name</th>
                            <th>Last Name</th>
                            <th>Email</th>
                            <th>Contact Number</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {hrList.map((hr) => (
                            <tr key={hr.id}>
                                {editingHrId === hr.id ? (
                                    <>
                                        <td>
                                            {hr.id}
                                        </td>
                                        <td>
                                            <input
                                                type="text"
                                                name="firstName"
                                                value={editFormData.firstName}
                                                onChange={handleEditFormChange}
                                            />
                                        </td>
                                        <td>
                                            <input
                                                type="text"
                                                name="lastName"
                                                value={editFormData.lastName}
                                                onChange={handleEditFormChange}
                                            />
                                        </td>
                                        <td>
                                            <input
                                                type="email"
                                                name="email"
                                                value={editFormData.email}
                                                onChange={handleEditFormChange}
                                            />
                                        </td>
                                        <td>
                                            <input
                                                type="text"
                                                name="contactNumber"
                                                value={editFormData.contactNumber}
                                                onChange={handleEditFormChange}
                                            />
                                        </td>
                                        <td>
                                            <button onClick={handleEditFormSubmit}>Save</button>
                                            <button onClick={handleCancelEdit}>Cancel</button>
                                        </td>
                                    </>
                                ) : (
                                    <>
                                        <td>{hr.id}</td>
                                        <td>{hr.firstName}</td>
                                        <td>{hr.lastName}</td>
                                        <td>{hr.email}</td>
                                        <td>{hr.contactNumber}</td>
                                        <td>
                                            <button className="edit-btn" onClick={() => handleEditClick(hr)}>Edit</button>
                                            <button className="delete-btn" onClick={() => handleDeleteClick(hr.id)}>Delete</button>
                                        </td>
                                    </>
                                )}
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}

            {/* Delete Confirmation Modal */}
            {showDeleteConfirmation && (
                <div className="delete-confirmation-modal">
                    <div className="modal-content">
                        <p>Are you sure you want to delete this HR?</p>
                        <button onClick={handleDeleteConfirm}>Yes</button>
                        <button onClick={handleDeleteCancel}>No</button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default ViewHRs;