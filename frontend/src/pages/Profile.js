import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';

const Profile = () => {
    const [employee, setEmployee] = useState(null);
    const [updatedDetails, setUpdatedDetails] = useState({});
    const [profilePicture, setProfilePicture] = useState(null);
    const [oldPassword, setOldPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const navigate = useNavigate();
    const token = localStorage.getItem('user');

    useEffect(() => {
        if (!token) {
            navigate('/login');
            return;
        }

        try {
            const decoded = jwtDecode(token); // Decoding JWT
            const email = decoded.sub; // Use the 'sub' field for email
            console.log('Decoded Token:', decoded);

            axios
                .get(`http://localhost:9192/api/v1/employees/getEmployeeInfo/${email}`, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                })
                .then((response) => setEmployee(response.data))
                .catch((error) => {
                    console.error(error);
                    navigate('/login');
                });
        } catch (error) {
            console.error('Invalid Token:', error);
            navigate('/login');
        }
    }, [token, navigate]);

    const handleUpdate = () => {
        if (!employee) return;

        axios
            .put(`http://localhost:9192/api/v1/employees/update/${employee.employee_id}`, updatedDetails, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            })
            .then(() => alert('Profile updated successfully!'))
            .catch((error) => {
                console.error(error);
                alert(error.response?.data || 'Failed to update profile');
            });
    };

    const handleProfilePictureUpload = () => {
        if (!profilePicture || !employee) return;
    
        const formData = new FormData();
        formData.append('file', profilePicture);
    
        axios
            .post(`http://localhost:9192/api/v1/employees/uploadProfilePicture/${employee.employee_id}`, formData, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'multipart/form-data',
                },
            })
            .then((response) => {
                console.log('Upload Response:', response.data);
                alert(response.data.message || 'Profile picture uploaded successfully!');
            })
            .catch((error) => {
                console.error('Upload Error:', error.response?.data || error.message);
                alert(error.response?.data?.message || 'Failed to upload profile picture');
            });
    };

    const handleDelete = () => {
        if (!employee) return;

        if (window.confirm('Are you sure you want to delete your account?')) {
            axios
                .delete(`http://localhost:9192/api/v1/employees/delete/${employee.employee_id}`, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                })
                .then(() => {
                    localStorage.removeItem('user');
                    navigate('/login');
                })
                .catch((error) => {
                    console.error(error);
                    alert(error.response?.data || 'Failed to delete account');
                });
        }
    };

    const handleInputChange = (e) => {
        setUpdatedDetails({
            ...updatedDetails,
            [e.target.name]: e.target.value,
        });
    };

    const handleFileChange = (e) => {
        setProfilePicture(e.target.files[0]);
    };

    const handleChangePassword = () => {
        if (!oldPassword || !newPassword || !confirmPassword) {
            alert('All fields are required!');
            return;
        }
    
        if (newPassword !== confirmPassword) {
            alert('New passwords do not match!');
            return;
        }
    
        if (newPassword.length < 6) {
            alert('New password must be at least 8 characters long!');
            return;
        }
    
        axios
            .put(
                `http://localhost:9192/api/v1/employees/changePassword/${employee.employee_id}`,
                { oldPassword, newPassword, confirmPassword },
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                }
            )
            .then((response) => {
                alert('Password changed successfully');
                setOldPassword('');
                setNewPassword('');
                setConfirmPassword('');
            })
            .catch((error) => {
                console.error(error);
    
                if (error.response && error.response.status === 401) {
                    alert('Old password is incorrect');
                } else {
                    alert(error.response?.data || 'Failed to change password');
                }
            });
    };
    
    
    if (!employee) {
        return <p>Loading...</p>;
    }

    return (
        <div>
            <h1>Profile</h1>
            <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                <img
                    src={employee.photograph_path ? `http://localhost:9192${employee.photograph_path}` : '/default-profile.png'}
                    alt="Profile"
                    style={{
                        width: '100px',
                        height: '100px',
                        borderRadius: '50%',
                        objectFit: 'cover',
                    }}
                />
                <div>
                    <p><strong>First Name:</strong> {employee.first_name}</p>
                    <p><strong>Last Name:</strong> {employee.last_name}</p>
                    <p><strong>Email:</strong> {employee.email}</p>
                    <p><strong>Title:</strong> {employee.title || 'N/A'}</p>
                </div>
            </div>

            <h2>Update Profile</h2>
            <div>
                <input
                    type="text"
                    name="first_name"
                    placeholder="First Name"
                    onChange={handleInputChange}
                />
                <input
                    type="text"
                    name="last_name"
                    placeholder="Last Name"
                    onChange={handleInputChange}
                />
                <input
                    type="text"
                    name="title"
                    placeholder="Title"
                    onChange={handleInputChange}
                />
                <button onClick={handleUpdate}>Update</button>
            </div>

            <h2>Upload Profile Picture</h2>
            <div>
                <input
                    type="file"
                    accept="image/*"
                    onChange={handleFileChange}
                />
                <button onClick={handleProfilePictureUpload}>Upload</button>
            </div>

            <h2>Change Password</h2>
            <div>
                <input
                    type="password"
                    placeholder="Old Password"
                    value={oldPassword}
                    onChange={(e) => setOldPassword(e.target.value)}
                />
                <input
                    type="password"
                    placeholder="New Password"
                    value={newPassword}
                    onChange={(e) => setNewPassword(e.target.value)}
                />
                <input
                    type="password"
                    placeholder="Confirm New Password"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                />
                <button onClick={handleChangePassword}>Change Password</button>
            </div>

            <button onClick={handleDelete} style={{ marginTop: '1rem', color: 'red' }}>
                Delete Account
            </button>
        </div>
    );
};

export default Profile;
