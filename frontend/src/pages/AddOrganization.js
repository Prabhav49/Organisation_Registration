import React, { useState } from 'react';
import axios from 'axios';
import { validateAddOrganizationForm } from '../utils/validation';
import '../css/AddHR.css';  
import { BASEURL } from '../helper/helper';

const AddOrganization = ({ onAddOrganization }) => {
    const [name, setName] = useState('');
    const [address, setAddress] = useState('');
    const [errors, setErrors] = useState({});
    const [loading, setLoading] = useState(false);

    const handleAddOrganization = async () => {
        setErrors({});
    
        const validationErrors = validateAddOrganizationForm(name, address);
        if (Object.values(validationErrors).some((error) => error)) {
            setErrors(validationErrors);
            return;
        }

        const token = localStorage.getItem('user');
        if (!token) {
            alert('Please log in first.');
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
            onAddOrganization(); // Notify parent component
        } catch (error) {
            console.error('Failed to add organization:', error);
            alert(error.response?.data?.message || 'Failed to add organization. Please try again.');
        } finally {
            setLoading(false);
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
