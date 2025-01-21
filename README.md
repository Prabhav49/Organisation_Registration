# Outreach Management System

## Overview
Full-stack web application for managing organizational outreach using Spring Boot and React.js.

## Features
- JWT authentication
- Organization registration
- HR contact management
- Organization search and CRUD operations

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

### Backend Setup
```bash
git clone https://github.com/Prabhav49/Organisation_Registration.git
cd backend
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
