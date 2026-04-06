# FreshGuide API Integration Guide

**Last Updated:** 2026-04-07  
**Backend:** Laravel 11 + Sanctum  
**API Version:** v1

---

## Overview

FreshGuide communicates with a Laravel backend via REST API with JSON payloads. All requests use HTTPS and Sanctum token-based authentication. The API supports student and admin operations, with strict permission checking on the server side.

---

## Base URL Configuration

### Local Development

Set in `local.properties`:
```properties
api.base.url=https://your-ngrok-subdomain.ngrok-free.app/api
```

**Validation Rules:**
- Must be HTTPS (http:// auto-converts)
- Must end with `/api/` or `/api` (auto-appended)
- Cannot be blank (throws exception at startup)

### Example URLs
| URL | Normalized To |
|-----|---|
| `http://example.com` | `https://example.com/api/` |
| `https://example.com/api` | `https://example.com/api/` |
| `https://example.com/api/` | `https://example.com/api/` |
| `(blank)` | ❌ Exception: "API base URL is empty" |

---

## Authentication

### Sanctum Token Flow

```
1. POST /register or /admin/login
   ↓ (credentials sent)
   ← Backend validates, generates Sanctum token
   ↓
2. SessionManager.saveToken(token)
   ↓ (stored in EncryptedSharedPreferences)
   ↓
3. All subsequent requests auto-inject:
   Header: "Authorization: Bearer <token>"
   ↓ (via AuthInterceptor)
   ↓
4. Backend validates token signature
   ├─ Valid → process request with user context
   └─ Invalid → return 401, trigger logout
```

### Token Structure

**Sanctum Token Format:**
```
eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjEsImlzYWRtaW4iOjAsImlhdCI6MTcxNDEyMzQ1NiwiZXhwIjoxNzQ2NjU5NDU2fQ.abcdef...
```

| Component | Description |
|-----------|-------------|
| Header | Base64(type + algorithm) |
| Payload | Base64(user_id, is_admin, issued_at, expires_at) |
| Signature | HMAC-SHA256(header.payload, secret) |

**Expiration:** 365 days (configurable on backend)

### Token Injection (AuthInterceptor)

```java
public class AuthInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = sessionManager.getToken();
        Request.Builder builder = chain.request().newBuilder();
        
        if (token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }
        
        Response response = chain.proceed(builder.build());
        
        // Handle expired token
        if (response.code() == 401) {
            sessionManager.clearSession();
            // MainActivity will redirect to LoginActivity
        }
        
        return response;
    }
}
```

---

## Standard Response Format

### Success Response (2xx)

```json
{
  "success": true,
  "message": "Operation successful",
  "data": {
    "id": 1,
    "name": "Room 101",
    ...
  }
}
```

**HTTP Status:** 200 OK (or 201 Created for POST)

### Error Response (4xx/5xx)

```json
{
  "success": false,
  "message": "Validation failed",
  "errors": {
    "name": ["Name is required"],
    "floor_id": ["Floor not found"]
  }
}
```

**HTTP Status:** 422 Unprocessable Entity (validation), 401 Unauthorized, 403 Forbidden, 500 Internal Server Error

### Handling in Code

```java
apiService.getRoom(roomId).enqueue(new Callback<ApiResponse<RoomDto>>() {
    @Override
    public void onResponse(Call<ApiResponse<RoomDto>> call, Response<ApiResponse<RoomDto>> response) {
        if (response.isSuccessful()) {
            RoomDto room = response.body().data;
            viewModel.setRoom(room);
        } else {
            ApiResponse<?> errorBody = response.body();
            String message = errorBody.message;  // "Validation failed"
            showError(message);
        }
    }
    
    @Override
    public void onFailure(Call<ApiResponse<RoomDto>> call, Throwable t) {
        showError("Network error: " + t.getMessage());
        // Use cached data from Room DB as fallback
    }
});
```

---

## Endpoint Reference

### Authentication

#### POST /register (Student)
**Purpose:** Register new student and receive token  
**Auth Required:** No  
**Request:**
```json
{
  "student_id": "2024-001234",
  "email": "student@ucc.edu.ph"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Student registered successfully",
  "data": {
    "id": 42,
    "name": "Juan Dela Cruz",
    "email": "student@ucc.edu.ph",
    "token": "abc123def456...",
    "role": "student"
  }
}
```

**Error (422):**
```json
{
  "success": false,
  "message": "Validation failed",
  "errors": {
    "student_id": ["Student ID already registered"],
    "email": ["Email format invalid"]
  }
}
```

#### POST /admin/login (Admin)
**Purpose:** Admin login with credentials  
**Auth Required:** No  
**Request:**
```json
{
  "email": "admin@ucc.edu.ph",
  "password": "SecurePassword123!"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Admin logged in successfully",
  "data": {
    "id": 1,
    "name": "Administrator",
    "email": "admin@ucc.edu.ph",
    "token": "xyz789abc123...",
    "role": "admin"
  }
}
```

#### POST /logout
**Purpose:** Invalidate current token  
**Auth Required:** Yes  
**Request:** (empty body)  
**Response:**
```json
{
  "success": true,
  "message": "Logged out successfully"
}
```

---

### Synchronization

#### GET /sync/version
**Purpose:** Check if client data is up-to-date  
**Auth Required:** No  
**Query Params:** None

**Response:**
```json
{
  "success": true,
  "data": {
    "version": 5,
    "created_at": "2026-04-07T12:00:00Z"
  }
}
```

**Usage in Code:**
```java
SyncRepository.checkVersion().observe(owner, version -> {
    if (version > lastSyncedVersion) {
        triggerBootstrapSync();  // Data is stale
    } else {
        showMessage("Campus data is current");
    }
});
```

#### GET /sync/bootstrap
**Purpose:** Download complete campus data for offline use  
**Auth Required:** No (public data)  
**Query Params:** None

**Response:**
```json
{
  "success": true,
  "data": {
    "buildings": [
      {
        "id": 1,
        "name": "Building A",
        "code": "A",
        "floor_count": 5
      }
    ],
    "floors": [
      {
        "id": 1,
        "building_id": 1,
        "floor_number": 1,
        "code": "A1"
      }
    ],
    "rooms": [
      {
        "id": 101,
        "floor_id": 1,
        "code": "A101",
        "name": "Lecture Hall A",
        "description": "Large lecture theater",
        "image_url": "https://...room101.jpg",
        "location": "North Wing"
      }
    ],
    "facilities": [
      {
        "id": 1,
        "name": "WiFi",
        "icon": "wifi"
      },
      {
        "id": 2,
        "name": "Projector",
        "icon": "projector"
      }
    ],
    "room_facilities": [
      {
        "room_id": 101,
        "facility_id": 1
      },
      {
        "room_id": 101,
        "facility_id": 2
      }
    ],
    "origins": [
      {
        "id": 1,
        "name": "Main Gate",
        "description": "University main entrance"
      }
    ],
    "routes": [
      {
        "id": 1,
        "room_id": 101,
        "origin_id": 1,
        "distance_meters": 150,
        "estimated_minutes": 3
      }
    ],
    "version": 5
  }
}
```

---

### Rooms (Student)

#### GET /rooms/{id}
**Purpose:** Fetch single room details  
**Auth Required:** Yes  
**Path Params:** `id` — Room ID

**Response:**
```json
{
  "success": true,
  "data": {
    "id": 101,
    "floor_id": 1,
    "code": "A101",
    "name": "Lecture Hall A",
    "description": "Seats 100 students",
    "image_url": "https://...room101.jpg",
    "location": "North Wing, 1st Floor",
    "facilities": [
      {
        "id": 1,
        "name": "WiFi",
        "icon": "wifi"
      },
      {
        "id": 2,
        "name": "Projector",
        "icon": "projector"
      }
    ]
  }
}
```

**Error (404):** Room not found

#### GET /routes/{roomId}
**Purpose:** Get navigation route from origin to room  
**Auth Required:** Yes  
**Path Params:** `roomId` — Destination room ID  
**Query Params:** `origin_id` — Starting location ID

**Request:**
```
GET /routes/101?origin_id=1
```

**Response:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "room_id": 101,
    "origin_id": 1,
    "distance_meters": 150,
    "estimated_minutes": 3,
    "steps": [
      {
        "id": 1,
        "sequence": 1,
        "instruction": "Exit the main gate",
        "landmark": "Main entrance arch",
        "distance_meters": 50
      },
      {
        "id": 2,
        "sequence": 2,
        "instruction": "Head straight towards Building A",
        "landmark": "Building A sign",
        "distance_meters": 80
      },
      {
        "id": 3,
        "sequence": 3,
        "instruction": "Enter Building A and turn left",
        "landmark": "Information desk",
        "distance_meters": 20
      }
    ]
  }
}
```

---

### Schedule (Student)

#### GET /schedules
**Purpose:** List student's class schedule  
**Auth Required:** Yes

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "title": "Data Structures",
      "course_code": "CS201",
      "instructor": "Dr. Smith",
      "day_of_week": 1,
      "start_minutes": 480,
      "end_minutes": 600,
      "room_id": 101,
      "is_online": false,
      "online_platform": null,
      "reminder_minutes": 15,
      "created_at": "2026-04-07T10:00:00Z"
    }
  ]
}
```

**Note:** `day_of_week` is 0-6 (Sunday=0, Saturday=6); `start_minutes` is minutes from midnight

#### POST /schedules
**Purpose:** Create new schedule entry  
**Auth Required:** Yes

**Request:**
```json
{
  "title": "Algorithms",
  "course_code": "CS202",
  "instructor": "Dr. Johnson",
  "day_of_week": 2,
  "start_minutes": 510,
  "end_minutes": 630,
  "room_id": 102,
  "is_online": false,
  "reminder_minutes": 15
}
```

**Response:** (same as GET /schedules item)

#### PUT /schedules/{id}
**Purpose:** Update schedule entry  
**Auth Required:** Yes  
**Path Params:** `id` — Schedule ID

**Request:** (same fields as POST, omit unchanging fields)

#### DELETE /schedules/{id}
**Purpose:** Delete schedule entry  
**Auth Required:** Yes

---

### Admin — Buildings

#### GET /admin/buildings
**Purpose:** List all buildings  
**Auth Required:** Yes (admin only)

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "Building A",
      "code": "A",
      "floor_count": 5
    },
    {
      "id": 2,
      "name": "Building B",
      "code": "B",
      "floor_count": 3
    }
  ]
}
```

#### POST /admin/buildings
**Purpose:** Create building  
**Auth Required:** Yes (admin only)

**Request:**
```json
{
  "name": "Building C",
  "code": "C",
  "floor_count": 4
}
```

#### PUT /admin/buildings/{id}
**Purpose:** Update building  
**Auth Required:** Yes (admin only)

#### DELETE /admin/buildings/{id}
**Purpose:** Delete building  
**Auth Required:** Yes (admin only)

---

### Admin — Floors

#### GET /admin/floors
#### POST /admin/floors
#### PUT /admin/floors/{id}
#### DELETE /admin/floors/{id}

**Request/Response Format:**
```json
{
  "id": 1,
  "building_id": 1,
  "floor_number": 1,
  "code": "A1"
}
```

---

### Admin — Rooms

#### GET /admin/rooms
#### POST /admin/rooms (Multipart)
**Purpose:** Create room with optional image

**Request (Multipart Form):**
```
POST /admin/rooms
Content-Type: multipart/form-data

name: "Lecture Hall A"
code: "A101"
floor_id: 1
description: "Large lecture theater"
image: <binary file>
location: "North Wing"
```

#### PUT /admin/rooms/{id} (Multipart)
**Purpose:** Update room with optional image

#### DELETE /admin/rooms/{id}

#### DELETE /admin/rooms/{id}/image
**Purpose:** Remove room image

---

### Admin — Facilities

#### GET /admin/facilities
#### POST /admin/facilities
#### PUT /admin/facilities/{id}
#### DELETE /admin/facilities/{id}

**Request/Response Format:**
```json
{
  "id": 1,
  "name": "WiFi",
  "icon": "wifi"
}
```

---

### Admin — Origins

#### GET /admin/origins
#### POST /admin/origins
#### PUT /admin/origins/{id}
#### DELETE /admin/origins/{id}

**Request/Response Format:**
```json
{
  "id": 1,
  "name": "Main Gate",
  "description": "University main entrance"
}
```

---

### Admin — Routes

#### GET /admin/routes
#### POST /admin/routes
#### PUT /admin/routes/{id}
#### DELETE /admin/routes/{id}

**Request/Response Format:**
```json
{
  "id": 1,
  "room_id": 101,
  "origin_id": 1,
  "distance_meters": 150,
  "estimated_minutes": 3
}
```

---

### Admin — Publish

#### POST /admin/publish
**Purpose:** Increment sync version (triggers student sync)  
**Auth Required:** Yes (admin only)

**Request:** (empty)

**Response:**
```json
{
  "success": true,
  "message": "Version published successfully",
  "data": {
    "version": 6,
    "published_at": "2026-04-07T12:00:00Z"
  }
}
```

---

## Error Codes & Handling

| Code | Meaning | Typical Action |
|------|---------|-----------------|
| **200** | OK | Success — use data from response |
| **201** | Created | Resource created — show success message |
| **400** | Bad Request | Client error — log request, show generic error |
| **401** | Unauthorized | Token invalid/expired — clear session, redirect to login |
| **403** | Forbidden | User lacks permission — show "Access denied" message |
| **404** | Not Found | Resource missing — show "Item not found" message |
| **422** | Unprocessable Entity | Validation failed — show validation errors to user |
| **500** | Server Error | Backend error — show "Server error, try again" message |
| **503** | Service Unavailable | API down — show "Service temporarily offline" message |

**Handling 422 Validation Errors:**
```java
if (response.code() == 422) {
    ApiResponse<?> error = response.body();
    Map<String, List<String>> errors = error.errors;  // Field → error list
    
    for (String field : errors.keySet()) {
        for (String message : errors.get(field)) {
            showFieldError(field, message);  // Show below input field
        }
    }
}
```

---

## Rate Limiting

**Currently:** No rate limiting (to be implemented Q2 2026)

**Future Implementation:**
```
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 999
X-RateLimit-Reset: 1712500800

If limit exceeded: 429 Too Many Requests
Retry-After: 60 (seconds)
```

---

## Pagination

**Currently:** All endpoints return complete data sets

**Future Implementation (for large lists):**
```
GET /admin/rooms?page=1&per_page=50

Response:
{
  "data": [...],
  "pagination": {
    "current_page": 1,
    "per_page": 50,
    "total": 450,
    "last_page": 9
  }
}
```

---

## Image Upload Details

### Room Image Upload

**Endpoint:** `POST /admin/rooms` (multipart)  
**Field:** `image` (binary file)  
**Accepted Types:** JPEG, PNG  
**Max Size:** 5 MB (enforced client-side before upload)

**Client Implementation:**
```java
// AdminFormFragment.java
private void uploadRoomWithImage(RoomDto room, File imageFile) {
    // 1. Compress image
    File compressed = ImageUtils.compressImage(imageFile);
    
    // 2. Create multipart body
    RequestBody imageBody = RequestBody.create(MediaType.parse("image/jpeg"), compressed);
    MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", compressed.getName(), imageBody);
    
    // 3. Create form fields
    RequestBody namePart = RequestBody.create(MediaType.parse("text/plain"), room.name);
    RequestBody codePart = RequestBody.create(MediaType.parse("text/plain"), room.code);
    
    // 4. Call API
    apiService.adminCreateRoom(namePart, codePart, imagePart).enqueue(...);
}
```

**Server Response:**
```json
{
  "success": true,
  "data": {
    "id": 101,
    "name": "Lecture Hall A",
    "code": "A101",
    "image_url": "https://api.example.com/storage/rooms/101-1712500800.jpg",
    ...
  }
}
```

---

## Sync Strategy

### Offline-First Approach

```
App Startup
    ↓
Check /sync/version
    ↓
    ├─ Version > last_synced
    │  └─ Download /sync/bootstrap
    │     ├─ Insert BuildingEntity
    │     ├─ Insert FloorEntity
    │     ├─ Insert RoomEntity
    │     ├─ ... (all entities)
    │     └─ Update SyncMetaEntity.version
    │
    └─ Version = last_synced
       └─ Use Room DB (offline-compatible)

User Actions
    ├─ View Room → RoomRepository.getRoom() → Room DB
    ├─ Search Rooms → RoomRepository.search() → SQL LIKE query
    ├─ Get Directions → RouteRepository.getRoute() → Room DB (or API if missing)
    │
    └─ (Admin only) Create Building → API call
       ├─ POST /admin/buildings
       └─ AdminViewModel updates local list (re-sync on next sync/version check)
```

### Incremental Sync (Future)

```
Instead of re-downloading everything, use:
GET /sync/bootstrap?since=2026-04-06T12:00:00Z

Server returns only:
- Modified buildings since timestamp
- Modified floors
- ... etc

Reduces bandwidth 90% for incremental updates
```

---

## Testing API Locally

### Using curl

```bash
# Check sync version
curl -X GET http://localhost:8000/api/sync/version

# Register student
curl -X POST http://localhost:8000/api/register \
  -H "Content-Type: application/json" \
  -d '{"student_id":"2024-001","email":"test@ucc.edu.ph"}'

# Get room (requires token)
curl -X GET http://localhost:8000/api/rooms/101 \
  -H "Authorization: Bearer abc123def456..."
```

### Using Postman

1. Import endpoints into Postman collection
2. Set variable `{{base_url}}` = `http://localhost:8000/api`
3. Set variable `{{token}}` = token from login response
4. Use `{{base_url}}/rooms/101` to test endpoints
5. Add `Authorization: Bearer {{token}}` header automatically

---

## Common Integration Issues

### Issue 1: CORS Error
**Symptom:** "No 'Access-Control-Allow-Origin' header"
**Cause:** Browser request (web) vs. app request (mobile different)
**Fix:** This shouldn't occur in Android app (not a browser)

### Issue 2: 401 Unauthorized on Valid Token
**Symptom:** Login succeeds, but subsequent requests return 401
**Cause:** Token not being injected by AuthInterceptor
**Fix:** Verify `SessionManager.getToken()` returns non-null value

### Issue 3: Image Upload Returns 422
**Symptom:** "image" field validation error
**Cause:** Image not attached or wrong content-type
**Fix:** Verify multipart form-data encoding; check image size < 5MB

### Issue 4: Sync Never Completes
**Symptom:** Loading spinner stays on for 30+ seconds
**Cause:** Ngrok tunnel timeout or large dataset
**Fix:** Check ngrok is running; limit bootstrap payload size on backend

---

## API Versioning

**Current Version:** v1 (endpoints at `/api/*`)

**Future Versions:** If breaking changes needed:
```
POST /api/v2/...  // New endpoints
POST /api/v1/...  // Legacy endpoints (deprecated)
```

Clients updated on their own schedule; server supports both versions temporarily.

---

## References

- **Backend API Code:** `/home/john/projects/AndroidStudioProjects/Fresh_Guide_BackEnd/laravel/app/Http/Controllers/`
- **Retrofit Documentation:** https://square.github.io/retrofit/
- **Sanctum Documentation:** https://laravel.com/docs/sanctum
- **JSON Format:** https://www.json.org/

---

**Last Updated:** 2026-04-07  
**Backend Developer:** Bryan & Trisha  
**University of Caloocan City — BSCS 3A, Group 2**
