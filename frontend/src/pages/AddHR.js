import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { validateHRForm } from '../utils/ValidationHR'; 
import '../css/AddHR.css';
import { BASEURL } from "../helper/helper.js";
import { jwtDecode } from 'jwt-decode';

const AddHR = ({ onAddHR, organizationId }) => {
    const navigate = useNavigate();
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [email, setEmail] = useState('');
    const [contactNumber, setContactNumber] = useState('');
    const [errors, setErrors] = useState({});
    const [showModal, setShowModal] = useState(false); 

    const validateToken = (token) => {
        try {
            const decoded = jwtDecode(token);
            const currentTime = Date.now() / 1000; 
            if (decoded.exp < currentTime) {
                return false; 
            }
            return true;
        } catch (error) {
            console.error('Failed to decode token:', error);
            return false;
        }
    };

    useEffect(() => {
        const token = localStorage.getItem('user');
        if (!token || !validateToken(token)) {
            setShowModal(true); 
        }
    }, []);

    const handleAddHR = async () => {
        setErrors({});
        const token = localStorage.getItem('user');

        if (!token || !validateToken(token)) {
            alert('Session has expired. Please log in again.');
            navigate('/login'); // Navigate to login if token is expired
            return;
        }

        const validationErrors = await validateHRForm(firstName, lastName, email, contactNumber, organizationId, token);
        if (Object.keys(validationErrors).length > 0) {
            setErrors(validationErrors); 
            return;
        }

        try {
            await axios.post(
                `${BASEURL}/api/v1/organizations/${organizationId}/hr/add`,
                { firstName, lastName, email, contactNumber },
                { headers: { Authorization: `Bearer ${token}` } }
            );
            onAddHR();
        } catch (error) {
            console.error('Failed to add HR:', error);
            alert(error.response?.data?.message || 'Failed to add HR. Please try again.');
        }
    };

    return (
        <div className="add-hr-container">
            {/* Modal for expired session */}
            {showModal && (
                <div className="error-modal-overlay">
                    <div className="error-modal">
                        <div className="modal-content">
                            <h3>Your session has expired. Please log in again.</h3>
                            <button onClick={() => navigate('/login')}>Go to Login</button>
                        </div>
                    </div>
                </div>
            )}

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
                {errors.firstName && <p className="error-text">{errors.firstName}</p>}
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
                {errors.lastName && <p className="error-text">{errors.lastName}</p>}
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
                {errors.email && <p className="error-text">{errors.email}</p>}
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
                {errors.contactNumber && <p className="error-text">{errors.contactNumber}</p>}
            </div>

            <div className="button-container">
                <button className="submit-btn" onClick={handleAddHR}>Submit</button>
                <button className="cancel-btn" onClick={() => onAddHR()}>Cancel</button>
            </div>
        </div>
    );
};

export default AddHR;
