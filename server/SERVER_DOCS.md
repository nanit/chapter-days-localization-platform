# Localization Server API Documentation

Base URL: `http://localhost:8081`

## Overview

This server provides a REST API for managing localized string resources. It supports creating, updating, and retrieving translations across multiple locales.

---

## Endpoints

### 1. Health Check

**GET** `/`

Simple health check endpoint to verify the server is running.

#### Response

**Status:** `200 OK`

**Content-Type:** `text/plain`

```
Nothing to show
```

---

### 2. Get Translations by Locale

**GET** `/translations`

Retrieves all string value translations for a specific locale.

#### Query Parameters

| Parameter | Type   | Required | Description                          |
|-----------|--------|----------|--------------------------------------|
| `locale`  | string | Yes      | The locale code (e.g., "en", "es")   |

#### Example Request

```http
GET /translations?locale=en
```

#### Success Response

**Status:** `200 OK`

**Content-Type:** `application/json`

```json
{
  "locale": "en",
  "values": [
    {
      "key": "welcome_message",
      "translation": "Welcome to our app"
    },
    {
      "key": "goodbye_message",
      "translation": "See you later"
    }
  ]
}
```

#### Response Schema

```typescript
{
  locale: string,
  values: Array<{
    key: string,
    translation: string
  }>
}
```

#### Error Response

**Status:** `404 Not Found`

**Content-Type:** `application/json`

```
No query for locale provided
```

---

### 3. Update Translation

**PUT** `/translation`

Updates an existing translation value for a specific key and locale.

#### Request Body

**Content-Type:** `application/json`

```json
{
  "locale": "en",
  "key": "welcome_message",
  "value": "Welcome back!"
}
```

#### Request Schema

```typescript
{
  locale: string,    // The locale code (e.g., "en", "es")
  key: string,       // The translation key
  value: string      // The new translation value
}
```

#### Example Request

```http
PUT /translation
Content-Type: application/json

{
  "locale": "en",
  "key": "welcome_message",
  "value": "Welcome back to the app!"
}
```

#### Success Response

**Status:** `204 No Content`

```
OK
```

#### Error Response

**Status:** `404 Not Found`

**Content-Type:** `application/json`

```
NotFound
```

---

### 4. Create New Translation

**POST** `/translation`

Creates new translation entries for a given key across multiple locales.

#### Request Body

**Content-Type:** `application/json`

```json
{
  "key": "new_feature_title",
  "description": "Title for the new feature section",
  "values": [
    {
      "locale": "en",
      "value": "New Feature"
    },
    {
      "locale": "es",
      "value": "Nueva Función"
    },
    {
      "locale": "fr",
      "value": "Nouvelle Fonctionnalité"
    }
  ]
}
```

#### Request Schema

```typescript
{
  key: string,                    // The translation key
  description?: string | null,    // Optional description
  values: Array<{
    locale: string,               // The locale code
    value: string                 // The translation value
  }>
}
```

#### Example Request

```http
POST /translation
Content-Type: application/json

{
  "key": "logout_button",
  "description": "Text for logout button",
  "values": [
    {
      "locale": "en",
      "value": "Log Out"
    },
    {
      "locale": "es",
      "value": "Cerrar Sesión"
    }
  ]
}
```

#### Success Response

**Status:** `200 OK`

**Content-Type:** `application/json`

```json
{
  "values": [
    {
      "locale": "en",
      "values": [
        {
          "key": "logout_button",
          "translation": "Log Out"
        }
      ]
    },
    {
      "locale": "es",
      "values": [
        {
          "key": "logout_button",
          "translation": "Cerrar Sesión"
        }
      ]
    }
  ]
}
```

#### Response Schema

```typescript
{
  values: Array<{
    locale: string,
    values: Array<{
      key: string,
      translation: string
    }>
  }>
}
```

#### Error Response

**Status:** `422 Unprocessable Entity`

**Content-Type:** `application/json`

```
Unprocessable
```

---

## Error Handling

All endpoints use consistent error response patterns:

| Status Code | Description                                      |
|-------------|--------------------------------------------------|
| 200         | Success - Request completed successfully         |
| 204         | No Content - Update successful, no body returned |
| 404         | Not Found - Resource not found or missing param  |
| 422         | Unprocessable Entity - Invalid request data      |

Error responses include a text message describing the error.

---

## Content Negotiation

All JSON endpoints automatically:
- Accept `application/json` content type
- Return `application/json` content type
- Use pretty-printed JSON with lenient parsing
- Ignore unknown JSON keys

---

## Server Configuration

- **Host:** `0.0.0.0` (binds to all network interfaces)
- **Port:** `8081`
- **Engine:** Ktor with Netty
- **Serialization:** kotlinx.serialization with JSON

---

## Notes

- The server automatically populates mock data on startup for testing purposes
- All timestamps are stored as Unix epoch milliseconds (INTEGER)
- The `locale` parameter follows standard locale codes (ISO 639-1 language codes)
- Translation keys should be unique per locale combination