# API USER - Finance Control

API for user consult registration, login, and update

**Version:** 1.0.0

## Base URL

```
https://localhost/api
```

## Endpoints

### Create User

Creates a new user in the system.

**Endpoint:** `POST /user`

**Request Body:**

```json
{
  "name": "string",
  "email": "user@example.com",
  "password": "string"
}
```

**Parameters:**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| name | string | Yes | User's full name |
| email | string | Yes | User's email address (must be valid email format) |
| password | string | Yes | User's password |

**Response:**

**Status Code:** `201 Created`

```json
{
  "user_id": "string",
  "status": "created"
}
```

**Response Fields:**

| Field | Type | Description |
|-------|------|-------------|
| user_id | string | Unique identifier for the created user |
| status | string | Status of the operation (always "created" for successful creation) |

**Example Request:**

```bash
curl -X POST https://your-api-domain.com/api/user \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva",
    "email": "joao.silva@example.com",
    "password": "mySecurePassword123"
  }'
```

**Example Response:**

```json
{
  "user_id": "usr_123456789",
  "status": "created"
}
```

---

### User Login

Authenticates a user and provides access to the system.

**Endpoint:** `POST /user/login`

**Request Body:**

```json
{
  "email": "user@example.com",
  "password": "string"
}
```

**Parameters:**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| email | string | Yes | User's registered email address |
| password | string | Yes | User's password |

**Responses:**

**Success - Status Code:** `200 OK`

```json
{
  "user_id": "string",
  "status": "authenticated"
}
```

**Error - Status Code:** `401 Unauthorized`

```json
{
  "error": "Invalid credentials"
}
```

**Response Fields:**

| Field | Type | Description |
|-------|------|-------------|
| user_id | string | Unique identifier for the authenticated user |
| status | string | Status of the authentication (always "authenticated" for successful login) |

**Example Request:**

```bash
curl -X POST https://your-api-domain.com/api/user/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao.silva@example.com",
    "password": "mySecurePassword123"
  }'
```

**Example Response:**

```json
{
  "user_id": "usr_123456789",
  "status": "authenticated"
}
```

## Error Handling

The API uses standard HTTP status codes to indicate the success or failure of requests:

- `200 OK` - Request successful
- `201 Created` - Resource created successfully
- `401 Unauthorized` - Authentication failed
- `400 Bad Request` - Invalid request format or missing required fields
- `500 Internal Server Error` - Server error

## Data Formats

- All requests and responses use JSON format
- Dates are in ISO 8601 format
- Email fields must be valid email addresses
- All string fields are UTF-8 encoded

## Authentication

After successful login, use the returned `user_id` for subsequent authenticated requests to other endpoints.

## Security Considerations

- Passwords should be at least 8 characters long
- All API calls should be made over HTTPS
- Store user credentials securely
- Implement proper session management for authenticated users
