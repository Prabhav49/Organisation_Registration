import React, { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';

const AddHR = () => {
    const { organizationId } = useParams();
    const navigate = useNavigate();
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [email, setEmail] = useState('');
    const [contactNumber, setContactNumber] = useState('');

    const handleAddHR = async () => {
        const token = localStorage.getItem('user');
        if (!token) {
            navigate('/login');
            return;
        }

        try {
            await axios.post(
                `http://localhost:9192/api/v1/organizations/${organizationId}/hr/add`,
                { firstName, lastName, email, contactNumber },
                { headers: { Authorization: `Bearer ${token}` } }
            );
            navigate('/dashboard');
        } catch (error) {
            console.error('Failed to add HR:', error);
        }
    };

    return (
        <div>
            <h1>Add HR</h1>
            <label>
                First Name:
                <input type="text" value={firstName} onChange={(e) => setFirstName(e.target.value)} />
            </label>
            <br />
            <label>
                Last Name:
                <input type="text" value={lastName} onChange={(e) => setLastName(e.target.value)} />
            </label>
            <br />
            <label>
                Email:
                <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} />
            </label>
            <br />
            <label>
                Contact Number:
                <input type="text" value={contactNumber} onChange={(e) => setContactNumber(e.target.value)} />
            </label>
            <br />
            <button onClick={handleAddHR}>Submit</button>
            <button onClick={() => navigate('/dashboard')}>Cancel</button>
        </div>
    );
};

export default AddHR;
