import React, { useState } from 'react';
import axios from 'axios';
import '../css/AddHR.css';  
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
            setTimeout(() => {
                onAddOrganization(); 
            }, 100);
        } catch (error) {
            console.error('Failed to add organization:', error);
            alert('Failed to add organization. Please try again.');
        }
    };

    return (
        <div className="add-hr-container">
            <h1>Add Organization</h1>
            <div className="form-group">
                <label>
                    Name:
                    <input 
                        type="text" 
                        value={name} 
                        onChange={(e) => setName(e.target.value)} 
                        placeholder="Enter organization name"
                    />
                </label>
            </div>

            <div className="form-group">
                <label>
                    Address:
                    <input 
                        type="text" 
                        value={address} 
                        onChange={(e) => setAddress(e.target.value)} 
                        placeholder="Enter organization address"
                    />
                </label>
            </div>

            <div className="button-container">
                <button className="submit-btn" onClick={handleAddOrganization}>Submit</button>
                <button className="cancel-btn" onClick={() => onAddOrganization()}>Cancel</button>
            </div>
        </div>
    );
};

export default AddOrganization;
