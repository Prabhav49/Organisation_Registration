import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { validateAddOrganizationForm } from '../utils/validation';
import '../css/AddHR.css';  
import { BASEURL } from '../helper/helper';
import { useNavigate } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';

const AddOrganization = ({ onAddOrganization }) => {
    const [name, setName] = useState('');
    const [address, setAddress] = useState('');
    const [errors, setErrors] = useState({});
    const [loading, setLoading] = useState(false);
    const [showModal, setShowModal] = useState(false); 
    const navigate = useNavigate();

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

    const handleAddOrganization = async () => {
        setErrors({});
    
        const validationErrors = validateAddOrganizationForm(name, address);
        if (Object.values(validationErrors).some((error) => error)) {
            setErrors(validationErrors);
            return;
        }

        const token = localStorage.getItem('user');
        if (!token || !validateToken(token)) {
            alert('Session has expired. Please log in again.');
            navigate('/login'); // Navigate to login if token is expired
            return;
        }

        try {
            setLoading(true);
            await axios.post(
                `${BASEURL}/api/v1/organizations/create`,
                { name, address },
                { headers: { Authorization: `Bearer ${token}` } }
            );
            setName('');
            setAddress('');
            onAddOrganization();
        } catch (error) {
            console.error('Failed to add organization:', error);
            alert(error.response?.data?.message || 'Failed to add organization. Please try again.');
        } finally {
            setLoading(false);
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
                {errors.name && <p className="error-message">{errors.name}</p>}
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
                {errors.address && <p className="error-message">{errors.address}</p>}
            </div>

            <div className="button-container">
                <button 
                    className="submit-btn" 
                    onClick={handleAddOrganization} 
                    disabled={loading}
                >
                    {loading ? 'Submitting...' : 'Submit'}
                </button>
                <button className="cancel-btn" onClick={() => onAddOrganization()}>Cancel</button>
            </div>
        </div>
    );
};

export default AddOrganization;
