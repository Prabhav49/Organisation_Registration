# Outreach Management System

## Overview
Full-stack web application for managing organizational outreach using Spring Boot and React.js.

## Features
- JWT authentication
- **Google OAuth2 SSO login**
- Organization registration
- HR contact management
- Organization search and CRUD operations

## OAuth2 + JWT Authentication Flow

### 🔐 Complete Authentication Architecture

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                           OAUTH2 + JWT AUTHENTICATION FLOW                          │
└─────────────────────────────────────────────────────────────────────────────────────┘

┌──────────────┐    ┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│    User      │    │   React      │    │ Spring Boot  │    │   Google     │
│   Browser    │    │  Frontend    │    │   Backend    │    │   OAuth2     │
│              │    │ (Port 3000)  │    │ (Port 9192)  │    │   Server     │
└──────┬───────┘    └──────┬───────┘    └──────┬───────┘    └──────┬───────┘
       │                   │                   │                   │

🔹 PHASE 1: OAUTH2 LOGIN INITIATION
   [1] │ Click "Continue with Google"                            │
       ├──────────────────→│                   │                   │
   [2] │                   │ GET /oauth2/authorization/google     │
       │                   ├──────────────────→│                   │
   [3] │                   │                   │ Redirect to Google│
       │                   │                   ├──────────────────→│
   [4] │ Redirect to Google OAuth2 Authorization              │
       │←─────────────────────────────────────────────────────────┤

🔹 PHASE 2: GOOGLE AUTHENTICATION  
   [5] │ User authenticates with Google                        │
       ├──────────────────────────────────────────────────────────→│
   [6] │ Google callback with auth code                        │
       │←─────────────────────────────────────────────────────────┤

🔹 PHASE 3: BACKEND OAUTH2 PROCESSING
   [7] │ OAuth2 callback                                      │
       ├─────────────────────────────────────→│                   │
   [8] │                   │ CustomOAuth2UserService          │
       │                   │ • Extract user info              │
       │                   │ • Create/Update employee         │
       │                   │ • Save to database               │
   [9] │                   │ OAuth2AuthenticationSuccessHandler│
       │                   │ • Generate JWT token             │
       │                   │ • Create redirect URL            │
  [10] │ Redirect to /oauth2/redirect?token=JWT&user=email    │
       │←─────────────────────────────────────┤                   │

🔹 PHASE 4: FRONTEND TOKEN STORAGE
  [11] │ OAuth2RedirectHandler                                │
       ├──────────────────→│ • Extract token & email          │
       │                   │ • Store in localStorage          │
       │                   │ • Navigate to /dashboard         │
  [12] │ Redirect to /dashboard                               │
       │←─────────────────┤                   │                   │

🔹 PHASE 5: AUTHENTICATED API CALLS
  [13] │ Protected API Call                                   │
       ├──────────────────→│ Authorization: Bearer {JWT}      │
       │                   ├──────────────────→│                   │
  [14] │                   │ JwtAuthenticationFilter          │
       │                   │ • Validate JWT token             │
       │                   │ • Set authentication context     │
  [15] │                   │ Return protected data             │
       │                   │←─────────────────┤                   │
  [16] │ Display data      │                   │                   │
       │←─────────────────┤                   │                   │
```

### 🏗️ Key Components

#### **Authentication Methods**
- **Local Login**: Email/Password with JWT tokens
- **Google OAuth2**: Social login with JWT tokens  
- **Unified System**: Both methods generate JWT for API access

#### **Backend Components**
- `CustomOAuth2UserService` - Processes Google user data
- `OAuth2AuthenticationSuccessHandler` - Generates JWT after OAuth2 success
- `JwtAuthenticationFilter` - Validates JWT tokens for API calls
- `JwtUtil` - JWT token generation and validation

#### **Frontend Components**
- `GoogleLoginButton` - Initiates OAuth2 flow
- `OAuth2RedirectHandler` - Handles callback and token storage
- `PrivateRoute` - Protects routes with JWT validation

#### **Security Features**
- ✅ Stateless JWT authentication
- ✅ Auto user provisioning from Google
- ✅ CORS protection
- ✅ Protected API endpoints
- ✅ Dual authentication support (Local + OAuth2)

## Technologies
### Backend
- Java 17
- Spring Boot
- Hibernate
- MySQL
- JWT Authentication

### Frontend
- React.js
- Axios

## Setup Instructions

### Prerequisites
- Java 17
- Node.js 
- MySQL
- Google Cloud Console account for OAuth2

### Environment Configuration
⚠️ **Important**: Set up environment variables for OAuth2 credentials

```bash
# Required Environment Variables
export GOOGLE_CLIENT_ID="your-google-client-id"
export GOOGLE_CLIENT_SECRET="your-google-client-secret"
export JWT_SECRET="your-jwt-secret"
export FRONTEND_URL="http://localhost:3000"
```

See `.env.example` for complete environment setup guide.

### Backend Setup
```bash
git clone https://github.com/Prabhav49/Organisation_Registration.git
cd backend

# Set environment variables (see .env.example)
export GOOGLE_CLIENT_ID="your-client-id"
export GOOGLE_CLIENT_SECRET="your-client-secret"

mvn clean install
mvn spring-boot:run
```

### Frontend Setup
```bash
cd frontend
npm install
npm install react-router-dom
npm start
```

## API Documentation
Comprehensive API documentation available at:
[Postman API Documentation](https://documenter.getpostman.com/view/39229910/2sAYBXBWAf)

## Project Structure

### Backend
- `/auth`: Authentication services
- `/controller`: REST API controllers
- `/dto`: Data transfer objects
- `/entity`: Database entities
- `/service`: Business logic

### Frontend
- `/component`: Reusable React components
- `/pages`: Application screens

## Future Enhancements
- Role-based access control
- Pagination
- HR contact profile pictures

## Contributing
1. Fork the repository
2. Create feature branch
3. Commit changes
4. Push to branch
5. Create pull request

## License
© 2024 Prabhav Pandey, IIITB. All Rights Reserved.

## Contact
Prabhav Pandey - Prabhav.Pandey@iiitb.ac.in
