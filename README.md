# Whale Wearables

This backend service is built using Java Spring Boot, designed to provide robust APIs and business logic for the Whale Wearables application.

## Tech Stack & Tools
- **Java Spring Boot**: Framework for building RESTful APIs and backend services.
- **PostgreSQL**: Relational database to securely store user data, login credentials, and Cashfree orders.
- **JWT Authentication**: Secure authentication mechanism using JSON Web Tokens for both normal login and Google OAuth users.
- **Cashfree SDK**: Integration of Cashfree payment gateway for managing payment orders and transactions.

## Features
- **User Authentication**: JWT-based login/signup APIs supporting traditional and Google OAuth authentication flows.
- **Secure Data Storage**: User credentials and payment order details stored in PostgreSQL with best practices.
- **Payment Integration**: Handles Cashfree payment order creation, verification, and callback processing.
- **RESTful API**: Clean and maintainable API endpoints for frontend consumption.
