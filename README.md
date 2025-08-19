# Spring Boot Point of Sale (PoS) System

This is a simple Point of Sale system built with Spring Boot.

## Features

- User management (Admin, Staff, Customer roles)
- Product and category management
- Order processing
- Session-based authentication (HttpSession)

## API Endpoints

### Authentication

- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login and start a session (sets JSESSIONID cookie)
- `POST /api/auth/logout` - Logout and end the session

### Users

- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `POST /api/users` - Create a new user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

### Categories

- `GET /api/categories` - Get all categories
- `GET /api/categories/{id}` - Get category by ID
- `POST /api/categories` - Create a new category
- `PUT /api/categories/{id}` - Update category
- `DELETE /api/categories/{id}` - Delete category

### Products

- `GET /api/products` - Get all products
- `GET /api/products/{id}` - Get product by ID
- `GET /api/products/category/{categoryId}` - Get products by category
- `GET /api/products/search?name={name}` - Search products by name
- `POST /api/products` - Create a new product
- `PUT /api/products/{id}` - Update product
- `DELETE /api/products/{id}` - Delete product

### Orders

- `GET /api/orders` - Get all orders
- `GET /api/orders/{id}` - Get order by ID
- `GET /api/orders/user/{userId}` - Get orders by user
- `GET /api/orders/status/{status}` - Get orders by status
- `POST /api/orders` - Create a new order
- `PUT /api/orders/{id}` - Update order
- `DELETE /api/orders/{id}` - Delete order

## Authentication Body

Most endpoints require authentication. To authenticate, first login using the `/api/auth/login` endpoint:

```json
{
  "username": "admin",
  "password": "password"
}
```

After login, the server sets a JSESSIONID cookie. Include this cookie in subsequent requests so your session is recognized.
Example with curl:

```bash
# Login and store cookies
curl -i -c cookies.txt -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}' \
  http://localhost:8080/api/auth/login

# Call a protected endpoint using the stored session cookie
curl -b cookies.txt http://localhost:8080/api/users
```

## Default Users

The system comes with three default users:

- Admin: username `admin`, password `password`
- Staff: username `staff`, password `password`
- Customer: Not require anything but can login if customer want to do it
  - Customer Username : `customer`
  - Customer Password : `password`

## Running the Application

```bash
./gradlew bootRun
```

The application will start on port 8080.

## Frontend and Backend on the Same Server

- The frontend is served by Spring Boot from `src/main/resources/static` and is available at: `http://localhost:8080/`.
- Static assets and the root path are publicly accessible; API endpoints generally require authentication, except `POST /api/auth/**`.
- For rapid development, static resource and Thymeleaf caching is disabled in `application.properties`.

## H2 Console

You can access the H2 database console at:
[http://localhost:8080/h2-console](http://localhost:8080/h2-console)

Use these settings:

- JDBC URL: jdbc:h2:mem:testdb
- User Name: sa
- Password: (leave empty)
