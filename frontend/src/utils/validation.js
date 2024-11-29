// Common Validation Functions
export const validateEmail = (email) => {
    const emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
    return emailPattern.test(email);
};

export const validatePassword = (password) => {
    return password.length >= 6;
};

export const validateNotEmpty = (input, fieldName) => {
    if (!input.trim()) {
        return `${fieldName} is required.`;
    }
    return null;
};

export const validateContactNumber = (contactNumber) => {
    if (!contactNumber || !contactNumber.trim()) { 
        return 'Contact Number is required.';
    } else if (!/^\d+$/.test(contactNumber)) {
        return 'Contact Number can only contain digits.';
    } else if (contactNumber.length !== 10) {
        return 'Contact Number must be exactly 10 digits long.';
    }
    return null;
};
export const validateName = (name, fieldName) => {
    const nameStr = String(name);

    if (!nameStr.trim()) {
        return `${fieldName} is required.`;
    } else if (!/^[a-zA-Z]+$/.test(nameStr)) {
        return `${fieldName} can only contain letters.`;
    }
    return null;
};


// Login Validation
export const validateLoginForm = (email, password) => {
    const errors = {};
    if (!validateEmail(email)) {
        errors.email = 'Please enter a valid email address.';
    }
    if (!validatePassword(password)) {
        errors.password = 'Password must be at least 6 characters long.';
    }
    return errors;
};

// Add Organization Validation
export const validateAddOrganizationForm = (name, address) => {
    const errors = {};
    errors.name = validateNotEmpty(name, 'Organization Name');
    errors.address = validateNotEmpty(address, 'Address');
    return Object.fromEntries(Object.entries(errors).filter(([, value]) => value));
};

// Add HR Validation
export const validateAddHRForm = (firstName, lastName, email, contactNumber) => {
    const errors = {};
    errors.firstName = validateName(firstName, 'First Name');
    errors.lastName = validateName(lastName, 'Last Name');
    errors.email = validateEmail(email) ? null : 'Please enter a valid email address.';
    errors.contactNumber = validateContactNumber(contactNumber);
    return Object.fromEntries(Object.entries(errors).filter(([, value]) => value));
};


const validations = {
    Login: { validateLoginForm },
    AddOrganizations: { validateAddOrganizationForm },
    AddHR: { validateAddHRForm },
};

export default validations;
