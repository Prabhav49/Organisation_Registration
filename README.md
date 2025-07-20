# Organization Registration & Outreach Management System

## Overview
A comprehensive full-stack web application for managing organizational outreach and employee registration. Built with Spring Boot backend, React.js frontend, and integrated Google OAuth2 authentication alongside traditional JWT-based login.

## 🚀 Features
- **Dual Authentication System**: Local JWT authentication + Google OAuth2 SSO
- **Two-Factor Authentication (2FA)**: TOTP-based security using QR codes
- **Advanced Security**: Password policies, account lockout, audit logging
- **Admin User Management**: Super admin controls for user roles, 2FA, and accounts
- **Organization Management**: Complete CRUD operations for organizations
- **HR Contact Management**: Manage HR contacts for each organization  
- **Employee Management**: Employee registration and profile management
- **Security Dashboard**: 2FA setup, password management, security settings
- **Audit Trail**: Comprehensive logging of all user actions and security events
- **Secure API**: JWT-protected REST endpoints with role-based access
- **Responsive UI**: Modern React.js frontend with professional styling
- **File Upload**: Profile picture and document upload support

## 🏗️ System Architecture

```mermaid
graph TB
    subgraph "Frontend (React.js - Port 3000)"
        A[Login Page]
        B[Dashboard]
        C[Organization Management]
        D[HR Management]
        E[Employee Profile]
        F[Security Settings]
        G[2FA Setup/Verify]
        H[Admin Users Panel]
    end
    
    subgraph "Backend (Spring Boot - Port 9192)"
        I[Authentication Controller]
        J[Organization Controller]
        K[HR Controller]
        L[Employee Controller]
        M[Admin Controller]
        N[JWT Service]
        O[OAuth2 Service]
        P[2FA Service]
        Q[Audit Service]
        R[Password Policy Service]
    end
    
    subgraph "Database"
        S[(MySQL Database)]
        T[Employee Table]
        U[TwoFactorAuth Table]
        V[AuditLog Table]
        W[Organization Table]
        X[PasswordHistory Table]
    end
    
    subgraph "External Services"
        Y[Google OAuth2]
        Z[TOTP Authenticator Apps]
    end
    
    A --> I
    B --> J
    C --> J
    D --> K
    E --> L
    F --> P
    G --> P
    H --> M
    I --> N
    I --> O
    I --> P
    M --> Q
    P --> Q
    I --> R
    O --> Y
    P --> Z
    N --> S
    Q --> V
    R --> X
    J --> W
    L --> T
    P --> U
    K --> M
    G --> L
    H --> L
    I --> L
    J --> L
```

## � Security Architecture

```mermaid
graph TB
    subgraph "Authentication Layer"
        A1[JWT Authentication]
        A2[Google OAuth2]
        A3[Two-Factor Authentication]
        A4[Session Management]
    end
    
    subgraph "Authorization Layer"
        B1[Role-Based Access Control]
        B2[EMPLOYEE Role]
        B3[ADMIN Role]
        B4[SUPER_ADMIN Role]
    end
    
    subgraph "Security Features"
        C1[Password Policies]
        C2[Account Lockout]
        C3[Password History]
        C4[TOTP 2FA]
        C5[QR Code Generation]
        C6[Backup Codes]
    end
    
    subgraph "Monitoring & Audit"
        D1[Audit Logging]
        D2[Security Events]
        D3[Login Attempts]
        D4[Admin Actions]
        D5[Failed Operations]
    end
    
    subgraph "Admin Controls"
        E1[User Management]
        E2[Force 2FA Enable/Disable]
        E3[Account Lock/Unlock]
        E4[Role Assignment]
        E5[Audit Trail Review]
    end
    
    A1 --> B1
    A2 --> B1
    A3 --> B1
    A4 --> D1
    B2 --> C1
    B3 --> E1
    B4 --> E1
    C4 --> C5
    C4 --> C6
    E1 --> D4
    D1 --> D2
    D1 --> D3
    D1 --> D5
    E5 --> D1
    
    style A1 fill:#e1f5fe
    style A2 fill:#e1f5fe
    style A3 fill:#f3e5f5
    style C4 fill:#f3e5f5
    style D1 fill:#fff3e0
    style E1 fill:#e8f5e8
```

## �🔐 Authentication Flow

```mermaid
sequenceDiagram
    participant User
    participant Frontend
    participant Backend
    participant Database
    participant Google
    participant Authenticator
    
    Note over User,Authenticator: Local Authentication + 2FA Flow
    User->>Frontend: Enter credentials
    Frontend->>Backend: POST /auth/login
    Backend->>Database: Validate credentials
    Database-->>Backend: User data (2FA enabled?)
    
    alt 2FA Enabled
        Backend-->>Frontend: Require 2FA verification
        Frontend-->>User: Show 2FA input
        User->>Authenticator: Get TOTP code
        Authenticator-->>User: 6-digit code
        User->>Frontend: Enter TOTP code
        Frontend->>Backend: POST /auth/verify-2fa
        Backend->>Database: Validate TOTP
        Database-->>Backend: Verification result
        Backend-->>Frontend: JWT Token
    else 2FA Disabled
        Backend-->>Frontend: JWT Token
    end
    Frontend-->>User: Dashboard access
    
    Note over User,Authenticator: Google OAuth2 Flow
    User->>Frontend: Click "Login with Google"
    Frontend->>Backend: Redirect to /oauth2/authorization/google
    Backend->>Google: OAuth2 authorization request
    Google-->>User: Google login page
    User->>Google: Enter Google credentials
    Google->>Backend: Authorization code (callback)
    Backend->>Google: Exchange code for tokens
    Google-->>Backend: Access token + user info
    Backend->>Database: Create/update user (EMPLOYEE role)
    Backend-->>Frontend: JWT Token + redirect
    Frontend-->>User: Dashboard access
```

## 🛠️ Technologies

### Backend
- **Java 17** - Programming language
- **Spring Boot 3.3.5** - Application framework
- **Spring Security** - Authentication & authorization
- **Spring Data JPA** - Data persistence
- **Hibernate** - ORM framework
- **MySQL** - Database
- **JWT** - Token-based authentication
- **OAuth2** - Google SSO integration
- **Maven** - Dependency management

### Frontend
- **React.js 18** - UI framework
- **React Router DOM** - Client-side routing
- **Axios** - HTTP client
- **CSS3** - Styling
- **JavaScript ES6+** - Programming language

## 🔐 Security Features

### Two-Factor Authentication (2FA)
- **TOTP Implementation**: Time-based One-Time Password using industry standards
- **QR Code Setup**: Easy setup with Google Authenticator, Authy, or similar apps
- **Backup Codes**: Recovery options for account access
- **Mandatory 2FA**: Admin can enforce 2FA for enhanced security

### Password Security
- **Complex Password Policies**: Minimum length, special characters, numbers
- **Password History**: Prevents reuse of recent passwords
- **Secure Hashing**: BCrypt with salt for password storage
- **Password Change Flow**: Secure current password verification

### Account Protection
- **Account Lockout**: Automatic lockout after failed login attempts
- **Session Management**: Secure JWT token handling
- **Role-Based Access**: EMPLOYEE, ADMIN, SUPER_ADMIN roles
- **Admin Controls**: Super admin can lock/unlock accounts and manage 2FA

### Audit & Monitoring
- **Comprehensive Logging**: All user actions tracked with timestamps
- **Security Events**: Login attempts, 2FA setup, admin actions
- **Audit Trail**: Filterable logs by action, status, date range
- **Admin Dashboard**: Real-time monitoring of security events

### Data Protection
- **JWT Security**: Short-lived tokens with role-based claims
- **OAuth2 Integration**: Secure Google SSO with proper scopes
- **Input Validation**: Frontend and backend validation
- **Error Handling**: Secure error messages without data leakage

## ⚙️ Google OAuth2 Configuration

### Step 1: Google Cloud Console Setup
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable the **Google+ API** and **Google OAuth2 API**

### Step 2: Create OAuth2 Credentials
1. Navigate to **"Credentials"** → **"Create Credentials"** → **"OAuth 2.0 Client IDs"**
2. Choose **"Web application"** as application type
3. Set **Authorized JavaScript origins**:
   ```
   http://localhost:3000
   ```
4. Set **Authorized redirect URIs**:
   ```
   http://localhost:9192/oauth2/callback/google
   ```
5. Click **"Create"** and copy the **Client ID** and **Client Secret**

### Step 3: Environment Variables Setup
Create a `.env` file in the project root or export environment variables:

```bash
# Google OAuth2 Configuration
export GOOGLE_CLIENT_ID="your-google-client-id-here"
export GOOGLE_CLIENT_SECRET="your-google-client-secret-here"

# Database Configuration (optional - defaults provided)
export DATABASE_URL="jdbc:mysql://localhost:3306/ESDProject?createDatabaseIfNotExist=true"
export DATABASE_USERNAME="root"
export DATABASE_PASSWORD="your-db-password"
```

> **⚠️ Security Note**: Never commit actual credentials to version control. Use environment variables or `.env` files (which are gitignored).

## 🚀 Getting Started

### Prerequisites
- **Java 17+** installed
- **Node.js 16+** and npm installed
- **MySQL 8.0+** running
- **Git** for cloning the repository

### Clone Repository
```bash
git clone https://github.com/Prabhav49/Organisation_Registration.git
cd Organisation_Registration
```

### Database Setup
1. Start MySQL service:
   ```bash
   sudo systemctl start mysql
   ```
2. Create database (auto-created by application):
   ```sql
   CREATE DATABASE IF NOT EXISTS ESDProject;
   ```

### Backend Setup & Start

#### Option 1: Using Environment Variables (Recommended)
```bash
# Set environment variables
export GOOGLE_CLIENT_ID="your-google-client-id"
export GOOGLE_CLIENT_SECRET="your-google-client-secret"

# Clean install dependencies
./mvnw clean install

# Start Spring Boot application
./mvnw spring-boot:run
```

#### Option 2: Using Start Script
```bash
# Make script executable
chmod +x start-backend-with-env.sh

# Edit the script with your credentials
nano start-backend-with-env.sh

# Run the script
./start-backend-with-env.sh
```

**Backend will start on:** `http://localhost:9192`

### Frontend Setup & Start

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start React development server
npm start
```

**Frontend will start on:** `http://localhost:3000`

## 📁 Project Structure

```
Organisation_Registration/
├── src/main/java/com/prabhav/employee/
│   ├── auth/                          # Authentication services
│   │   ├── AuthService.java           # JWT authentication logic
│   │   ├── JwtUtil.java               # JWT token utilities
│   │   ├── JwtAuthenticationFilter.java # JWT filter
│   │   └── oauth2/                    # OAuth2 implementation
│   │       ├── CustomOAuth2UserService.java
│   │       ├── OAuth2AuthenticationSuccessHandler.java
│   │       └── OAuth2AuthenticationFailureHandler.java
│   ├── config/
│   │   └── SecurityConfig.java        # Spring Security configuration
│   ├── controller/                    # REST API controllers
│   │   ├── AuthController.java        # Authentication endpoints
│   │   ├── AdminController.java       # Admin user management + audit logs
│   │   ├── EmployeeController.java    # Employee management
│   │   ├── OrganizationController.java# Organization management
│   │   ├── HRController.java          # HR contact management
│   │   ├── TwoFactorAuthController.java# 2FA setup and verification
│   │   └── PasswordController.java    # Password management
│   ├── entity/                        # JPA entities
│   │   ├── Employee.java              # Employee entity
│   │   ├── Organization.java          # Organization entity
│   │   ├── HRContact.java             # HR contact entity
│   │   ├── TwoFactorAuth.java         # 2FA configuration entity
│   │   ├── AuditLog.java              # Audit trail entity
│   │   └── PasswordHistory.java       # Password history entity
│   ├── service/                       # Business logic services
│   │   ├── TwoFactorAuthService.java  # 2FA implementation
│   │   ├── AuditService.java          # Audit logging service
│   │   ├── PasswordPolicyService.java # Password validation
│   │   └── EmployeeService.java       # Employee operations
│   ├── dto/                           # Data Transfer Objects
│   │   ├── AdminUserResponse.java     # Admin user management DTOs
│   │   ├── AuditLogResponse.java      # Audit log DTOs
│   │   └── TwoFactorSetupResponse.java# 2FA setup DTOs
│   └── repo/                          # JPA repositories
├── frontend/
│   ├── src/
│   │   ├── components/                # Reusable React components
│   │   │   ├── GoogleLoginButton.js   # Google OAuth2 login
│   │   │   ├── OAuth2RedirectHandler.js # OAuth2 callback handler
│   │   │   ├── PrivateRoute.js        # Protected routes
│   │   │   ├── Navbar.js              # Navigation component
│   │   │   ├── SecuritySettings.js    # 2FA and security management
│   │   │   ├── TwoFactorSetup.js      # 2FA QR code setup
│   │   │   ├── TwoFactorVerify.js     # 2FA code verification
│   │   │   └── AuditLogs.js           # Audit trail viewer
│   │   ├── pages/                     # Application screens
│   │   │   ├── Login.js               # Login page with 2FA support
│   │   │   ├── Dashboard.js           # Main dashboard
│   │   │   ├── Profile.js             # User profile with security
│   │   │   ├── AdminUsers.js          # Admin user management panel
│   │   │   ├── AddOrganization.js     # Organization form
│   │   │   └── ViewHRs.js             # HR management
│   │   ├── css/                       # Component stylesheets
│   │   │   ├── SecuritySettings.css   # Security UI styles
│   │   │   ├── TwoFactorSetup.css     # 2FA setup styles
│   │   │   ├── AdminUsers.css         # Admin panel styles
│   │   │   └── AuditLogs.css          # Audit logs styles
│   │   ├── services/
│   │   │   └── AuthService.js         # API service layer
│   │   └── utils/                     # Utility functions
│   │       ├── validation.js          # Form validation
│   │       └── ValidationHR.js        # HR validation
│   └── public/                        # Static assets
├── .env.example                       # Environment variables template
├── .gitignore                         # Git ignore rules
└── README.md                          # This file
```

## 🔗 API Endpoints

### Authentication
- `POST /auth/login` - Local login with email/password
- `POST /auth/register` - User registration
- `POST /auth/verify-2fa` - Verify 2FA code during login
- `GET /oauth2/authorization/google` - Initiate Google OAuth2 login
- `GET /oauth2/callback/google` - Google OAuth2 callback

### Two-Factor Authentication
- `POST /api/v1/2fa/setup` - Generate 2FA QR code and secret
- `POST /api/v1/2fa/enable` - Enable 2FA with verification
- `POST /api/v1/2fa/disable` - Disable 2FA with verification
- `GET /api/v1/2fa/status` - Check 2FA status

### Password Management
- `POST /api/v1/password/change` - Change user password
- `POST /api/v1/password/validate` - Validate password policy

### Admin User Management
- `GET /api/v1/admin/users` - Get all users (admin only)
- `POST /api/v1/admin/users` - Create new user (admin only)
- `PUT /api/v1/admin/users/{id}/role` - Update user role (super admin only)
- `DELETE /api/v1/admin/users/{id}` - Delete user (super admin only)
- `POST /api/v1/admin/users/{id}/2fa/enable` - Force enable 2FA (admin only)
- `POST /api/v1/admin/users/{id}/2fa/disable` - Force disable 2FA (admin only)
- `POST /api/v1/admin/users/{id}/lock` - Lock user account (admin only)
- `POST /api/v1/admin/users/{id}/unlock` - Unlock user account (admin only)

### Audit Logs
- `GET /api/v1/admin/audit-logs` - Get audit logs (admin only)
- `GET /api/v1/admin/audit-logs/{entityType}/{entityId}` - Get entity-specific logs

### Organizations
- `GET /api/organizations` - Get all organizations
- `POST /api/organizations` - Create new organization
- `PUT /api/organizations/{id}` - Update organization
- `DELETE /api/organizations/{id}` - Delete organization

### HR Contacts
- `GET /api/hr/{orgId}` - Get HR contacts for organization
- `POST /api/hr` - Create HR contact
- `PUT /api/hr/{id}` - Update HR contact
- `DELETE /api/hr/{id}` - Delete HR contact

### Employee
- `GET /api/employee/profile` - Get current user profile
- `PUT /api/employee/profile` - Update user profile

> **📚 Complete API Documentation:** [Postman Collection](https://documenter.getpostman.com/view/39229910/2sAYBXBWAf)

## 🔍 Testing the Application

### 1. Local Authentication Test
1. Open `http://localhost:3000`
2. Click "Sign Up" to create a new account
3. Login with email/password credentials
4. Access dashboard and test CRUD operations

### 2. Google OAuth2 Test
1. Open `http://localhost:3000`
2. Click "Login with Google" button
3. Complete Google authentication
4. Verify automatic account creation/login
5. Test dashboard functionality

### 3. Two-Factor Authentication Test
1. Login to your account
2. Navigate to Profile → Security Settings
3. Click "Setup 2FA" and scan QR code with authenticator app
4. Enable 2FA by entering verification code
5. Logout and login again to test 2FA requirement
6. Test 2FA disable functionality

### 4. Admin Features Test (Super Admin Account Required)
1. Login with SUPER_ADMIN role account
2. Navigate to Admin Users panel
3. Test user role changes, account lock/unlock
4. Test force enable/disable 2FA for other users
5. Review audit logs with filters

### 5. Security Features Test
1. Test password change with policy validation
2. Test account lockout (multiple failed login attempts)
3. Verify audit logging for all actions
4. Test admin controls and monitoring

### 6. API Testing
Use the provided Postman collection or curl commands:
```bash
# Login and get JWT token
curl -X POST http://localhost:9192/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password"}'

# Test 2FA setup (requires JWT token)
curl -X POST http://localhost:9192/api/v1/2fa/setup \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Use token to access protected endpoint
curl -X GET http://localhost:9192/api/organizations \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 🚀 Deployment

### Production Environment Variables
```bash
# Application
SPRING_PROFILES_ACTIVE=production
SERVER_PORT=9192

# Database
DATABASE_URL=jdbc:mysql://your-db-host:3306/ESDProject
DATABASE_USERNAME=your-db-user
DATABASE_PASSWORD=your-secure-password

# OAuth2
GOOGLE_CLIENT_ID=your-production-client-id
GOOGLE_CLIENT_SECRET=your-production-client-secret

# Frontend URL
APP_FRONTEND_URL=https://your-frontend-domain.com
```

### Build Commands
```bash
# Backend - Create JAR
./mvnw clean package -DskipTests

# Frontend - Create production build
cd frontend && npm run build
```

## 🤝 Contributing
1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

## 📝 License
© 2024 Prabhav Pandey, IIITB. All Rights Reserved.

## 📞 Contact
**Prabhav Pandey**  
📧 Email: Prabhav.Pandey@iiitb.ac.in  
🎓 Institution: Indian Institute of Information Technology Bangalore (IIITB)

---

⭐ **Star this repository if you find it helpful!**
