import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthService from '../services/AuthService';
import { jwtDecode } from 'jwt-decode';
import axios from 'axios';

const Dashboard = () => {
    const navigate = useNavigate();
    const [email, setEmail] = useState('');
    const [organizations, setOrganizations] = useState([]);

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

    const handleDelete = async (id) => {
        const token = localStorage.getItem('user');

        if (!window.confirm('Are you sure you want to delete this organization?')) return;

        try {
            const response = await axios.delete(`http://localhost:9192/api/v1/organizations/delete/${id}`, {
                headers: { Authorization: `Bearer ${token}` },
            });

            if (response.status === 200) {
                alert('Organization deleted successfully!');
                fetchOrganizations(token); // Refresh the list after deletion
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

    return (
        <div>
            <h1>Dashboard</h1>
            <button onClick={() => navigate('/profile')}>Profile</button>
            <button onClick={() => navigate('/add-organization')}>Add Organization</button>
            <button onClick={handleLogout}>Logout</button>
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
                        {organizations.map((org) => (
                            <tr key={org.id}>
                                <td>{org.name}</td>
                                <td>{org.address}</td>
                                <td>
                                    <button onClick={() => navigate(`/add-hr/${org.id}`)}>Add HR</button>
                                    <button onClick={() => navigate(`/view-hrs/${org.id}`)}>View HRs</button>
                                    <button onClick={() => handleDelete(org.id)}>Delete</button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default Dashboard;
