import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate, useParams } from 'react-router-dom';

const ViewHRs = () => {
    const { organizationId } = useParams();
    const navigate = useNavigate();
    const [hrList, setHrList] = useState([]);

    useEffect(() => {
        const token = localStorage.getItem('user');
        if (!token) {
            navigate('/login');
        }

        const fetchHRs = async () => {
            try {
                const response = await axios.get(`http://localhost:9192/api/v1/organizations/getHRsByOrganizationId/${organizationId}`, {
                    headers: { Authorization: `Bearer ${token}` },
                });
                setHrList(response.data);
            } catch (error) {
                console.error('Failed to fetch HRs:', error);
            }
        };

        fetchHRs();
    }, [organizationId, navigate]);

    return (
        <div>
            <h1>HRs for Organization ID: {organizationId}</h1>
            <button onClick={() => navigate('/dashboard')}>Back to Dashboard</button>
            <table border="1">
                <thead>
                    <tr>
                        <th>First Name</th>
                        <th>Last Name</th>
                        <th>Email</th>
                        <th>Contact Number</th>
                    </tr>
                </thead>
                <tbody>
                    {hrList.map((hr) => (
                        <tr key={hr.id}>
                            <td>{hr.firstName}</td>
                            <td>{hr.lastName}</td>
                            <td>{hr.email}</td>
                            <td>{hr.contactNumber}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default ViewHRs;
