# Whale Wearables

This backend service is built using Java Spring Boot, designed to provide robust APIs and business logic for the Whale Wearables application.
It powers all core services including authentication, product management, cart & checkout logic, and payment integration.

## Tech Stack & Tools
- **Java Spring Boot**: Framework for building RESTful APIs and backend services.
- **PostgreSQL**: Relational database to securely store user data, login credentials, and Cashfree orders.
- **JWT Authentication**: Secure authentication mechanism using JSON Web Tokens for both normal login and Google OAuth users.
- **AWS S3**: Storage for product images
- **Cashfree SDK**: Integration of Cashfree payment gateway for managing payment orders and transactions.

## Features
- **User Authentication**: JWT-based login/signup APIs supporting traditional and Google OAuth authentication flows.
- **Database Schema**  
  Well-structured schema linking:  
  - Users  
  - Products (with images stored in S3)  
  - Cart items  
  - Orders  
  - Billing & Shipping details  
  - Payments  
- **Product Management**: Create, fetch, and manage products
- **Cart & Orders**  
  - Persist cart items across sessions  
  - Checkout flow with billing & shipping details  
  - Order history persistence  
- **Payment Integration**: Handles Cashfree payment order creation, verification, and callback processing.
- **RESTful API**: Clean and maintainable API endpoints for frontend consumption.
