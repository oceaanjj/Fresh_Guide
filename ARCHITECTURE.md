# FreshGuide Architecture

**Last Updated:** 2026-04-07

## Overview

FreshGuide uses the **MVVM (Model-View-ViewModel)** architecture pattern combined with a **Repository** data access layer. This design separates concerns, enables offline-first functionality, and facilitates testing.

### Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│ UI LAYER (Activities, Fragments, Custom Views)                  │
│ ├── SplashActivity / LoginActivity / MainActivity               │
│ ├── HomeFragment / RoomListFragment / DirectionsFragment        │
│ └── AdminDashboardFragment / AdminFormFragment                  │
└───────────────────────────────────────────────────────────────┬─┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│ VIEWMODEL LAYER (UI State Management)                           │
│ ├── HomeViewModel (dashboard state, sync status)                │
│ ├── RoomListViewModel (search, filters)                         │
│ ├── DirectionsViewModel (routes, waypoints)                     │
│ ├── AdminViewModel (CRUD state for admin features)              │
│ └── ScheduleViewModel (schedule entries, reminders)             │
└───────────────────────────────────────────────────────────────┬─┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│ REPOSITORY LAYER (Data Abstraction)                             │
│ ├── AuthRepository (login, logout, register)                    │
│ ├── RoomRepository (room search, filters)                       │
│ ├── RouteRepository (route queries, directions)                 │
│ ├── SyncRepository (bootstrap, version checking)                │
│ └── ScheduleSyncRepository (schedule fetch/sync)                │
└───────────────────────────────────────────────────────────────┬─┘
                    ↙ Network ↓ Database ↘
        ┌────────────────┐      ┌──────────────────┐
        │  API (Retrofit) │      │ Room Database    │
        │                │      │                  │
        │ • ApiService   │      │ • AppDatabase    │
        │ • ApiClient    │      │ • DAOs           │
        │ • Interceptor  │      │ • Entities       │
        └────────────────┘      └──────────────────┘
                │                        │
                └────────────┬───────────┘
                             ↓
        ┌────────────────────────────────┐
        │ Backend (Laravel + Sanctum)     │
        │ • Student/Admin endpoints       │
        │ • CRUD operations               │
        │ • Token authentication          │
        └────────────────────────────────┘
```

---

## Core Components

### 1. Activities

#### SplashActivity
- **Purpose:** App entry point; shows splash screen
- **Flow:** Checks session → navigates to Login or Main
- **Duration:** 2-3 seconds
- **File:** `app/src/main/java/com/example/freshguide/SplashActivity.java`

#### LoginActivity
- **Purpose:** User authentication (student ID or admin credentials)
- **Features:**
  - Student ID registration (no password)
  - Admin email + password login
  - Form validation
  - Error messages for failed auth
- **File:** `app/src/main/java/com/example/freshguide/LoginActivity.java`

#### MainActivity
- **Purpose:** Main app hub with bottom navigation and fragment hosting
- **Components:**
  - NavController (navigation graph orchestration)
  - BottomNavigation menu (Home, Rooms, Directions, Schedule, Profile, Admin)
  - Header bar (title + back button)
  - Network connectivity receiver
  - Theme preference application
- **Lifecycle:** Created after login; survives config changes
- **File:** `app/src/main/java/com/example/freshguide/MainActivity.java`

#### QrScannerActivity
- **Purpose:** Scan room QR codes for quick navigation
- **Features:**
  - CameraX preview
  - ML Kit barcode scanning
  - Result callback to calling fragment
- **File:** `app/src/main/java/com/example/freshguide/QrScannerActivity.java`

#### OnboardingActivity
- **Purpose:** First-time user tutorial
- **Shown:** After first successful login
- **File:** `app/src/main/java/com/example/freshguide/OnboardingActivity.java`

### 2. Fragments & Screens

#### User Screens

| Fragment | Purpose | ViewModel | Key Features |
|----------|---------|-----------|--------------|
| **HomeFragment** | Dashboard | HomeViewModel | Building/floor maps, sync status, quick actions |
| **RoomListFragment** | Search rooms | RoomListViewModel | Search bar, filters, RecyclerView list |
| **RoomDetailFragment** | Room info | RoomDetailViewModel | Facilities, image gallery, "Get Directions" button |
| **DirectionsFragment** | Route visualization | DirectionsViewModel | Step-by-step instructions, map |
| **ScheduleFragment** | Class schedule | ScheduleViewModel | Calendar view, reminders, add/edit |
| **ProfileFragment** | Student info | (no dedicated VM) | User data, sync version, logout button |

#### Admin Screens

| Fragment | Purpose | ViewModel | Key Features |
|----------|---------|-----------|--------------|
| **AdminDashboardFragment** | Admin hub | AdminViewModel | Entity counts, quick nav to CRUD screens |
| **AdminListFragment** (generic) | List CRUD entities | AdminViewModel | Building/Floor/Room/Facility list, delete actions |
| **AdminFormFragment** (generic) | Create/edit entity | AdminViewModel | 3 EditText fields, image upload (rooms only), save |
| **AdminPublishFragment** | Publish sync version | AdminViewModel | Bump version number, trigger student sync |

### 3. ViewModels

All ViewModels extend `androidx.lifecycle.ViewModel` and use LiveData for reactive updates.

#### HomeViewModel
```java
public class HomeViewModel extends ViewModel {
    private LiveData<List<Building>> buildings;
    private LiveData<Integer> syncStatus;  // 0=synced, 1=syncing, 2=error
    private SyncRepository syncRepository;
    
    // Observables
    public LiveData<List<Building>> getBuildings();
    public LiveData<Integer> getSyncStatus();
    
    // Actions
    public void triggerSync();
}
```

#### RoomListViewModel
```java
public class RoomListViewModel extends ViewModel {
    private LiveData<List<RoomDto>> rooms;
    private MutableLiveData<String> searchQuery = new MutableLiveData<>();
    private RoomRepository roomRepository;
    
    public LiveData<List<RoomDto>> getRooms();
    public void setSearchQuery(String query);
}
```

#### DirectionsViewModel
```java
public class DirectionsViewModel extends ViewModel {
    private LiveData<RouteDto> route;
    private LiveData<List<RouteStepDto>> steps;
    private RouteRepository routeRepository;
    
    public LiveData<RouteDto> getRoute();
    public void loadRoute(int roomId, int originId);
}
```

#### AdminViewModel
```java
public class AdminViewModel extends ViewModel {
    private LiveData<List<BuildingDto>> buildings;
    private LiveData<List<FloorDto>> floors;
    private LiveData<List<RoomDto>> rooms;
    // ... etc
    
    // Admin API calls return Call<ApiResponse<T>>
    public void createBuilding(String name, String code);
    public void updateRoom(int id, RoomDto dto);
    public void deleteFloor(int id);
    public void publishSyncVersion();
}
```

### 4. Repository Layer

Repositories abstract data sources (API + Room DB). They implement the **Repository Pattern** to provide a clean data access interface.

#### AuthRepository
```java
// Handles authentication lifecycle
public class AuthRepository {
    public void registerStudent(String studentId, String email);
    public void adminLogin(String email, String password);
    public void logout();
    public SessionManager getSession();  // Current user + token
}
```

#### RoomRepository
```java
// Provides room data (offline-first)
public class RoomRepository {
    public LiveData<List<RoomEntity>> getAllRooms();
    public LiveData<RoomEntity> getRoomById(int id);
    public void searchRooms(String query);  // Uses SQL LIKE
}
```

#### RouteRepository
```java
// Calculates and stores routes
public class RouteRepository {
    public LiveData<RouteEntity> getRoute(int roomId, int originId);
    public void computeRoute(int roomId, int originId);
}
```

#### SyncRepository
```java
// Manages data synchronization with backend
public class SyncRepository {
    public LiveData<BootstrapResponse> getBootstrap();
    public LiveData<SyncVersionResponse> checkVersion();
    public void syncIfNeeded();  // Auto-sync on first login
}
```

### 5. Database Layer (Room ORM)

#### AppDatabase
- **Location:** `app/src/main/java/com/example/freshguide/database/AppDatabase.java`
- **Version:** 7 (with migration history)
- **Entities:**
  - `BuildingEntity` — Campus buildings
  - `FloorEntity` — Building floors (1-5)
  - `RoomEntity` — Individual rooms
  - `FacilityEntity` — Facilities (WiFi, projector, etc.)
  - `RoomFacilityCrossRef` — Many-to-many join
  - `OriginEntity` — Navigation starting points
  - `RouteEntity` — Computed routes
  - `RouteStepEntity` — Route waypoints
  - `ScheduleEntryEntity` — Class schedule entries
  - `SyncMetaEntity` — Sync version metadata

#### DAOs (Data Access Objects)
Located in `app/src/main/java/com/example/freshguide/database/dao/`:

| DAO | Operations |
|-----|-----------|
| RoomDao | insertRoom, getRoomById, searchRooms, getAllRooms, deleteRoom |
| RouteDao | insertRoute, getRoute, updateRoute |
| ScheduleDao | insertSchedule, getSchedulesByDay, deleteSchedule |
| BuildingDao | insertBuilding, getAllBuildings, deleteBuilding |
| FloorDao | getFloorsByBuilding, insertFloor |
| FacilityDao | insertFacility, getFacilitiesByRoom |
| OriginDao | getAllOrigins, insertOrigin |
| SyncMetaDao | insertOrUpdate, getLatestVersion |

### 6. Network Layer (API Integration)

#### ApiClient
```java
// Singleton Retrofit builder
public class ApiClient {
    private static ApiClient instance;
    private ApiService apiService;
    
    public static synchronized ApiClient getInstance(Context context);
    public ApiService getApiService();
}
```

**Key Features:**
- Enforces HTTPS (converts http:// to https://)
- Requires `api.base.url` in `local.properties`
- Auto-appends `/api/` if missing
- Includes OkHttp logging interceptor (BODY level)
- Token injection via `AuthInterceptor`

#### ApiService (Retrofit Interface)
```java
public interface ApiService {
    // Auth
    @POST("register")
    Call<ApiResponse<LoginResponse>> registerStudent(@Body Map<String, String> body);
    
    @POST("admin/login")
    Call<ApiResponse<LoginResponse>> adminLogin(@Body Map<String, String> body);
    
    // Sync
    @GET("sync/version")
    Call<SyncVersionResponse> getSyncVersion();
    
    @GET("sync/bootstrap")
    Call<BootstrapResponse> getBootstrap();
    
    // Rooms (user)
    @GET("rooms/{id}")
    Call<ApiResponse<RoomDto>> getRoom(@Path("id") int roomId);
    
    @GET("routes/{roomId}")
    Call<ApiResponse<RouteDto>> getRoute(
        @Path("roomId") int roomId,
        @Query("origin_id") int originId
    );
    
    // Admin endpoints...
    @GET("admin/buildings")
    @POST("admin/buildings")
    @PUT("admin/buildings/{id}")
    @DELETE("admin/buildings/{id}")
    // ... Floors, Rooms, Facilities, Origins, Routes, Publish
}
```

#### AuthInterceptor
```java
public class AuthInterceptor implements Interceptor {
    // Retrieves token from SessionManager
    // Injects "Authorization: Bearer <token>" header
    // Handles 401 responses (re-login)
}
```

### 7. Models

#### Entity Models (Room Database)
Located in `app/src/main/java/com/example/freshguide/model/entity/`:

```java
@Entity(tableName = "buildings")
public class BuildingEntity {
    @PrimaryKey
    public int id;
    public String name;
    public String code;
    public int floor_count;
}

@Entity(tableName = "rooms")
public class RoomEntity {
    @PrimaryKey
    public int id;
    public int floor_id;
    public String code;
    public String name;
    public String description;
    public String image_url;
    public String location;
}

@Entity(tableName = "schedule_entries")
public class ScheduleEntryEntity {
    @PrimaryKey
    public int id;
    public String title;
    public String course_code;
    public String instructor;
    public int day_of_week;      // 0-6 (Sun-Sat)
    public int start_minutes;    // Minutes from midnight
    public int end_minutes;
    public int room_id;
    public boolean is_online;
    public String online_platform;
    public int reminder_minutes;
}
```

#### DTO Models (API Requests/Responses)
Located in `app/src/main/java/com/example/freshguide/model/dto/`:

```java
public class LoginResponse {
    public int id;
    public String name;
    public String email;
    public String token;  // Sanctum token
    public String role;   // "student" or "admin"
}

public class RoomDto {
    public int id;
    public int floor_id;
    public String code;
    public String name;
    public String description;
    public String image_url;
    public List<FacilityDto> facilities;  // Many-to-many
}

public class RouteDto {
    public int id;
    public int room_id;
    public int origin_id;
    public List<RouteStepDto> steps;  // Waypoints
}

public class BootstrapResponse {
    public List<BuildingDto> buildings;
    public List<FloorDto> floors;
    public List<RoomDto> rooms;
    public List<FacilityDto> facilities;
    public List<OriginDto> origins;
    public List<RouteDto> routes;
    public int version;  // Sync version
}
```

### 8. UI Components & Adapters

#### RecyclerView Adapters

| Adapter | Usage | Features |
|---------|-------|----------|
| **RoomAdapter** | RoomListFragment | ListAdapter + DiffUtil, click listeners |
| **GenericListAdapter** | Admin CRUD lists | Generic type for any entity |
| **RouteStepAdapter** | DirectionsFragment | Step-by-step instructions |
| **ScheduleAdapter** | ScheduleFragment | Calendar-based grid |

#### Custom Views

| View | Purpose | Location |
|------|---------|----------|
| **FloorMapView** | Interactive floor plan | `app/src/main/java/com/example/freshguide/ui/view/FloorMapView.java` |

### 9. Utilities

#### SessionManager
```java
public class SessionManager {
    // Token & user state management
    public boolean isLoggedIn();
    public String getToken();
    public void saveToken(String token);
    public String getUserRole();  // "student" or "admin"
    public int getCurrentUserId();
    public void clearSession();  // Logout
}
```

#### ThemePreferenceManager
```java
public class ThemePreferenceManager {
    // Dark mode preference storage & application
    public static int getThemeMode(Context context);
    public static void setThemeMode(int mode);  // MODE_DAY, MODE_NIGHT, MODE_AUTO
    public static void applyTheme(int mode);
}
```

---

## Data Flow

### 1. User Login Flow

```
LoginActivity
    ↓ (user enters credentials)
    ↓ Click "Login"
AuthRepository.login()
    ↓
ApiService.registerStudent() or adminLogin()
    ↓
Server validates → returns LoginResponse with token
    ↓
AuthRepository stores token in SessionManager (EncryptedSharedPreferences)
    ↓
HomeFragment loads via MainActivity
    ↓
SyncRepository.getBootstrap() triggers
    ↓
Room DB populated with buildings/floors/rooms
    ↓
HomeViewModel observes sync status
    ↓
UI updates (stops loading spinner)
```

### 2. Room Search & Navigation

```
User types in RoomListFragment search box
    ↓
RoomListViewModel.setSearchQuery(query)
    ↓
RoomRepository.searchRooms(query)  [uses SQL LIKE]
    ↓
RoomDao.searchRoomsByName() executes on Room DB
    ↓
Results returned as LiveData<List<RoomEntity>>
    ↓
RoomAdapter updates RecyclerView
    ↓
User taps room → RoomDetailFragment opens
    ↓
RouteRepository.getRoute(roomId, originId)
    ↓
Looks up cached RouteEntity, or calls API
    ↓
DirectionsViewModel.route updated
    ↓
DirectionsFragment renders steps
```

### 3. Offline-First Sync

```
On app startup (SplashActivity/HomeFragment)
    ↓
SyncRepository.checkVersion()
    ↓ (calls API /sync/version)
Server returns latest version (e.g., 5)
    ↓
Check SyncMetaEntity.last_synced_version in Room DB
    ↓
If versions differ:
    ├─ Call /sync/bootstrap
    ├─ Receive BootstrapResponse with all data
    ├─ Insert/update into Room DB (using DAOs)
    └─ Update SyncMetaEntity
    ↓
Next time app opens, Room DB has fresh data
    ↓
All queries use Room DB (fast, offline-compatible)
    ↓
API calls only needed for mutations (admin CRUD, schedule changes)
```

### 4. Admin Create Room with Image

```
AdminFormFragment (create room)
    ↓ (user fills name, code, floor)
    ↓ (user selects image from gallery)
    ↓
ActivityResultContract.launch() → Gallery picker
    ↓ (user selects image)
    ↓
ImageUtils.compressImage() reduces file size
    ↓ Click "Save"
    ↓
AdminViewModel.createRoom(RoomDto)
    ↓
ApiService.adminCreateRoom() + Multipart image upload
    ↓
POST /api/admin/rooms with MultipartBody (name, code, image)
    ↓
Server validates, stores image, returns RoomDto
    ↓
AdminViewModel updates local list
    ↓
AdminListFragment RecyclerView refreshes
    ↓
SyncMetaEntity.version incremented (triggers student sync)
```

---

## Key Design Patterns

### MVVM (Model-View-ViewModel)
- **Separation of concerns:** UI logic separate from business logic
- **Testability:** ViewModels can be tested independently
- **Lifecycle awareness:** ViewModels survive config changes

### Repository Pattern
- **Abstraction:** Data source independence (API, DB, cache)
- **Single responsibility:** Repository handles all data access
- **Flexibility:** Easy to swap implementations (testing, mocking)

### LiveData & Observables
- **Reactive:** UI automatically updates when data changes
- **Lifecycle-aware:** Stops updates when Fragment/Activity destroyed
- **Thread-safe:** Callbacks always on main thread

### Singleton (ApiClient, SessionManager)
- **Single instance:** Prevents multiple Retrofit/OkHttp clients
- **Thread-safe:** Synchronized getInstance()

### Adapter Pattern (RecyclerView)
- **Reusability:** GenericListAdapter works for any entity type
- **DiffUtil:** Efficient list updates with minimal re-draws

---

## Error Handling

### API Errors
```java
// Repositories use Call<T> with callbacks
apiService.getRoom(roomId).enqueue(new Callback<ApiResponse<RoomDto>>() {
    @Override
    public void onResponse(Call<ApiResponse<RoomDto>> call, Response<ApiResponse<RoomDto>> response) {
        if (response.isSuccessful()) {
            // Update UI with response.body().data
        } else {
            // Handle HTTP errors (401, 404, 500, etc.)
            // Show error Snackbar or error message
        }
    }
    
    @Override
    public void onFailure(Call<ApiResponse<RoomDto>> call, Throwable t) {
        // Handle network errors
        // Use cached Room DB data as fallback
    }
});
```

### Database Errors
- Room DAOs wrapped in try-catch
- Errors propagated via LiveData error state or Snackbar

### Validation
- Client-side validation in ViewModels (empty strings, invalid IDs)
- Server-side validation returns 422 with error details

---

## Testing Strategy

### Unit Tests
- **ViewModel tests:** Mock repositories, test LiveData emissions
- **Repository tests:** Mock API and DAO, verify data flow
- **Utility tests:** SessionManager, ThemePreferenceManager

### Instrumented Tests
- **Fragment tests:** Fragmentscenario, launch and verify UI
- **DAO tests:** Create in-memory Room DB, test queries
- **Integration tests:** API mocking with MockWebServer

### Example Unit Test
```java
@RunWith(AndroidTestRunner.class)
public class RoomListViewModelTest {
    private RoomListViewModel viewModel;
    private RoomRepository mockRepository;
    
    @Before
    public void setup() {
        mockRepository = mock(RoomRepository.class);
        viewModel = new RoomListViewModel(mockRepository);
    }
    
    @Test
    public void searchRooms_UpdatesLiveData() {
        List<RoomDto> mockRooms = Arrays.asList(...);
        when(mockRepository.searchRooms("lib")).thenReturn(
            new MutableLiveData<>(mockRooms)
        );
        
        viewModel.setSearchQuery("lib");
        
        assertEquals(mockRooms, viewModel.getRooms().getValue());
    }
}
```

---

## Performance Considerations

### Database Indexing
```sql
CREATE INDEX index_rooms_floor_id ON rooms(floor_id);
CREATE INDEX index_schedule_entries_room_id ON schedule_entries(room_id);
CREATE INDEX index_schedule_entries_day_of_week ON schedule_entries(day_of_week);
```

### Pagination (Future)
- Room lists: Implement LIMIT/OFFSET for large datasets
- Schedule: Show only current week initially, load more on demand

### Image Optimization
- Compress before upload (ImageUtils)
- Use Glide library for caching & lazy loading

### Query Optimization
- Use `@Query` with efficient SQL (avoid SELECT *)
- Leverage Room's relation annotations for JOIN queries

---

## Dependencies

| Dependency | Version | Purpose |
|-----------|---------|---------|
| `androidx.appcompat` | 1.6.1 | Material components |
| `com.google.android.material` | Latest | Material Design 3 |
| `androidx.room` | 2.5.1 | Local database ORM |
| `androidx.lifecycle` | 2.6.1 | ViewModel, LiveData |
| `androidx.navigation` | 2.7.1 | Fragment navigation |
| `com.squareup.retrofit2` | 2.9.0 | HTTP client |
| `com.squareup.okhttp3` | 4.9.3 | HTTP interceptor |
| `androidx.security` | 1.1.0-alpha06 | EncryptedSharedPreferences |
| `androidx.camera` | 1.3.0 | QR scanner |
| `com.google.mlkit` | 17.0.0 | Barcode detection |

---

## Migration History

| Version | Date | Changes |
|---------|------|---------|
| 7 | 2026-03-21 | Added schedule_entries table, route migrations |
| 6 | 2026-03-18 | RouteEntity update with metadata |
| 5 | 2026-03-15 | Initial schema with buildings, floors, rooms |

See `AppDatabase.java` for migration code.

---

## Next Steps & Future Architecture

1. **Pagination:** Implement room list pagination for scalability
2. **Offline-first Sync:** Batched queue for admin mutations (currently online-only)
3. **Modular Architecture:** Split into feature modules (`:feature-rooms`, `:feature-admin`)
4. **Jetpack Compose:** Gradually migrate from XML layouts
5. **Kotlin Coroutines:** Replace Retrofit callbacks with suspend functions
6. **Hilt DI:** Dependency injection framework for cleaner code

---

For questions or architectural discussions, refer to the team at:
**University of Caloocan City — BSCS 3A, Group 2**
