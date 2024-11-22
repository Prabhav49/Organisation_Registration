import React, { useState } from 'react';
import axios from 'axios';
import { wait } from '@testing-library/user-event/dist/utils';

const AddOrganization = ({ onAddOrganization }) => { 
    const [name, setName] = useState('');
    const [address, setAddress] = useState('');

    const handleAddOrganization = async () => {
        const token = localStorage.getItem('user');
        if (!token) {
            alert('Please log in first.');
            return;
        }

        try {
            await axios.post(
                'http://localhost:9192/api/v1/organizations/create',
                { name, address },
                { headers: { Authorization: `Bearer ${token}` } }
            );
            wait(100);
            onAddOrganization(); 
        } catch (error) {
            console.error('Failed to add organization:', error);
            alert('Failed to add organization. Please try again.');
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
            <button onClick={() => onAddOrganization()}>Cancel</button>
        </div>
    );
};

export default AddOrganization;
