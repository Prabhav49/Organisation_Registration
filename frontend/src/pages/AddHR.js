import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../css/AddHR.css'

const AddHR = ({ onAddHR, organizationId }) => {
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
            const response = await axios.post(
                `http://localhost:9192/api/v1/organizations/${organizationId}/hr/add`,
                { firstName, lastName, email, contactNumber },
                { headers: { Authorization: `Bearer ${token}` } }
            );

            if (response.status === 200) {
                onAddHR();
            } else {
                console.error('Error adding HR:', response.statusText);
            }
        } catch (error) {
            console.error('Failed to add HR:', error);
        }
    };

    return (
        <div className="add-hr-container">
            <h1>Add HR</h1>
            <div className="form-group">
                <label>
                    First Name:
                    <input 
                        type="text" 
                        value={firstName} 
                        onChange={(e) => setFirstName(e.target.value)} 
                        placeholder="Enter first name"
                    />
                </label>
            </div>

            <div className="form-group">
                <label>
                    Last Name:
                    <input 
                        type="text" 
                        value={lastName} 
                        onChange={(e) => setLastName(e.target.value)} 
                        placeholder="Enter last name"
                    />
                </label>
            </div>

            <div className="form-group">
                <label>
                    Email:
                    <input 
                        type="email" 
                        value={email} 
                        onChange={(e) => setEmail(e.target.value)} 
                        placeholder="Enter email"
                    />
                </label>
            </div>

            <div className="form-group">
                <label>
                    Contact Number:
                    <input 
                        type="text" 
                        value={contactNumber} 
                        onChange={(e) => setContactNumber(e.target.value)} 
                        placeholder="Enter contact number"
                    />
                </label>
            </div>

            <div className="button-container">
                <button className="submit-btn" onClick={handleAddHR}>Submit</button>
                <button className="cancel-btn" onClick={() => onAddHR()}>Cancel</button>
            </div>
        </div>
    );
};

export default AddHR;
