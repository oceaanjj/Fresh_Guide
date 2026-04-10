# FreshGuide Setup Guide

**Last Updated:** 2026-04-07

## Prerequisites

### System Requirements
- **OS:** macOS 10.15+, Windows 10+, or Linux (Ubuntu 18.04+)
- **RAM:** 8 GB minimum (16 GB recommended)
- **Disk Space:** 30 GB minimum for Android SDK + emulator

### Software Prerequisites
| Software | Version | Why? |
|----------|---------|------|
| **Java Development Kit (JDK)** | 11 or later | Android Gradle plugin requires Java 11 |
| **Android Studio** | Ladybug 2024.1.1+ | IDE with Gradle bundled |
| **Git** | 2.30+ | Clone repository |
| **ngrok** | Latest | Tunnel backend API for emulator/device testing |

### Android SDK Components
- **Android SDK Platform 36** (or 24+)
- **Android SDK Build-Tools 36.x**
- **Android Emulator** (or physical device with USB debugging enabled)

---

## Step 1: Install Java Development Kit (JDK)

### macOS
```bash
# Using Homebrew
brew install openjdk@11

# Verify installation
java -version
# Expected output: openjdk version "11.x.x"
```

### Windows
1. Download from [Oracle JDK 11](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) or use [OpenJDK](https://adoptopenjdk.net/)
2. Run installer; follow prompts
3. Open PowerShell and verify:
```powershell
java -version
```

### Linux (Ubuntu)
```bash
sudo apt-get update
sudo apt-get install openjdk-11-jdk
java -version
```

---

## Step 2: Install Android Studio

### macOS
1. Download from [developer.android.com](https://developer.android.com/studio)
2. Drag `Android Studio.app` to `/Applications/`
3. Open Applications → Android Studio
4. Follow first-run setup wizard

### Windows
1. Download installer from [developer.android.com](https://developer.android.com/studio)
2. Run `android-studio-*.exe`
3. Follow installation wizard

### Linux
```bash
cd ~/Downloads
unzip android-studio-*.zip
sudo mv android-studio /opt/
/opt/android-studio/bin/studio.sh
```

### Post-Installation
1. Open Android Studio
2. **Settings → SDK Manager:**
   - Install **API 36** platform
   - Install **API 24** platform (min SDK)
   - Install **Build-Tools 36.x**
   - Install **Android Emulator**
   - Install **SDK Platform-Tools**

3. **Settings → AVD Manager:**
   - Create emulator: Pixel 6, API 36, xxhdpi (recommended)

---

## Step 3: Install Git

### macOS
```bash
# Using Homebrew
brew install git
git --version
```

### Windows
Download from [git-scm.com](https://git-scm.com) and run installer

### Linux
```bash
sudo apt-get install git
git --version
```

---

## Step 4: Clone FreshGuide Repository

```bash
# Navigate to your projects folder
cd ~/Projects

# Clone repository
git clone https://github.com/your-org/FreshGuide.git
cd FreshGuide

# Verify directory structure
ls -la
# Should show: app/, docs/, gradle/, build.gradle.kts, README.md, etc.
```

---

## Step 5: Configure Local Properties

The app requires **API base URL** configuration to connect to the backend.

### 5a. Find or Create local.properties

**Location:** Root of project directory

```bash
cd ~/Projects/FreshGuide
cat local.properties
```

Expected content (already present from initial setup):
```properties
sdk.dir=/Users/YOUR_USERNAME/Library/Android/sdk
```

### 5b. Set API Base URL

You need a running backend API and an ngrok tunnel to expose it.

#### Option A: Using ngrok Tunnel (For Development)

1. **Install ngrok:**
   ```bash
   # macOS
   brew install ngrok
   
   # Windows: download from ngrok.com and add to PATH
   # Linux: download from ngrok.com and add to PATH
   ```

2. **Start ngrok tunnel** (assuming backend runs on `localhost:8000`):
   ```bash
   ngrok http 8000
   ```
   
   Output will show:
   ```
   Forwarding                    https://abc123def456.ngrok-free.app -> http://localhost:8000
   ```

3. **Copy the ngrok URL** and add to `local.properties`:
   ```properties
   sdk.dir=/Users/YOUR_USERNAME/Library/Android/sdk
   api.base.url=https://abc123def456.ngrok-free.app/api
   ```

4. **Save and close** the file.

**Important:** ngrok URLs change on each restart. You must:
- Restart ngrok → get new URL
- Update `api.base.url` in `local.properties`
- Run `./gradlew clean build`

#### Option B: Using Production Backend (If Available)

If your team provides a production API URL:
```properties
api.base.url=https://api.example.com/api
```

### 5c. Verify Configuration

```bash
# Check that api.base.url is set
grep "api.base.url" local.properties
# Should output: api.base.url=https://...

# The app will validate this URL at build time
# Must be HTTPS (http:// auto-converts to https://)
```

---

## Step 6: Backend Setup & Connection

FreshGuide requires a running **Laravel 11 + Sanctum** API backend.

### Backend Repository
- **Location (on developer machine):** `/home/john/projects/AndroidStudioProjects/Fresh_Guide_BackEnd/laravel/`
- **Language:** PHP (Laravel 11)
- **Authentication:** Sanctum tokens
- **Database:** MySQL (or SQLite for development)

### Start Backend Locally

```bash
# Navigate to backend
cd /home/john/projects/AndroidStudioProjects/Fresh_Guide_BackEnd/laravel

# Install dependencies
composer install

# Create .env file
cp .env.example .env
php artisan key:generate

# Run migrations
php artisan migrate

# (Optional) Seed test data
php artisan db:seed
# Seeded admin (development): admin@freshguide.com / password

# Start Laravel server (defaults to localhost:8000)
php artisan serve
```

Backend should now listen on `http://localhost:8000`.

### Test Backend Connection

```bash
# GET /api/sync/version (no auth required)
curl -X GET http://localhost:8000/api/sync/version

# Should return JSON with version number
# If connection fails, verify backend is running and firewall allows traffic
```

---

## Step 7: Sync Gradle & Build

```bash
cd ~/Projects/FreshGuide

# Clean and rebuild (required after local.properties changes)
./gradlew clean build

# On Windows:
# gradlew.bat clean build

# Expected output (final lines):
# BUILD SUCCESSFUL in Xs
```

**Troubleshooting:**
- If build fails with "API_BASE_URL is empty," check `local.properties`
- If gradle sync fails, open Android Studio and let it rebuild project

---

## Step 8: Configure Emulator

### Create New Emulator (Recommended)

```bash
# macOS/Linux
emulator -list-avds                    # See existing AVDs
emulator -avd Pixel6_xxhdpi &         # Start emulator in background

# Or use Android Studio:
# Tools → AVD Manager → Create Virtual Device
```

### Emulator Settings
| Setting | Recommended |
|---------|-------------|
| Device | Pixel 6 (6.4" display) |
| API Level | 36 (latest) or 30+ |
| RAM | 2 GB |
| Internal Storage | 64 GB |
| GPU | Enabled (hardware acceleration) |
| Orientation | Portrait (default) |

### Test Emulator Network Access

```bash
# Inside emulator, open browser and navigate to:
# http://10.0.2.2:8000/api/sync/version

# 10.0.2.2 is the special alias for host machine's localhost inside emulator
# If backend is running and accessible, you'll see JSON response
```

**Note:** If ngrok tunneling is used, the emulator can directly reach:
```
https://abc123def456.ngrok-free.app/api/sync/version
```

---

## Step 9: Run App on Emulator

### Using Android Studio
1. **Open FreshGuide** project in Android Studio
2. **Select emulator:**
   - Top menu: Device selector dropdown
   - Choose your emulator (or "Create new")
3. **Run app:**
   - Click green **Run** button (or `Shift + F10`)
   - Android Studio builds APK and installs on emulator
4. **Wait for splash screen** (2-3 seconds)

### Using Command Line
```bash
cd ~/Projects/FreshGuide

# Build and install APK
./gradlew installDebug

# Start app on emulator (requires adb)
adb shell am start -n com.example.freshguide/.SplashActivity
```

### First Time Running
- **Splash screen** appears (2-3 seconds)
- **Login screen** loads
- Enter **Student ID** or seeded admin credentials (`admin@freshguide.com` / `password`)
- If backend is reachable, sync begins (wait for "Synced" message)
- **Home screen** displays campus map and quick actions

---

## Step 10: Physical Device Setup (Optional)

### Enable USB Debugging
1. On device: **Settings → About Phone → Build Number** (tap 7 times)
2. Developer options appear
3. **Settings → Developer Options → USB Debugging** (toggle ON)

### Connect to Computer
```bash
# Check connected devices
adb devices
# Should list device with serial number

# Install APK on device
./gradlew installDebug

# View device logs
adb logcat | grep "FreshGuide"
```

---

## Verification Checklist

After completing setup, verify:

- [ ] `local.properties` has `api.base.url` set to HTTPS endpoint
- [ ] Backend API is running and accessible (test with curl)
- [ ] Emulator or device is connected and visible in Android Studio
- [ ] `./gradlew clean build` completes without errors
- [ ] App launches (SplashActivity visible)
- [ ] Login screen appears
- [ ] Login succeeds and sync starts
- [ ] Home screen displays buildings/floors
- [ ] Bottom navigation is visible (Home, Rooms, Directions, etc.)

---

## Troubleshooting

### Build Issues

#### Error: "API base URL is empty"
**Cause:** `api.base.url` not set in `local.properties`
```bash
# Fix:
echo "api.base.url=https://your-ngrok-url/api" >> local.properties
./gradlew clean build
```

#### Error: "API base URL must start with https://"
**Cause:** URL uses http:// instead of https://
```bash
# Fix in local.properties:
api.base.url=https://abc123def456.ngrok-free.app/api  # Correct
api.base.url=http://abc123def456.ngrok-free.app/api   # Wrong
```

#### Gradle Sync Fails
**Cause:** Android SDK not fully installed
```bash
# Fix:
# Open Android Studio → Settings → SDK Manager
# Install any missing Android SDK platforms (API 24, 36)
# Restart Android Studio
```

### Runtime Issues

#### App Crashes at Login
**Cause:** Backend not running or ngrok URL expired
```bash
# Check backend:
curl -X GET http://localhost:8000/api/sync/version

# Check ngrok:
ngrok status  # or restart and get new URL

# Update local.properties and rebuild
```

#### App Shows "No Network" Despite WiFi
**Cause:** Backend unreachable
```bash
# From emulator browser, navigate to:
# http://10.0.2.2:8000/api/sync/version

# If fails, check:
# 1. Backend process running (ps aux | grep "php artisan serve")
# 2. Firewall allows traffic on port 8000
# 3. ngrok tunnel active (if using)
```

#### Emulator "Device Offline"
**Cause:** Emulator crashed or stalled
```bash
# Restart emulator:
adb kill-server
adb start-server
emulator -avd Pixel6_xxhdpi
```

#### Login Button Not Responding
**Cause:** Previous session token cached
```bash
# Clear app data:
adb shell pm clear com.example.freshguide
# Then restart app
```

### Database Issues

#### App Shows "Sync Failed" After Login
**Cause:** Room database migration failed
```bash
# Migrations are versioned in AppDatabase.java (v1→v7)
# If you modified schema, ensure migration exists

# Check logs:
adb logcat | grep "Room"

# Force reset (clears all data):
adb shell pm clear com.example.freshguide
# Re-login to re-sync
```

---

## Performance Tips

### Emulator Speed
- Enable GPU acceleration: **Settings → Advanced → GPU → "Automatic"**
- Use KVM (Linux) or Hypervisor.Framework (macOS) for hardware acceleration
- Allocate 2+ GB RAM to emulator
- Use Pixel 4a (smaller screen) for faster rendering

### Development Workflow
```bash
# Hot reload (no full rebuild):
# If only resources changed (drawable, layout):
./gradlew installDebug

# If Java code changed:
./gradlew clean build  # Full rebuild required
./gradlew installDebug

# Real-time logs:
adb logcat -s "FreshGuide"  # Filter by app tag
```

### Debugging
```bash
# Enable verbose logging in MainActivity.java
Log.d("FreshGuide", "Event happened: " + value);

# View logs:
adb logcat -s "FreshGuide" | grep "Event"
```

---

## Common ngrok Issues

### Issue 1: "Invalid URL" After ngrok Restart
**Symptom:** "AuthInterceptor: Invalid token" errors
**Cause:** ngrok URL changed but app still uses old URL
**Fix:**
```bash
# Stop old ngrok
# Ctrl+C

# Start new ngrok session
ngrok http 8000

# Copy new URL to local.properties
api.base.url=https://NEW_URL.ngrok-free.app/api

# Rebuild (critical!)
./gradlew clean build

# Restart emulator
adb shell pm clear com.example.freshguide
```

### Issue 2: "Connection Refused" from Emulator
**Symptom:** "Failed to sync bootstrap"
**Cause:** ngrok tunnel inactive or emulator can't reach
**Fix:**
```bash
# Check ngrok status
curl https://abc123def456.ngrok-free.app/api/sync/version

# If fails, restart ngrok:
ngrok http 8000

# From emulator browser, test:
http://10.0.2.2:8000/api/sync/version
```

---

## Docker Setup (Optional)

If you prefer to run backend in Docker:

```bash
# Create docker-compose.yml in FreshGuide root
cat > docker-compose.yml << 'EOF'
version: '3.8'
services:
  laravel:
    image: php:11-apache
    ports:
      - "8000:80"
    volumes:
      - /path/to/backend:/var/www/html
    environment:
      - DB_CONNECTION=sqlite
EOF

# Start container
docker-compose up -d

# Expose via ngrok
ngrok http 8000

# Update local.properties with ngrok URL
```

---

## Next Steps

1. ✅ **Setup complete** - App is running
2. 📖 **Read** [ARCHITECTURE.md](ARCHITECTURE.md) to understand code structure
3. 🔐 **Read** [SECURITY.md](SECURITY.md) for authentication details
4. 🔗 **Read** [API_INTEGRATION.md](API_INTEGRATION.md) for endpoint documentation
5. 🤝 **Read** [CONTRIBUTING.md](CONTRIBUTING.md) for development guidelines

---

## Support

- **Backend Issues:** Contact Bryan or Trisha (Backend Developers)
- **Frontend Issues:** Contact Angela, Jovilyn, or Joyce (Frontend Developers)
- **Architecture Questions:** Contact Gab (Team Lead)

For setup questions, check logs:
```bash
adb logcat | grep "FreshGuide"
```

---

**Last Updated:** 2026-04-07  
**University of Caloocan City — BSCS 3A, Group 2**
