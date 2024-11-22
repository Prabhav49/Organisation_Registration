import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const ViewHRs = ({ organizationId, onViewHRs }) => {
    const navigate = useNavigate();
    const [hrList, setHrList] = useState([]);
    const [organizationName, setOrganizationName] = useState('');
    const [editingHrId, setEditingHrId] = useState(null);
    const [editFormData, setEditFormData] = useState({
        firstName: '',
        lastName: '',
        email: '',
        contactNumber: '',
    });
    const [emailToSearch, setEmailToSearch] = useState('');
    const [searchedHr, setSearchedHr] = useState(null); // New state to hold HR details fetched by email

    useEffect(() => {
        const token = localStorage.getItem('user');
        if (!token) {
            navigate('/login');
        }

        const fetchOrganizationDetails = async () => {
            try {
                const response = await axios.get(`http://localhost:9192/api/v1/organizations/getAll`, {
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
                const response = await axios.get(`http://localhost:9192/api/v1/organizations/${organizationId}/hr/getAll`, {
                    headers: { Authorization: `Bearer ${token}` },
                });
                setHrList(response.data);
            } catch (error) {
                console.error('Failed to fetch HRs:', error);
            }
        };

        fetchOrganizationDetails();
        fetchHRs();
    }, [organizationId, navigate]);

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
                `http://localhost:9192/api/v1/organizations/${organizationId}/hr/update/${editingHrId}`,
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

    const handleDeleteClick = async (hrId) => {
        const token = localStorage.getItem('user');
        if (!token) {
            navigate('/login');
            return;
        }
        if (!window.confirm('Are you sure you want to delete the HR information?')) return;
        try {
            const response = await axios.delete(
                `http://localhost:9192/api/v1/organizations/${organizationId}/hr/delete/${hrId}`,
                { headers: { Authorization: `Bearer ${token}` } }
            );

            if (response.status === 200) {
                setHrList((prevList) => prevList.filter((hr) => hr.id !== hrId));
            } else {
                console.error('Failed to delete HR:', response.status);
            }
        } catch (error) {
            console.error('Error deleting HR:', error.response?.data || error.message);
        }
    };

    const handleSearchByEmail = async () => {
        const token = localStorage.getItem('user');
        if (!token) {
            navigate('/login');
            return;
        }

        try {
            const response = await axios.get(
                `http://localhost:9192/api/v1/organizations/${organizationId}/hr/getByEmail/${emailToSearch}`,
                { headers: { Authorization: `Bearer ${token}` } }
            );
            setSearchedHr(response.data);
        } catch (error) {
            console.error('Error fetching HR by email:', error.response?.data || error.message);
            setSearchedHr(null); // Reset if error occurs
        }
    };

    return (
        <div>
            <h1>HRs for Organization: {organizationName}</h1>
            <button onClick={() => onViewHRs()}>Back to Dashboard</button>

            <div>
                <input
                    type="email"
                    placeholder="Search HR by email"
                    value={emailToSearch}
                    onChange={(e) => setEmailToSearch(e.target.value)}
                />
                <button onClick={handleSearchByEmail}>Search</button>
            </div>

            {searchedHr ? (
                <div>
                    <h3>HR Details:</h3>
                    <p>First Name: {searchedHr.firstName}</p>
                    <p>Last Name: {searchedHr.lastName}</p>
                    <p>Email: {searchedHr.email}</p>
                    <p>Contact Number: {searchedHr.contactNumber}</p>
                </div>
            ) : (
                <p>No HR found for this email.</p>
            )}

            {hrList.length === 0 ? (
                <h3>No HR records have been found. Please add HR details to continue.</h3>
            ) : (
                <table border="1">
                    <thead>
                        <tr>
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
                                        <td>{hr.firstName}</td>
                                        <td>{hr.lastName}</td>
                                        <td>{hr.email}</td>
                                        <td>{hr.contactNumber}</td>
                                        <td>
                                            <button onClick={() => handleEditClick(hr)}>Edit</button>
                                            <button onClick={() => handleDeleteClick(hr.id)}>Delete</button>
                                        </td>
                                    </>
                                )}
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
        </div>
    );
};

export default ViewHRs;
