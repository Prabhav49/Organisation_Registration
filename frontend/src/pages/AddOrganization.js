import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const AddOrganization = () => {
    const navigate = useNavigate();
    const [name, setName] = useState('');
    const [address, setAddress] = useState('');

    const handleAddOrganization = async () => {
        const token = localStorage.getItem('user');
        if (!token) {
            navigate('/login');
            return;
        }

        try {
            await axios.post(
                'http://localhost:9192/api/v1/organizations/create',
                { name, address },
                { headers: { Authorization: `Bearer ${token}` } }
            );
            navigate('/dashboard');
        } catch (error) {
            console.error('Failed to add organization:', error);
        }
    };

    return (
        <div>
            <h1>Add Organization</h1>
            <label>
                Name:
                <input type="text" value={name} onChange={(e) => setName(e.target.value)} />
            </label>
            <br />
            <label>
                Address:
                <input type="text" value={address} onChange={(e) => setAddress(e.target.value)} />
            </label>
            <br />
            <button onClick={handleAddOrganization}>Submit</button>
            <button onClick={() => navigate('/dashboard')}>Cancel</button>
        </div>
    );
};

export default AddOrganization;
