# Outreach Management System

## Overview
A full-stack web application for managing organizational outreach, built with Spring Boot and React.js.

## Features
- User authentication via JWT
- Organization registration
- HR contact management
- Search and filter organizations
- CRUD operations for organizations

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
- Tailwind CSS

## Setup Instructions

### Backend Setup
```bash
# Clone repository
git clone https://github.com/your-repo.git
cd backend

# Configure database
# Update application.properties with your database credentials

# Build and run
mvn clean install
mvn spring-boot:run
```

### Frontend Setup
```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start development server
npm start
```

## API Endpoints

### Authentication
- `POST /auth/login`: User authentication

### Organization Management
- `POST /organization`: Create organization
- `GET /organization`: List organizations
- `GET /organization/{id}`: Get specific organization
- `PUT /organization/{id}`: Update organization
- `DELETE /organization/{id}`: Delete organization

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
2. Create your feature branch
3. Commit changes
4. Push to the branch
5. Create pull request

## License
[Specify your license]

## Contact
Prabhav - Prabhav.Pandey@iiitb.ac.in