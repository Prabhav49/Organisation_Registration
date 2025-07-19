import axios from 'axios';
import { BASEURL } from "../helper/helper.js";

const API_URL = `${BASEURL}/api/v1/auth/login`;
const OAUTH2_URL = `${BASEURL}/api/v1/auth/oauth2`;

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

const oauth2Login = (email, provider) => {
  return axios
    .post(`${OAUTH2_URL}/login`, { email, provider })
    .then((response) => {
      const { token, user } = response.data;
      
      if (token) {
        localStorage.setItem('user', token);
        localStorage.setItem('userEmail', user);
        return { token, user };
      } else {
        throw new Error('OAuth2 login failed');
      }
    })
    .catch((error) => {
      throw new Error('OAuth2 login failed');
    });
};

const logout = () => {
  localStorage.removeItem('user');
  localStorage.removeItem('userEmail');
};

const oauth2Logout = (email) => {
  return axios
    .post(`${OAUTH2_URL}/logout`, { email })
    .then((response) => {
      logout();
      return response.data;
    })
    .catch((error) => {
      // Still logout locally even if server request fails
      logout();
      throw error;
    });
};

const AuthService = {
  login,
  oauth2Login,
  logout,
  oauth2Logout,
};

export default AuthService;