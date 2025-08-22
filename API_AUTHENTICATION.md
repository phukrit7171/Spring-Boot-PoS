# API Authentication Guide

This Spring Boot PoS API uses **session-based authentication** with JSESSIONID cookies.

## Quick Start

### Default Users
The application creates two default users at startup:

| Username | Password | Role    |
|----------|----------|---------|
| `admin`  | `password` | ADMIN   |
| `staff`  | `password` | STAFF   |

### Authentication Flow

#### 1. Login
```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password"
}
```

**Success Response (200 OK):**
```json
{
  "id": 1,
  "username": "admin",
  "role": "ADMIN"
}
```

**Important:** The server will return a `Set-Cookie: JSESSIONID=...` header that must be included in subsequent requests.

#### 2. Access Protected Resources
```bash
GET /api/users
Cookie: JSESSIONID=129EB0953B621690FD59C52ED273D322
```

#### 3. Logout
```bash
POST /api/auth/logout
Cookie: JSESSIONID=129EB0953B621690FD59C52ED273D322
```

## Frontend Integration

### JavaScript/Fetch API
When using `fetch()`, always include `credentials: 'include'` to send cookies:

```javascript
// Login
const loginResponse = await fetch('/api/auth/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  credentials: 'include', // IMPORTANT: This sends/receives cookies
  body: JSON.stringify({
    username: 'admin',
    password: 'password'
  })
});

// Subsequent API calls
const usersResponse = await fetch('/api/users', {
  credentials: 'include' // IMPORTANT: This sends the session cookie
});
```

### Axios
Configure Axios to include credentials:

```javascript
// Global configuration
axios.defaults.withCredentials = true;

// Or per-request
const response = await axios.get('/api/users', {
  withCredentials: true
});
```

## Authorization Rules

### Public Endpoints (No Authentication Required)
- `GET /` - Static files (index.html, app.js, etc.)
- `GET /h2-console/**` - H2 Database Console (development only)
- `POST /api/auth/login` - User login
- `POST /api/orders` - Anonymous order creation (self-checkout)
- `GET /api/products/**` - Product browsing (self-checkout)

### Protected Endpoints (Authentication Required)

#### ADMIN Only
- `GET /api/users` - List all users
- `GET /api/users/{id}` - Get user by ID
- `POST /api/users` - Create new user

#### ADMIN or STAFF
- `PUT /api/products/{id}` - Update product
- `DELETE /api/products/{id}` - Delete product
- Most other API endpoints

## Error Handling

### 401 Unauthorized
- **Cause:** No valid session cookie, or expired session
- **Solution:** Redirect user to login page

### 403 Forbidden
- **Cause:** Authenticated but insufficient permissions
- **Solution:** Show "Access Denied" message

### 400 Bad Request (Login)
- **Cause:** Invalid username/password
- **Solution:** Show login error message

## CORS Configuration
The API is configured to accept requests from:
- `http://localhost:*` (any port)
- `http://127.0.0.1:*` (any port)
- `file://*` (local HTML files)

Credentials (cookies) are allowed for all configured origins.

## CSRF Protection
CSRF protection is **disabled** for `/api/**` endpoints to simplify frontend integration. If you need to access non-API endpoints, you'll need to handle CSRF tokens.

## Session Management
- Sessions are created automatically upon successful login
- Sessions expire after the default Spring Boot timeout
- Maximum 10 concurrent sessions per user are allowed
- Sessions are invalidated on logout

## Testing with cURL

```bash
# Login and save cookies
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}' \
  -c cookies.txt

# Use saved cookies for API calls
curl -X GET http://localhost:8080/api/users -b cookies.txt

# Logout
curl -X POST http://localhost:8080/api/auth/logout -b cookies.txt
```

## Troubleshooting

### Common Issues

1. **401 errors after login**
   - Ensure `credentials: 'include'` is set in frontend requests
   - Check that JSESSIONID cookie is being sent in requests

2. **CORS errors**
   - Verify your frontend URL matches the allowed origins in SecurityConfig
   - Make sure `credentials: 'include'` is set

3. **403 errors**
   - User is authenticated but lacks required role permissions
   - Check endpoint authorization requirements (@PreAuthorize annotations)

### Browser DevTools
- Check Application → Cookies for JSESSIONID
- Check Network → Request Headers for Cookie header
- Check Network → Response Headers for Set-Cookie header
