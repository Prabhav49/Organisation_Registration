import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthService from '../services/AuthService';
import { jwtDecode } from 'jwt-decode';

const Dashboard = () => {
    const navigate = useNavigate();
    const [email, setEmail] = useState('');

    useEffect(() => {
        const token = localStorage.getItem('user'); 
        
        if (token) {
            try {
                const decoded = jwtDecode(token); 
                setEmail(decoded.sub);
            } catch (error) {
                console.error('Failed to decode token:', error);
                navigate('/login');
            }
        } else {
            navigate('/login');
        }
    }, [navigate]);

    const handleLogout = () => {

        if (email) {
          AuthService.logout(email)
        } 
        navigate('/login'); 
    };

    return (
        <div>
            <h1>Dashboard</h1>
            <button onClick={() => navigate('/profile')}>Profile</button>
            <button onClick={handleLogout}>Logout</button>
        </div>
    );
};

export default Dashboard;
