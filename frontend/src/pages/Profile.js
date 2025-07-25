import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { jwtDecode } from "jwt-decode";
import "../css/Profile.css";
import Modal from "./Modal";
import { BASEURL } from "../helper/helper";

const Profile = () => {
  const [employee, setEmployee] = useState(null);
  const [updatedDetails, setUpdatedDetails] = useState({});
  const [profilePicture, setProfilePicture] = useState(null);
  const navigate = useNavigate();
  const token = localStorage.getItem("user");
  const [modalData, setModalData] = useState({
    isOpen: false,
    title: "",
    message: "",
    isError: false,
    onConfirm: null,
  });

  // const extractErrorMessage = (error) => {
  //   if (error.response?.data?.message) {
  //     return error.response.data.message; 
  //   } else if (typeof error.response?.data === "string") {
  //     return error.response.data; 
  //   } else if (error.message) {
  //     return error.message; 
  //   }
  //   return "An unexpected error occurred";
  // };

  useEffect(() => {
    console.log('Profile page - token from localStorage:', token ? token.substring(0, 20) + '...' : 'null');
    
    if (!token) {
      console.log('No token found, redirecting to login');
      navigate("/login");
      return;
    }

    try {
      const decoded = jwtDecode(token);
      const email = decoded.sub;
      console.log('Decoded email from token:', email);

      axios
        .get(
          `${BASEURL}/api/v1/employees/getEmployeeInfo/${email}`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        )
        .then((response) => {
          console.log('API response received:', response.data);
          setEmployee(response.data);
        })
        .catch((error) => {
          console.error('API call failed:', error);
          console.error('Error details:', error.response?.data);
          console.error('Error status:', error.response?.status);
          navigate("/login");
        });
    } catch (error) {
      console.error("Invalid Token:", error);
      navigate("/login");
    }
  }, [token, navigate]);

  const handleUpdate = () => {
    axios
      .put(
        `${BASEURL}/api/v1/employees/update/${employee.employee_id}`,
        updatedDetails,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      )
      .then(() => {
        setEmployee((prevEmployee) => ({
          ...prevEmployee,
          ...updatedDetails,
        }));
        setModalData({
          isOpen: true,
          title: "Success",
          message: "Profile updated successfully!",
          isError: false,
          onConfirm: null,
        });
      })
      .catch((error) => {
        console.error(error);
        setModalData({
          isOpen: true,
          title: "Error",
          message: "Fields are empty..!",
          isError: true,
          onConfirm: null,
        });
      });
  };

  const handleProfilePictureUpload = () => {
    const formData = new FormData();
    formData.append("file", profilePicture);

    axios
      .post(
        `${BASEURL}/api/v1/employees/uploadImage/${employee.employee_id}`,
        formData,
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "multipart/form-data",
          },
        }
      )
      .then((response) => {
        setModalData({
          isOpen: true,
          title: "Success",
          message: "Profile picture uploaded successfully!",
          isError: false,
          onConfirm: null,
        });
        const updatedEmployee = { ...employee, photograph_path: response.data };

        setEmployee(updatedEmployee);
        setProfilePicture(null);
      })
      .catch((error) => {
        console.error(error);
        setModalData({
          isOpen: true,
          title: "Error",
          message: "Profile picture is missing..!",
          isError: true,
          onConfirm: null,
        });
      });
  };

  const handleDelete = () => {
    setModalData({
      isOpen: true,
      title: "Delete Account",
      message: "Are you sure you want to delete your account?",
      isError: false,
      onConfirm: () => {
        axios
          .delete(
            `${BASEURL}/api/v1/employees/delete/${employee.employee_id}`,
            {
              headers: { Authorization: `Bearer ${token}` },
            }
          )
          .then(() => {
            localStorage.removeItem("user");
            navigate("/login");
          })
          .catch((error) => {
            console.error(error);
            setModalData({
              isOpen: true,
              title: "Error",
              message: "Unable to delete your account try again!",
              isError: true,
              onConfirm: null,
            });
          });
      },
    });
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

  const closeModal = () => {
    setModalData((prevState) => ({ ...prevState, isOpen: false }));
  };

  if (!employee) {
    return <p>Loading...</p>;
  }

  return (
    <div className="profile-container">
    <Modal {...modalData} onClose={closeModal} />
      <div className="profile-layout">
        <div className="profile-info">
          <h1 className="profile-heading">Profile</h1>
          <div className="profile-picture-container">
            <img
              src={
                employee.photograph_path
                  ? `${BASEURL}${employee.photograph_path}`
                  : "/uploads/images/default.png"
              }
              alt={employee.name}
              className="profile-picture"
            />
          </div>
          <div className="employee-details">
            <p>
              <strong>Name:</strong> {employee.first_name} {employee.last_name}
            </p>
            <p>
              <strong>Email:</strong> {employee.email}
            </p>
            <p>
              <strong>Title:</strong> {employee.title || "N/A"}
            </p>
          </div>
          <button className="delete-btn-profile" onClick={handleDelete}>
            Delete Account
          </button>
        </div>

        <div className="profile-actions">
          <div className="update-section">
            <h2>Update Profile</h2>
            <div className="input-group">
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
              <button className="update-btn" onClick={handleUpdate}>
                Update
              </button>
            </div>
          </div>

          <div className="upload-section">
            <h2>Upload Profile Picture</h2>
            <div className="file-input-container">
              <label htmlFor="file-input" className="file-input-label">
                Choose Profile Picture
              </label>
              <input 
                id="file-input"
                type="file" 
                accept="image/*" 
                onChange={handleFileChange} 
              />
            </div>
            <button
              className="upload-btn"
              onClick={handleProfilePictureUpload}
            >
              Upload
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Profile;
