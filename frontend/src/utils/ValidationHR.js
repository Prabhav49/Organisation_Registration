import axios from 'axios';
import { BASEURL } from '../helper/helper';


export const validateHRForm = async (firstName, lastName, email, contactNumber, organizationId,token) => {
    const errors = {};

    if (!firstName || firstName.length < 2 || firstName.length > 50) {
        errors.firstName = 'First name must be between 2 and 50 characters';
    }

    if (!lastName || lastName.length < 2 || lastName.length > 50) {
        errors.lastName = 'Last name must be between 2 and 50 characters';
    }

    if (!email || !/\S+@\S+\.\S+/.test(email)) {
        errors.email = 'Please enter a valid email address';
    } else {

        try {
            const response = await axios.get(
                `${BASEURL}/api/v1/organizations/getByHrEmail/${email}`,
                { headers: { Authorization: `Bearer ${token}` } }
            );
            if (response.status === 200) {
                errors.email = 'Email already exists';
            }
        } catch (error) {
            if (error.response && error.response.status !== 404) {
                errors.email = 'Error checking email existence';
            }
        }
    }

    if (!contactNumber || !/^[0-9]{10}$/.test(contactNumber)) {
        errors.contactNumber = 'Contact number must be a 10-digit numeric value';
    }

    return errors;
};
