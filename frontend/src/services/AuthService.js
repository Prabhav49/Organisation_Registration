import axios from 'axios';

const API_URL = 'http://localhost:9192/api/v1/auth/login';

const login = (email, password) => {
  return axios
    .post(API_URL, { email, password })
    .then((response) => {
      const token = response.data;

      if (token && token !== "Invalid email or password") {
        localStorage.setItem('user', token);  
        return { token };
      } else {
        throw new Error('Invalid login credentials');
      }
    })
    .catch((error) => {
      throw new Error('Invalid email or password');
    });
};

const logout = () => {
  localStorage.removeItem('user');
};

const AuthService = {
  login,
  logout,
};

export default AuthService;