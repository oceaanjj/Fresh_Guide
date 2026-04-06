<p align="center">
  <img src="logo.png" alt="FreshGuide Logo" width="200"/>
</p>

<h1 align="center">FreshGuide</h1>
<h3 align="center">Campus Navigation Made Simple</h3>

<p align="center">
  A comprehensive Android navigation app for UCC students and administrators. Find rooms, get directions, manage schedules, and access campus information—online and offline.
</p>

---

## Overview

FreshGuide is a dual-role campus navigation application built for the University of Caloocan City. Students can discover campus locations, view directions, and manage class schedules. Administrators can manage buildings, floors, rooms, facilities, and publish campus data for offline access.

## Key Features

### For Students
- Search and browse campus rooms and buildings
- Get turn-by-turn directions with route visualization
- View room details with facilities and location info
- Manage class schedule with reminders
- Offline campus data access (synced from backend)
- Dark mode support
- QR code scanning for quick room access

### For Administrators
- Full CRUD operations for buildings, floors, rooms, and facilities
- Room image upload and management
- Define campus origins and navigation routes
- Publish data versions for student synchronization
- Schedule facility maintenance with route management

## Tech Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| Language | Java 11 | - |
| Min SDK | Android 7.0 | API 24 |
| Target SDK | Android 15 | API 36 |
| Build System | Gradle | 9.0.1 |
| Architecture | MVVM + Repository Pattern | - |
| Database | Room ORM | AndroidX |
| Networking | Retrofit 2 + OkHttp | HTTP/2 |
| UI Framework | Material Design 3 | AndroidX |
| Layout | ConstraintLayout + NavComponent | - |
| Authentication | Laravel Sanctum Tokens | - |
| Secure Storage | EncryptedSharedPreferences | - |
| Camera/QR | CameraX + ML Kit Barcode | - |

## Project Structure

```
FreshGuide/
├── app/
│   ├── build.gradle.kts                    # Build configuration with API URL setup
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml         # App permissions & activities
│       │   ├── java/com/example/freshguide/
│       │   │   ├── SplashActivity.java           # Splash screen entry point
│       │   │   ├── LoginActivity.java            # Auth screen (student ID / admin email)
│       │   │   ├── MainActivity.java             # Main navigation hub with NavController
│       │   │   ├── QrScannerActivity.java        # QR code scanner
│       │   │   ├── OnboardingActivity.java       # First-time user guide
│       │   │   ├── viewmodel/                    # MVVM ViewModels
│       │   │   │   ├── AdminViewModel.java       # Admin CRUD operations
│       │   │   │   ├── HomeViewModel.java        # Dashboard & sync state
│       │   │   │   ├── RoomListViewModel.java    # Room search
│       │   │   │   ├── DirectionsViewModel.java  # Route visualization
│       │   │   │   └── ScheduleViewModel.java    # Schedule management
│       │   │   ├── repository/                   # Data access layer
│       │   │   │   ├── AuthRepository.java       # Login/logout/register
│       │   │   │   ├── RoomRepository.java       # Room queries
│       │   │   │   ├── RouteRepository.java      # Navigation routes
│       │   │   │   ├── SyncRepository.java       # Bootstrap sync
│       │   │   │   └── ScheduleSyncRepository.java # Schedule sync
│       │   │   ├── database/                     # Room database layer
│       │   │   │   ├── AppDatabase.java          # Database schema & migrations
│       │   │   │   └── dao/                      # Data Access Objects
│       │   │   │       ├── RoomDao.java
│       │   │   │       ├── RouteDao.java
│       │   │   │       ├── ScheduleDao.java
│       │   │   │       └── ...
│       │   │   ├── model/                        # Data models
│       │   │   │   ├── entity/                   # Room DB entities
│       │   │   │   ├── dto/                      # API DTOs
│       │   │   │   └── ui/                       # UI state models
│       │   │   ├── network/                      # API layer
│       │   │   │   ├── ApiClient.java            # Retrofit setup
│       │   │   │   ├── ApiService.java           # API endpoints
│       │   │   │   └── AuthInterceptor.java      # Token injection
│       │   │   ├── ui/                           # UI components
│       │   │   │   ├── user/                     # Student screens
│       │   │   │   │   ├── HomeFragment.java
│       │   │   │   │   ├── RoomListFragment.java
│       │   │   │   │   ├── RoomDetailFragment.java
│       │   │   │   │   ├── DirectionsFragment.java
│       │   │   │   │   ├── ScheduleFragment.java
│       │   │   │   │   └── ProfileFragment.java
│       │   │   │   ├── admin/                    # Admin CRUD screens
│       │   │   │   │   ├── AdminDashboardFragment.java
│       │   │   │   │   ├── AdminListFragment.java
│       │   │   │   │   ├── AdminFormFragment.java
│       │   │   │   │   └── AdminPublishFragment.java
│       │   │   │   ├── adapter/                  # RecyclerView adapters
│       │   │   │   │   ├── RoomAdapter.java
│       │   │   │   │   ├── RouteStepAdapter.java
│       │   │   │   │   └── GenericListAdapter.java
│       │   │   │   └── view/                     # Custom views
│       │   │   │       └── FloorMapView.java
│       │   │   ├── util/                         # Utilities
│       │   │   │   ├── SessionManager.java       # Token & user state
│       │   │   │   ├── ThemePreferenceManager.java # Dark mode
│       │   │   │   └── ...
│       │   │   └── receiver/                     # Broadcast receivers
│       │   │       ├── NetworkChangeReceiver.java
│       │   │       └── ScheduleReminderReceiver.java
│       │   └── res/
│       │       ├── layout/                       # Phone layouts
│       │       ├── layout-sw600dp/               # Tablet layouts
│       │       ├── layout-land/                  # Landscape layouts
│       │       ├── drawable/                     # Vector icons & graphics
│       │       ├── drawable-*/                   # Density-specific assets
│       │       ├── values/                       # Colors, strings, themes
│       │       └── xml/                          # Configs & backup rules
│       └── test/
│           └── java/                             # Unit tests
├── docs/                                         # Documentation
├── gradle/                                       # Gradle version catalog
├── build.gradle.kts                              # Root build config
├── local.properties                              # LOCAL: API base URL
└── README.md                                     # This file

```

## Quick Start

### Prerequisites
- **Android Studio** (Ladybug 2024.1.1 or newer)
- **Android SDK**: API 24+ installed
- **Java 11** or later
- **Gradle 9.0+** (bundled with Android Studio)

### Setup Instructions

#### 1. Clone Repository
```bash
git clone https://github.com/your-org/FreshGuide.git
cd FreshGuide
```

#### 2. Configure API Base URL
Edit `local.properties` and set your backend API URL:
```properties
sdk.dir=/path/to/Android/SDK
api.base.url=https://your-ngrok-subdomain.ngrok-free.app/api
```

**Note:** The app enforces HTTPS and automatically appends `/api/` if needed.

#### 3. Sync Gradle
```bash
./gradlew clean build
```

#### 4. Open in Android Studio
```
File → Open → Select FreshGuide folder
```
Android Studio will auto-detect and sync the Gradle project.

#### 5. Select Emulator or Device
- Recommended emulators: Pixel 6 (xxhdpi), Pixel 4a (xxhdpi), Pixel Tablet (xhdpi)
- Run: `Shift + F10` (Windows/Linux) or `Ctrl + R` (macOS)

#### 6. First Login
- **Student:** Use your UCC student ID (no password required)
- **Admin:** Use email and password

### Backend Connection

FreshGuide requires a running Laravel 11 API server with Sanctum authentication. The app attempts to sync campus data on first login.

**Backend setup:**
```bash
# Backend repository location (on developer machine)
/home/john/projects/AndroidStudioProjects/Fresh_Guide_BackEnd/laravel/

# Expose backend via ngrok (tunnel for emulator/device testing)
ngrok http 8000
# Copy ngrok URL to local.properties as api.base.url
```

**Important:** ngrok URLs change on each restart. Update `local.properties` and rebuild if connection fails.

## Architecture Overview

### MVVM Pattern
- **Model:** Room entities, DTOs, repository classes
- **View:** Fragments, Activities, custom views
- **ViewModel:** Manages UI state, API calls, database queries

### Data Flow
```
API (Retrofit) ──> AuthInterceptor ──> OkHttp ──> Network
    ↓                                              ↓
DTOs ──> Repository (async) ──> DAO ──> Room DB ──> UI (LiveData)
    ↓
Cached in memory (Repository + ViewModel)
```

### Authentication
- **Student:** POST `/api/register` with student ID → receives Sanctum token
- **Admin:** POST `/api/admin/login` with email/password → receives Sanctum token
- Token stored securely in `EncryptedSharedPreferences`
- Auto-injected via `AuthInterceptor` on all requests

### Synchronization
- Initial sync: `/api/sync/bootstrap` downloads buildings, floors, rooms, routes
- Incremental sync: `/api/sync/version` checks if client is up-to-date
- Offline-first: All queries use Room DB; API calls update DB
- Admin actions: Online-only (no offline admin features)

## Key Technologies & Patterns

| Technology | Purpose | Implementation |
|-----------|---------|-----------------|
| **Room ORM** | Local SQLite database | AppDatabase + DAOs |
| **Retrofit 2** | HTTP client for API calls | ApiClient + ApiService |
| **NavComponent** | Fragment navigation | action_* routes in nav_graph.xml |
| **LiveData** | Reactive UI updates | ViewModel → View binding |
| **ViewModel** | State management | Survives config changes |
| **ConstraintLayout** | Responsive UI | Phone + tablet layouts |
| **Material Design 3** | Modern UI components | Material themes + colors |
| **EncryptedSharedPreferences** | Secure token storage | SessionManager |
| **CameraX** | QR code scanning | QrScannerActivity |

## Testing

### Running Tests
```bash
# Unit tests (JVM)
./gradlew test

# Instrumented tests (device/emulator)
./gradlew connectedAndroidTest
```

### Emulator Configuration
```bash
# Create a new emulator with recommended specs
android create avd -n "Pixel6_xxhdpi" \
  -k "system-images;android-36;default;x86_64" \
  -d "pixel" \
  -f

# Start emulator
emulator -avd Pixel6_xxhdpi
```

## Known Issues & Workarounds

### Issue 1: API URL Compilation Error
**Symptom:** "API base URL is empty" crash on app startup
**Fix:** Verify `api.base.url` is set in `local.properties` with valid HTTPS URL

### Issue 2: ngrok URL Expired
**Symptom:** Network errors after ngrok session restart
**Fix:** Restart ngrok tunnel, update `api.base.url`, run `./gradlew clean build`

### Issue 3: Image Upload Fails
**Symptom:** Room image upload returns 422 error
**Fix:** Ensure image is compressed before upload (implementation handles this automatically)

### Issue 4: Layout Constraints on Tablets
**Symptom:** UI elements cut off on sw600dp+ devices
**Fix:** Layouts use `layout-sw600dp/` overrides; check variant in Android Studio

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for code style, PR process, and testing guidelines.

## Security

See [SECURITY.md](SECURITY.md) for authentication details, encryption mechanisms, and security best practices.

## API Reference

See [API_INTEGRATION.md](API_INTEGRATION.md) for endpoint documentation and request/response examples.

## Architecture & Design

See [ARCHITECTURE.md](ARCHITECTURE.md) for detailed component descriptions and data flow diagrams.

## Setup Issues

See [SETUP_GUIDE.md](SETUP_GUIDE.md) for detailed environment configuration and troubleshooting.

## Team

| Name | Role | Responsibility |
|------|------|-----------------|
| Gab | Team Lead Developer | Architecture, core features |
| Angela | Frontend Developer | UI/UX implementation |
| Jovilyn | Frontend Developer | Layout design, styling |
| Joyce | Frontend Developer | Student-facing features |
| Bryan | Backend Developer | Laravel API, database |
| Trisha | Backend Developer | Admin features, sync logic |

## Changelog

**v1.0** (2026-04-07)
- Initial release with dual-role navigation system
- Complete MVVM architecture with Room offline support
- Secure authentication with Sanctum tokens
- Full admin CRUD for campus management
- Schedule management with reminders
- QR code scanner for quick access
- Dark mode support
- Responsive layouts for phone and tablet

---

<p align="center">
  <strong>University of Caloocan City — BSCS 3A, Group 2</strong><br/>
  <em>Last Updated: 2026-04-07</em>
</p>
