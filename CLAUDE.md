# FreshGuide Android — Project Context

**Last Verified:** 2026-04-07

## What it is
Dual-role campus navigation app for UCC (University of Caloocan City) students and admins.
Students find rooms, get directions, manage schedules, and sync offline data.
Admins manage buildings, floors, rooms, facilities, origins, routes, and publish data versions.

## Stack
- Language: Java 11
- Package: com.example.freshguide
- Min SDK: 24 (Android 7.0), Target SDK: 36 (Android 15)
- Architecture: MVVM + Repository Pattern + Retrofit + Room + NavComponent
- UI: ConstraintLayout + Material Design 3 + Dark Mode support
- Theme: Theme.Material3.DayNight.NoActionBar
- Auth: Sanctum tokens stored in EncryptedSharedPreferences
- Codebase: 102 Java files, ~15K LOC

## Backend
- Laravel 11 + Sanctum API
- Path: `/home/john/projects/AndroidStudioProjects/Fresh_Guide_BackEnd/laravel/`
- API base URL: set `api.base.url` in `local.properties` (ngrok required — ApiClient enforces HTTPS)
- Room images: POST/DELETE api/admin/rooms/{id}/image endpoints

## Auth Roles & Flow
| Role | Login method | Features |
|------|-------------|----------|
| Student | Student ID only (no password) | View rooms, directions, schedule, sync offline |
| Admin | Email + password | Full CRUD on all entities, version publish |

**Auth Flow:** LoginActivity → AuthRepository → ApiService → Sanctum token → SessionManager (encrypted storage) → AuthInterceptor (token injection)

## Key Screens (102 Activities/Fragments)

### Activities
- **SplashActivity** — Entry point, shows splash for 2-3s
- **LoginActivity** — Student ID / admin login, registration dialog
- **MainActivity** — NavController hub, BottomNav (Home, Rooms, Directions, Schedule, Profile, Admin)
- **QrScannerActivity** — QR code scanning with CameraX + ML Kit
- **OnboardingActivity** — First-time user tutorial

### User Fragments
- **HomeFragment** — Dashboard with building/floor maps, sync status, quick actions
- **RoomListFragment** — Search + RecyclerView with RoomAdapter
- **RoomDetailFragment** — Room info, facilities, "Get Directions" button
- **DirectionsFragment** — Turn-by-turn directions with RouteStepAdapter
- **ScheduleFragment** — Class schedule calendar view with reminders
- **ProfileFragment** — Student info, sync version, logout button
- **SettingsFragment** — Theme preference, app settings

### Admin Fragments
- **AdminDashboardFragment** — Overview counts, nav to CRUD screens
- **AdminListFragment** (generic) — List CRUD entities (Buildings, Floors, Rooms, etc.)
- **AdminFormFragment** (generic) — Create/edit with image upload (Rooms)
- **AdminPublishFragment** — Bump sync version, trigger student sync

## Architecture Notes
- **MVVM:** All ViewModels manage UI state, observe LiveData
- **Repository Pattern:** Clean data abstraction (API + Room DB)
- **Offline-First:** All queries use Room DB; API calls update DB
- **Admin Lists:** GenericListAdapter + AdminViewModel for code reuse
- **Admin Forms:** fragment_admin_form.xml template (3 EditText + Save)
- **Room Adapters:** RoomAdapter (ListAdapter + DiffUtil), RouteStepAdapter, ScheduleAdapter
- **Database:** Room v7 with migrations (1→7), 10 entities
- **Sync:** Bootstrap on first login via /api/sync/bootstrap; version check on subsequent opens
- **Images:** Multipart upload to backend, compression before upload, temp storage on device
- **Nav:** NavComponent with action IDs (action_home_to_roomDetail), avoid direct fragment navigation

## Recent Fixes (2026-04-07)
- Image corruption issue fixed by proper compression
- Layout constraint issues on tablets addressed with layout-sw600dp overrides
- API compatibility improved (HTTP → HTTPS auto-conversion, ngrok handling)
- Security review completed; 4 vulnerabilities identified (1 critical, 3 medium/low)
- Code quality review: identified god classes (HomeFragment 800+ LOC) and potential memory leaks

## Known Issues & Vulnerabilities

### Critical
- **Unencrypted Room Database** (MEDIUM severity) — SQLite not encrypted; data extractable with device access. Mitigation: public info only. Fix: Q3 2026 (SQLCipher).

### High
- **No Login Rate Limiting** — Brute-force attack possible on admin password. Fix: Q2 2026 (middleware throttle).
- **Debug Logging in Release** (LOW severity) — OkHttp logs may contain sensitive data. Fix: Q2 2026 (disable in release builds).

### Medium
- **No Certificate Pinning** — MITM possible with compromised CA. Mitigation: HTTPS + TLS 1.2+. Fix: Q3 2026.
- **No Account Lockout** — Unlimited login attempts. Fix: Q2 2026.

## Risk Areas
- **API_BASE_URL:** Must be set before build — app throws IllegalStateException at startup if blank
- **ngrok Sessions:** URL changes on restart; requires `local.properties` update and `./gradlew clean build`
- **Token Expiry:** Sanctum tokens valid 365 days; no auto-refresh (re-login required after expiry)
- **Database Migrations:** Existing users' devices must have migration path; missing migration = crash
- **NavComponent:** Must use action IDs (action_home_to_roomDetail) not fragment IDs; silent failures otherwise
- **God Classes:** HomeFragment ~800 LOC, ScheduleFragment ~1000 LOC; refactoring needed
- **Memory Leaks:** Handler callbacks, static references, listener registrations without cleanup

## Recent Updates
- Home map + floor map integration lives in `HomeFragment` with custom `map_floor_1..5` layouts
- Admin dashboard now has a dedicated logout button (`btn_admin_logout`)
- Room card click regression fixed by restoring `action_home_to_roomDetail` navigation from Home floor layouts

## Agent Routing Defaults
- Building agent: gpt-5.3-codex (high)
- Planner agent: claude-sonnet-3-5v2

## AI Workflow (Claude Code ↔ Codex Bridge)

This project uses a two-agent system:
- **Claude Code** — plans, delegates, synthesizes. Talks to the user.
- **Codex** — executes. Writes code, runs commands, fixes builds.

### Bridge Folder
```
~/ai-bridge/
  inbox/    ← Claude drops TASK-*.md files here
  outbox/   ← Codex writes TASK-*.result.md files here
  archive/  ← Completed pairs (do not touch)
  status.json
```

### How to delegate a task to Codex
Claude Code writes a `TASK-NNN.md` file to `~/ai-bridge/inbox/` in this format:
```
---
task_id: TASK-001
created: <ISO timestamp>
mode: L1 | L2 | L3
priority: low | normal | high
status: pending
project: Fresh_Guide
project_dir: /Users/gearworxdev/Projects/Fresh Guide/Fresh_Guide
---

## Context
Why this task exists.

## Steps
1. Do this
2. Then this

## Files to Touch
- path/to/file.java

## Success Criteria
- Build passes
- Feature works as described
```

### Worker commands
```bash
bridge-launch    # start the worker (auto-picks up tasks)
bridge-logs      # tail live logs
bridge-stop      # stop the worker
bridge-verify    # check status
```

### Task numbering
Check `~/ai-bridge/inbox/` and `~/ai-bridge/archive/` for the latest TASK-NNN to determine the next number.
