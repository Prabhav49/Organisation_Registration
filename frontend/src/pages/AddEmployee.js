import React, { useState } from 'react';
import axios from 'axios';
import '../css/AddEmployee.css';
import { BASEURL } from "../helper/helper.js";

const AddEmployee = ({ onAddEmployee }) => {
  const [employee, setEmployee] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    title: 'Employee', 
    photograph_path: '/uploads/images/default.png',
    department_id:1
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setEmployee((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const token = localStorage.getItem('user');

    try {
      const response = await axios.post(
        `${BASEURL}/api/v1/employees/create`,
        employee,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      alert(response.data); 
      onAddEmployee();
    } catch (error) {
      console.error('Failed to add employee:', error);
      alert('Error adding employee. Please try again.');
    }
  };

  return (
    <div className="add-employee-container">
      <h2>Add Employee</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="firstName">First Name</label>
          <input
            type="text"
            name="firstName"
            value={employee.firstName}
            onChange={handleChange}
            placeholder="First Name"
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="lastName">Last Name</label>
          <input
            type="text"
            name="lastName"
            value={employee.lastName}
            onChange={handleChange}
            placeholder="Last Name"
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="email">Email</label>
          <input
            type="email"
            name="email"
            value={employee.email}
            onChange={handleChange}
            placeholder="Email"
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="password">Password</label>
          <input
            type="password"
            name="password"
            value={employee.password}
            onChange={handleChange}
            placeholder="Password"
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="title">Title</label>
          <select
            name="title"
            value={employee.title}
            onChange={handleChange}
          >
            <option value="Employee">Employee</option>
            <option value="Manager">Manager</option>
            <option value="Admin">Admin</option>
          </select>
        </div>
        <div className="button-container">
          <button type="submit" className="submit-btn">Add Employee</button>
        </div>
      </form>
    </div>
  );
};

export default AddEmployee;
