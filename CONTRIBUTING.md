# Contributing to FreshGuide

**Last Updated:** 2026-04-07

Thank you for contributing to FreshGuide! This guide outlines our development process, code standards, and testing expectations.

---

## Table of Contents

1. [Getting Started](#getting-started)
2. [Code Style](#code-style)
3. [Development Workflow](#development-workflow)
4. [Testing](#testing)
5. [Pull Request Process](#pull-request-process)
6. [Common Pitfalls](#common-pitfalls)
7. [Architecture Guidelines](#architecture-guidelines)

---

## Getting Started

### Prerequisites
- Complete the [SETUP_GUIDE.md](SETUP_GUIDE.md)
- Familiarize yourself with [ARCHITECTURE.md](ARCHITECTURE.md)
- Read [SECURITY.md](SECURITY.md) for security best practices

### Project Structure
```
FreshGuide/
├── app/src/main/java/com/example/freshguide/
│   ├── ui/              # Fragments & Activities
│   ├── viewmodel/       # ViewModels
│   ├── repository/      # Data access layer
│   ├── database/        # Room DAOs & entities
│   ├── network/         # API clients
│   ├── model/           # DTOs, entities, UI models
│   └── util/            # Utilities & helpers
├── app/src/main/res/
│   ├── layout/          # XML layouts
│   ├── drawable/        # Vector drawables
│   ├── values/          # Colors, strings, themes
│   └── ...
└── docs/                # Documentation
```

### Local Development Checklist
- [ ] Clone repository
- [ ] Configure `local.properties` with API URL
- [ ] Run `./gradlew clean build`
- [ ] Create emulator or connect device
- [ ] Launch app and verify login works

---

## Code Style

### Java Style Guide

**Package Naming:**
```java
// Correct
package com.example.freshguide.ui.user;
package com.example.freshguide.network;

// Avoid
package com.example.freshguide.stuff;
package freshguide;
```

**Class Naming:**
```java
// Activities
public class LoginActivity extends AppCompatActivity { }
public class MainActivity extends AppCompatActivity { }

// Fragments
public class HomeFragment extends Fragment { }
public class RoomListFragment extends Fragment { }

// ViewModels
public class HomeViewModel extends ViewModel { }
public class RoomListViewModel extends ViewModel { }

// Repository
public class RoomRepository { }
public class SyncRepository { }

// Utilities
public class SessionManager { }
public class ImageUtils { }

// Entities (Room DB)
@Entity(tableName = "buildings")
public class BuildingEntity { }

@Entity(tableName = "rooms")
public class RoomEntity { }

// DTOs (API)
public class RoomDto { }
public class LoginResponse { }

// Adapters
public class RoomAdapter extends ListAdapter<RoomEntity, RoomAdapter.ViewHolder> { }
```

**Method & Variable Naming:**
```java
// Good: clear intent
private LiveData<List<RoomEntity>> rooms;
private void loadRoomsFromDatabase() { }
public boolean isUserLoggedIn() { }

// Avoid: vague names
private LiveData<List<RoomEntity>> data;  // Too generic
private void process() { }  // What process?
public boolean check() { }  // Check what?
```

**Constants:**
```java
// File-level constants (private)
private static final long NAV_ITEM_PRESS_DURATION_MS = 55L;
private static final String DB_NAME = "fresh_guide_db";

// Public constants (if needed)
public static final int MIN_PASSWORD_LENGTH = 8;
```

**Access Modifiers:**
```java
// Default: follow these rules
private int id;                 // Private unless needed elsewhere
private LiveData<User> user;    // Private, expose via getter method
public LiveData<User> getUser() { return user; }  // Public getter

// Avoid
public int userId;              // Never public field
protected int count;            // Rarely needed
```

### Formatting & Indentation

**2-space indentation** (set in Android Studio: Settings → Editor → Code Style → Java):
```java
public class LoginActivity extends AppCompatActivity {
  private Button loginButton;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    
    loginButton = findViewById(R.id.btn_login);
    loginButton.setOnClickListener(v -> {
      performLogin();
    });
  }
}
```

**Line Length:** Max 120 characters (Android Studio default: 100)

**Blank Lines:**
- One blank line between methods
- One blank line between variable groups
- Two blank lines between class members and inner classes

### Commenting

**JavaDoc (public methods/classes):**
```java
/**
 * Fetches room details from the database or API.
 *
 * @param roomId the ID of the room to fetch
 * @return a LiveData wrapping the room entity, or null if not found
 * @throws IllegalArgumentException if roomId is <= 0
 */
public LiveData<RoomEntity> getRoom(int roomId) {
    if (roomId <= 0) {
        throw new IllegalArgumentException("Invalid room ID");
    }
    // ...
}
```

**Inline Comments (explain "why", not "what"):**
```java
// Good: explains non-obvious logic
if (syncStatus == SYNC_PENDING) {
    // Delay sync to avoid hammering API during rapid navigation
    handler.postDelayed(this::triggerSync, 1000);
}

// Avoid: repeats what code already says
int count = rooms.size();  // Get the count of rooms
```

**Avoid Commented-Out Code:**
```java
// Bad: dead code wastes space
// user.setName("John");
// user.save();

// Good: delete unused code; git history preserves it anyway
```

### Import Organization
Android Studio auto-organizes on save:
```
1. java.* and javax.*
2. android.*
3. androidx.*
4. com.* (third-party)
5. com.example.freshguide.* (our code)
```

---

## Development Workflow

### 1. Create a Branch

```bash
# Update main branch
git checkout main
git pull origin main

# Create feature branch with descriptive name
git checkout -b feature/room-search-filter
git checkout -b bugfix/login-token-expiry
git checkout -b docs/api-integration-guide
```

**Branch Naming Convention:**
- `feature/<feature-name>` — New feature
- `bugfix/<bug-name>` — Bug fix
- `docs/<doc-name>` — Documentation
- `refactor/<refactor-name>` — Code refactoring
- `test/<test-name>` — Test improvements

### 2. Make Commits

**Commit Messages:**
```
[tag] Brief description

Longer explanation if needed, wrapping at 80 characters.
Include context, why the change was needed, and any 
side effects or breaking changes.

Fixes #123
Related to #456
```

**Tags:**
- `[feat]` — New feature
- `[fix]` — Bug fix
- `[refactor]` — Code restructuring (no behavior change)
- `[docs]` — Documentation only
- `[test]` — Test additions/changes
- `[style]` — Formatting, naming (no logic change)
- `[perf]` — Performance improvement

**Example Commits:**
```bash
git commit -m "[feat] Add room search with filters

Users can now search rooms by name or code.
Implemented RoomListViewModel with live search results.
Updates RoomListFragment to display filtered results.

Fixes #42"

git commit -m "[fix] Handle 401 token expiry gracefully

AuthInterceptor now catches 401 responses and triggers
logout + redirect to login screen instead of crashing.

Fixes #38"

git commit -m "[docs] Update API integration guide

Added endpoint documentation for admin room upload.
Includes multipart form-data example and error handling.

Related to #45"
```

### 3. Sync with Main

```bash
# If main has new commits while you're working:
git fetch origin
git rebase origin/main

# If conflicts occur, resolve them:
# 1. Open conflicted files
# 2. Choose which version to keep (ours vs. theirs)
# 3. Run git add
# 4. Complete rebase: git rebase --continue
```

---

## Testing

### Unit Tests (JVM)

**Location:** `app/src/test/java/com/example/freshguide/`

**Example: ViewModel Test**
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
    public void searchRooms_FiltersList() {
        List<RoomDto> mockRooms = Arrays.asList(
            new RoomDto(1, "Room 101"),
            new RoomDto(2, "Room 201")
        );
        
        when(mockRepository.searchRooms("101"))
            .thenReturn(new MutableLiveData<>(
                Arrays.asList(mockRooms.get(0))
            ));
        
        viewModel.setSearchQuery("101");
        
        assertEquals(1, viewModel.getRooms().getValue().size());
        assertEquals("Room 101", viewModel.getRooms().getValue().get(0).name);
    }
}
```

### Instrumented Tests (Device/Emulator)

**Location:** `app/src/androidTest/java/com/example/freshguide/`

**Example: Fragment Test**
```java
@RunWith(AndroidTestRunner.class)
public class HomeFragmentTest {
    @get:Rule
    public ActivityScenarioRule<MainActivity> activityRule =
        new ActivityScenarioRule<>(MainActivity.class);
    
    @Test
    public void homeFragment_DisplaysBuildingList() {
        onView(withId(R.id.building_list))
            .check(matches(isDisplayed()));
        
        onView(withText("Building A"))
            .check(matches(isDisplayed()));
    }
}
```

### Running Tests

```bash
# Run unit tests only (JVM, fast)
./gradlew test

# Run instrumented tests (device/emulator, slower)
./gradlew connectedAndroidTest

# Run specific test class
./gradlew test --tests RoomListViewModelTest

# Run with logging
./gradlew test --info
```

### Code Coverage

```bash
# Generate coverage report
./gradlew testDebugCoverage

# Report location: app/build/reports/coverage/
```

**Target:** 70%+ coverage for core repositories and ViewModels

---

## Pull Request Process

### Before Submitting

1. **Ensure build passes:**
   ```bash
   ./gradlew clean build
   ```

2. **Run tests:**
   ```bash
   ./gradlew test
   ./gradlew connectedAndroidTest
   ```

3. **Format code:**
   - Android Studio → Code → Reformat Code
   - Or: `./gradlew spotlessApply`

4. **Check for issues:**
   ```bash
   ./gradlew lint
   ./gradlew dependencyCheck  # Third-party vulnerabilities
   ```

5. **Update documentation:**
   - If adding API endpoints, update [API_INTEGRATION.md](API_INTEGRATION.md)
   - If changing architecture, update [ARCHITECTURE.md](ARCHITECTURE.md)
   - If database schema changes, update [ARCHITECTURE.md](ARCHITECTURE.md) schema section

### Creating a Pull Request

1. Push your branch:
   ```bash
   git push origin feature/room-search-filter
   ```

2. On GitHub: Click "Create Pull Request"

3. Fill in PR template:
   ```markdown
   ## Description
   Brief description of changes
   
   ## Type of Change
   - [ ] New feature
   - [ ] Bug fix
   - [ ] Documentation
   - [ ] Refactoring
   
   ## Testing
   - [ ] Unit tests added
   - [ ] Instrumented tests passed
   - [ ] Tested on emulator/device
   
   ## Screenshots (if UI changes)
   [Optional: add before/after screenshots]
   
   ## Checklist
   - [ ] Code follows style guide
   - [ ] JavaDoc updated
   - [ ] No new warnings/errors
   - [ ] Build passes (./gradlew clean build)
   
   ## Related Issues
   Fixes #123
   Related to #456
   ```

### Review Process

**Reviewers will check:**
- ✅ Code follows style guidelines
- ✅ Logic is sound and tested
- ✅ No security vulnerabilities introduced
- ✅ Documentation is updated
- ✅ Build passes
- ✅ No merge conflicts

**Addressing feedback:**
1. Make requested changes
2. Commit with descriptive message (reference feedback)
3. Push to same branch (PR auto-updates)
4. Mark conversations as resolved

**Merging:**
- PR requires 1+ approvals (lead developer Gab)
- All tests must pass
- Merge via GitHub (creates merge commit)
- Delete branch after merge

---

## Common Pitfalls

### Pitfall 1: God Classes (Large, Unfocused Classes)

**Symptom:** Single Activity or Fragment with 1000+ lines  
**Issue:** Hard to test, maintain, understand  
**Fix:** Break into smaller, focused classes

```java
// Bad: HomeFragment does everything
public class HomeFragment extends Fragment {
    // 500+ lines
    // Handles sync, map rendering, schedule display, settings, etc.
}

// Good: Separate concerns into smaller fragments
public class HomeSyncFragment extends Fragment { }  // Sync status
public class HomeMapFragment extends Fragment { }   // Map display
public class HomeScheduleFragment extends Fragment { }  // Schedule preview
```

### Pitfall 2: Memory Leaks

**Symptom:** App crashes or slows down over time; OutOfMemoryError  
**Common Causes:**
- Static references to Activity/Fragment
- Handler callbacks without cleanup
- Listener registrations without unregistration

**Fix:**
```java
// Bad: Static reference to Fragment
public class MyUtils {
    private static Fragment fragment;  // Leak!
}

// Good: Pass data via constructor or LiveData
public class MyUtils {
    public static void handleData(Fragment context, String data) {
        // Use context for lifecycle, don't store it
    }
}

// Bad: Handler without cleanup
handler.postDelayed(() -> {
    updateUI();  // 'this' context may be garbage by then
}, 1000);

// Good: Use ViewModel lifecycle
viewModel.scheduleUpdate(1000);  // ViewModel handles cleanup
```

### Pitfall 3: Blocking Network Calls on Main Thread

**Symptom:** "ANR (Application Not Responding)" dialog  
**Issue:** API calls on main thread freeze UI

**Fix:**
```java
// Bad: Blocks main thread
String response = apiService.getRoom(1).execute();

// Good: Use callbacks (async)
apiService.getRoom(1).enqueue(new Callback<...>() {
    @Override
    public void onResponse(...) {
        // Called on main thread, safe for UI updates
    }
});

// Better: Use Coroutines or LiveData
// (Future architecture improvement)
```

### Pitfall 4: Not Handling API Errors

**Symptom:** App crashes on 404 or server error  
**Issue:** Missing error handling in callback

**Fix:**
```java
// Bad: No null/error checking
apiService.getRoom(1).enqueue(new Callback<ApiResponse<RoomDto>>() {
    @Override
    public void onResponse(Call<ApiResponse<RoomDto>> call, Response<ApiResponse<RoomDto>> response) {
        RoomDto room = response.body().data;  // Null pointer if response fails!
        viewModel.setRoom(room);
    }
});

// Good: Comprehensive error handling
apiService.getRoom(1).enqueue(new Callback<ApiResponse<RoomDto>>() {
    @Override
    public void onResponse(Call<ApiResponse<RoomDto>> call, Response<ApiResponse<RoomDto>> response) {
        if (response.isSuccessful() && response.body() != null) {
            RoomDto room = response.body().data;
            viewModel.setRoom(room);
        } else {
            showError("Failed to load room: " + response.code());
        }
    }
    
    @Override
    public void onFailure(Call<ApiResponse<RoomDto>> call, Throwable t) {
        showError("Network error: " + t.getMessage());
    }
});
```

### Pitfall 5: Database Migrations Not Tested

**Symptom:** App crashes on existing users' devices when DB version changes  
**Issue:** Missing or incorrect Room DB migration

**Fix:**
```java
// In AppDatabase.java
private static final Migration MIGRATION_6_7 = new Migration(6, 7) {
    @Override
    public void migrate(SupportSQLiteDatabase database) {
        // Create new table
        database.execSQL("CREATE TABLE new_table (...)");
        
        // Copy data from old table
        database.execSQL("INSERT INTO new_table SELECT * FROM old_table");
        
        // Drop old table
        database.execSQL("DROP TABLE old_table");
    }
};

// Add migration to Room.databaseBuilder()
Room.databaseBuilder(context, AppDatabase.class, "db")
    .addMigrations(MIGRATION_6_7)
    .build();

// Test on device: uninstall app → reinstall → verify data preserved
```

### Pitfall 6: Not Clearing Token on Logout

**Symptom:** User logs out but can still access API with old token  
**Issue:** SessionManager.clearSession() not called

**Fix:**
```java
// Bad: No token cleanup
private void logout() {
    finish();  // Just close activity
}

// Good: Clear token properly
private void logout() {
    sessionManager.clearSession();  // Remove token from storage
    Intent intent = new Intent(this, LoginActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
    finish();
}
```

### Pitfall 7: Hardcoded Strings in Code

**Symptom:** Duplicated strings, hard to translate, inconsistent UI text  
**Issue:** Strings not in `res/values/strings.xml`

**Fix:**
```java
// Bad: Hardcoded string
button.setText("Login");

// Good: Use string resource
button.setText(R.string.btn_login);  // Defined in strings.xml
```

In `res/values/strings.xml`:
```xml
<string name="btn_login">Login</string>
<string name="btn_logout">Logout</string>
<string name="error_invalid_email">Please enter a valid email</string>
```

### Pitfall 8: Not Using LiveData for UI Updates

**Symptom:** UI doesn't update when data changes; race conditions  
**Issue:** Updating UI directly instead of observing LiveData

**Fix:**
```java
// Bad: Direct UI updates, loses data on config change
private RoomEntity room;

private void loadRoom(int roomId) {
    apiService.getRoom(roomId).enqueue(new Callback<...>() {
        @Override
        public void onResponse(...) {
            room = response.body().data;
            updateUI(room);  // Direct update
        }
    });
}

// Good: Use LiveData + observe pattern
private class RoomDetailViewModel extends ViewModel {
    private LiveData<RoomEntity> room = new MutableLiveData<>();
    
    public LiveData<RoomEntity> getRoom() {
        return room;
    }
    
    public void loadRoom(int roomId) {
        apiService.getRoom(roomId).enqueue(new Callback<...>() {
            @Override
            public void onResponse(...) {
                ((MutableLiveData<RoomEntity>) room).setValue(response.body().data);
            }
        });
    }
}

// In Fragment:
viewModel.getRoom().observe(getViewLifecycleOwner(), room -> {
    updateUI(room);  // Called whenever data changes
});
```

---

## Architecture Guidelines

### Adding a New Feature

**Step 1: Update Data Model**
```java
// app/src/main/java/com/example/freshguide/model/entity/
@Entity(tableName = "new_feature")
public class NewFeatureEntity {
    @PrimaryKey public int id;
    public String data;
}
```

**Step 2: Create DAO**
```java
// app/src/main/java/com/example/freshguide/database/dao/
@Dao
public interface NewFeatureDao {
    @Insert
    void insert(NewFeatureEntity entity);
    
    @Query("SELECT * FROM new_feature WHERE id = :id")
    LiveData<NewFeatureEntity> getById(int id);
}
```

**Step 3: Update Database**
```java
// AppDatabase.java
@Database(
    entities = {..., NewFeatureEntity.class},
    version = 8  // Increment version
)
public abstract class AppDatabase extends RoomDatabase {
    private static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE new_feature (...)");
        }
    };
    
    public abstract NewFeatureDao newFeatureDao();
}
```

**Step 4: Create Repository**
```java
// app/src/main/java/com/example/freshguide/repository/
public class NewFeatureRepository {
    private NewFeatureDao dao;
    private ApiService apiService;
    
    public LiveData<NewFeatureEntity> get(int id) {
        return dao.getById(id);
    }
}
```

**Step 5: Create ViewModel**
```java
// app/src/main/java/com/example/freshguide/viewmodel/
public class NewFeatureViewModel extends ViewModel {
    private NewFeatureRepository repository;
    
    public LiveData<NewFeatureEntity> feature(int id) {
        return repository.get(id);
    }
}
```

**Step 6: Create UI (Fragment/Activity)**
```java
// app/src/main/java/com/example/freshguide/ui/
public class NewFeatureFragment extends Fragment {
    private NewFeatureViewModel viewModel;
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(this).get(NewFeatureViewModel.class);
        viewModel.feature(1).observe(getViewLifecycleOwner(), feature -> {
            // Update UI
        });
    }
}
```

**Step 7: Add Navigation**
```xml
<!-- app/src/main/res/navigation/nav_graph.xml -->
<fragment
    android:id="@+id/newFeatureFragment"
    android:name="com.example.freshguide.ui.NewFeatureFragment"
    android:label="New Feature" />
```

**Step 8: Test & Document**
- Write unit tests for ViewModel
- Write instrumented tests for Fragment
- Update ARCHITECTURE.md with new component
- Update README.md feature list if user-facing

---

## Code Review Checklist

When reviewing a PR, check:

- [ ] **Code Style:** Follows Java naming conventions, proper indentation
- [ ] **Architecture:** Doesn't introduce new dependencies incorrectly
- [ ] **Testing:** Has unit tests for business logic, instrumented tests for UI
- [ ] **Error Handling:** All API calls have error callbacks, database errors handled
- [ ] **Memory:** No obvious memory leaks (static references, unregistered listeners)
- [ ] **Performance:** No blocking operations on main thread, efficient database queries
- [ ] **Security:** No hardcoded credentials, proper token handling, HTTPS enforced
- [ ] **Documentation:** JavaDoc for public methods, updated guides if needed
- [ ] **Build:** Passes without warnings/errors
- [ ] **Git:** Clean commit history, descriptive messages

---

## Resources

- [Android Developer Guide](https://developer.android.com/)
- [MVVM Pattern](https://developer.android.com/jetpack/guide)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [Retrofit](https://square.github.io/retrofit/)
- [LiveData & ViewModel](https://developer.android.com/jetpack/androidx/releases/lifecycle)
- [NavComponent](https://developer.android.com/guide/navigation)

---

**Questions?** Reach out to Gab (Team Lead) or the frontend team (Angela, Jovilyn, Joyce).

**Last Updated:** 2026-04-07  
**University of Caloocan City — BSCS 3A, Group 2**
