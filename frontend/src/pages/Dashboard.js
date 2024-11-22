import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthService from '../services/AuthService';
import { jwtDecode } from 'jwt-decode';
import axios from 'axios';
import AddOrganization from './AddOrganization';
import Profile from './Profile'; 
import AddHR from './AddHR';
import ViewHRs from './ViewHRs';

const Dashboard = () => {
    const navigate = useNavigate();
    const [email, setEmail] = useState('');
    const [organizations, setOrganizations] = useState([]);
    const [hrList, setHrList] = useState([]);
    const [searchQuery, setSearchQuery] = useState('');
    const [showAddOrganization, setShowAddOrganization] = useState(false);
    const [showProfile, setShowProfile] = useState(false); 
    const [showAddHR, setShowAddHR] = useState(false); 
    const [showViewHRs, setShowViewHRs] = useState(false); 
    const [selectedOrganizationId, setSelectedOrganizationId] = useState(null);

    // Decode token and fetch organizations
    useEffect(() => {
        const token = localStorage.getItem('user');
        if (token) {
            try {
                const decoded = jwtDecode(token);
                setEmail(decoded.sub);
                fetchOrganizations(token);
            } catch (error) {
                console.error('Failed to decode token:', error);
                navigate('/login');
            }
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

    const fetchHrsByName = async (token, name) => {
        try {
            const response = await axios.get(`http://localhost:9192/api/v1/hr/getHrsByName/${name}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            setHrList(response.data);
        } catch (error) {
            console.error('Failed to fetch HRs:', error);
        }
    };

    const fetchOrganizationByName = async (token, name) => {
        try {
            const response = await axios.get(`http://localhost:9192/api/v1/organizations/getByName/${name}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            setOrganizations([response.data]); 
        } catch (error) {
            console.error('Failed to fetch organization:', error);
        }
    };

    const handleSearchChange = async (e) => {
        const query = e.target.value;
        setSearchQuery(query);
        
        const token = localStorage.getItem('user');
        
        if (query.length >= 3) { 
            await fetchOrganizationByName(token, query);
            await fetchHrsByName(token, query);
        } else if (query.length === 0) {  
            fetchOrganizations(token);
            setHrList([]); 
        } else {
            setOrganizations([]);  
            setHrList([]);
        }
    };
    const handleDelete = async (id) => {
        const token = localStorage.getItem('user');

        if (!window.confirm('Are you sure you want to delete this organization?')) return;

        try {
            const response = await axios.delete(`http://localhost:9192/api/v1/organizations/delete/${id}`, {
                headers: { Authorization: `Bearer ${token}` },
            });

            if (response.status === 200) {
                fetchOrganizations(token);
            }
        } catch (error) {
            console.error('Failed to delete organization:', error);
            alert('Failed to delete organization. Please try again.');
        }
    };

    const handleLogout = () => {
        if (email) {
            AuthService.logout(email);
        }
        navigate('/login');
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
        <div>
            <h1>Dashboard</h1>
            <button onClick={() => setShowProfile(!showProfile)}>Profile</button>
            <button onClick={() => setShowAddOrganization(!showAddOrganization)}>Add Organization</button>
            <button onClick={handleLogout}>Logout</button>

            <input
                type="text"
                placeholder="Search Organization or HR"
                value={searchQuery}
                onChange={handleSearchChange}
            />

            {showProfile ? (
                <Profile />
            ) : showAddOrganization ? (
                <AddOrganization onAddOrganization={handleOrganizationAdded} />
            ) : showAddHR ? (
                <AddHR 
                    onAddHR={() => setShowAddHR(false)} 
                    organizationId={selectedOrganizationId} 
                />
            ) : showViewHRs? (
                <ViewHRs 
                    onViewHRs={() => setShowViewHRs(false)} 
                    organizationId={selectedOrganizationId} 
                />
            ) : (
                <div>
                    <h2>Organizations</h2>
                    <table border="1">
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
                                            <button onClick={() => handleDelete(org.id)}>Delete</button>
                                        </td>
                                    </tr>
                                ))
                            ) : (
                                <tr><td colSpan="3">No Organizations found</td></tr>
                            )}
                        </tbody>
                    </table>
                    <h2>HRs</h2>
                    <table border="1">
                        <thead>
                            <tr>
                                <th>Name</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {hrList.length > 0 ? (
                                hrList.map((hr) => (
                                    <tr key={hr.id}>
                                        <td>{hr.name}</td>
                                        <td>
                                            <button onClick={() => handleViewHRs(hr.organizationId)}>View HR Details</button>
                                        </td>
                                    </tr>
                                ))
                            ) : (
                                <tr><td colSpan="2">No HRs found</td></tr>
                            )}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
};

export default Dashboard;
