# Spring Boot Point of Sale (PoS) System

This is a complete Point of Sale system built with Spring Boot, designed to handle real-world retail workflows including staff-operated terminals and customer self-checkout.

## Core Concepts

The system is designed with a clear separation between **Employees** (Users who operate the system) and **Customers** (Loyalty members who earn points).

- **Employees (`UserModel`)**: Have `username`, `password`, and `Roles` (`ADMIN`, `STAFF`). They are authenticated via Spring Security to operate the POS.
- **Customers (`CustomerModel`)**: Are identified by `phoneNumber` and have a `points` total. They do not log in to the POS; they are looked up by staff or identify themselves at a kiosk.

## API Endpoints

### Authentication (`/api/auth`)

- `POST /api/auth/login`: For **Employees** to log in and start a secure session.
- `POST /api/auth/logout`: For **Employees** to log out.

### Employee Management (`/api/users`)
*Requires `ADMIN` role for all endpoints, except `DELETE` which also allows `STAFF`.*

- `GET /api/users`: Get all employee accounts.
- `GET /api/users/{id}`: Get an employee by ID.
- `POST /api/users`: Create a new employee account.
- `PUT /api/users/{id}`: Update an existing employee account.
- `DELETE /api/users/{id}`: Delete an employee account. *(Requires `ADMIN` or `STAFF` role)*

### Customer Management (`/api/customers`)
*Requires `ADMIN` or `STAFF` role.*

- `POST /api/customers`: Create a new customer loyalty account.
- `GET /api/customers/lookup/by-phone/{phoneNumber}`: Find a customer by their phone number.

### Product Management (`/api/products`)

- `GET /api/products`: Get all products.
- `GET /api/products/{id}`: Get a product by ID.
- `GET /api/products/search?name={name}`: Search for products by name.
- `POST /api/products`: Create a new product.
- `PUT /api/products/{id}`: Update an existing product. *(Requires `ADMIN` or `STAFF` role)*
- `DELETE /api/products/{id}`: Delete a product. *(Requires `ADMIN` or `STAFF` role)*

### Order Management (`/api/orders`)

- `POST /api/orders`: Create a new order.
    - Can be called by an authenticated Employee (`STA.FF`, `ADMIN`).
    - Can be called anonymously for self-checkout.
    - The request body can include an optional `customerPhoneNumber` to link the sale to a loyalty account.

## Default Users

The system comes with two default employee users, created on startup:

- **Admin**: `username` = `admin`, `password` = `password`
- **Staff**: `username` = `staff`, `password` = `password`