# FreshGuide Documentation Index

**Last Updated:** 2026-04-07

Welcome to the FreshGuide documentation! This is a comprehensive guide to the campus navigation app for University of Caloocan City.

---

## Quick Navigation

### For New Developers

1. **Start here:** [README.md](README.md)
   - Project overview, features, tech stack
   - Quick start guide
   - Project structure

2. **Then read:** [SETUP_GUIDE.md](SETUP_GUIDE.md)
   - Detailed environment setup
   - Android Studio configuration
   - Backend connection
   - Troubleshooting

3. **Understand architecture:** [ARCHITECTURE.md](ARCHITECTURE.md)
   - MVVM pattern explanation
   - Component descriptions (Activities, Fragments, ViewModels, Repositories)
   - Data flow diagrams
   - Database schema

4. **Before coding:** [CONTRIBUTING.md](CONTRIBUTING.md)
   - Code style guidelines
   - Development workflow (branches, commits, PRs)
   - Testing strategy
   - Common pitfalls to avoid

### For API Integration

- **See:** [API_INTEGRATION.md](API_INTEGRATION.md)
  - Complete endpoint reference
  - Request/response formats
  - Authentication flow
  - Error handling
  - Testing APIs locally

### For Security & Compliance

- **See:** [SECURITY.md](SECURITY.md)
  - Authentication mechanisms (Sanctum tokens)
  - Encryption (in transit, at rest)
  - Current security vulnerabilities & fixes
  - Best practices for developers
  - Incident response procedures

### For Project Leads & Admins

- **See:** [CLAUDE.md](CLAUDE.md)
  - Project context snapshot
  - Recent updates & fixes
  - Known risks
  - AI workflow (if using Codex)

---

## Documentation Overview

| Document | Purpose | Audience | Length |
|----------|---------|----------|--------|
| **README.md** | Project overview, features, quick start | Everyone | 330 lines |
| **SETUP_GUIDE.md** | Detailed setup instructions, troubleshooting | Developers | 599 lines |
| **ARCHITECTURE.md** | Code structure, design patterns, data flow | Developers, leads | 724 lines |
| **SECURITY.md** | Auth, encryption, vulnerabilities, compliance | Developers, leads, admins | 579 lines |
| **API_INTEGRATION.md** | Backend API reference, endpoints, examples | Backend devs, frontend devs | 919 lines |
| **CONTRIBUTING.md** | Code style, PR process, testing, pitfalls | Developers | 857 lines |
| **CLAUDE.md** | Project context snapshot, recent updates | Project leads, AI agents | 121 lines |

**Total Documentation:** 4,008 lines (comprehensive coverage)

---

## Quick Reference

### Technology Stack
```
Frontend (Android):
  • Java 11
  • MVVM Architecture
  • Room ORM (SQLite)
  • Retrofit 2 (HTTP)
  • LiveData (reactive)
  • Navigation Component
  • Material Design 3
  • EncryptedSharedPreferences

Backend:
  • Laravel 11
  • Sanctum (auth tokens)
  • MySQL database
  • RESTful API

DevOps:
  • Gradle 9.0+
  • Android SDK 24-36
  • ngrok (local tunneling)
  • Git version control
```

### Key Files

**Entry Points:**
- `app/src/main/java/com/example/freshguide/SplashActivity.java` — App launcher
- `app/src/main/java/com/example/freshguide/LoginActivity.java` — Auth
- `app/src/main/java/com/example/freshguide/MainActivity.java` — Main nav hub

**Data Layer:**
- `app/src/main/java/com/example/freshguide/network/ApiClient.java` — Retrofit setup
- `app/src/main/java/com/example/freshguide/network/ApiService.java` — API endpoints
- `app/src/main/java/com/example/freshguide/database/AppDatabase.java` — Room schema
- `app/src/main/java/com/example/freshguide/repository/` — Data abstraction

**Configuration:**
- `app/build.gradle.kts` — Build configuration (API URL injection)
- `local.properties` — Local settings (API base URL, SDK path)
- `AndroidManifest.xml` — Permissions, activities, receivers

---

## Getting Started Checklist

- [ ] Read [README.md](README.md) (10 mins)
- [ ] Follow [SETUP_GUIDE.md](SETUP_GUIDE.md) (30 mins)
- [ ] Review [ARCHITECTURE.md](ARCHITECTURE.md) (20 mins)
- [ ] Read [CONTRIBUTING.md](CONTRIBUTING.md) (15 mins)
- [ ] Run app on emulator (5 mins)
- [ ] Create test feature branch (2 mins)
- [ ] Review [API_INTEGRATION.md](API_INTEGRATION.md) if integrating (15 mins)
- [ ] Review [SECURITY.md](SECURITY.md) before PR (10 mins)

**Total Onboarding Time:** ~2 hours

---

## Common Tasks

### "How do I...?"

**...set up my development environment?**
→ [SETUP_GUIDE.md](SETUP_GUIDE.md) sections 1-9

**...understand the project architecture?**
→ [ARCHITECTURE.md](ARCHITECTURE.md) "Overview" + "Core Components"

**...add a new feature?**
→ [CONTRIBUTING.md](CONTRIBUTING.md) "Adding a New Feature"

**...connect to the API?**
→ [API_INTEGRATION.md](API_INTEGRATION.md) "Base URL Configuration" + "Endpoint Reference"

**...secure sensitive data?**
→ [SECURITY.md](SECURITY.md) "Data Encryption" + "Best Practices"

**...debug network issues?**
→ [SETUP_GUIDE.md](SETUP_GUIDE.md) "Troubleshooting" + [API_INTEGRATION.md](API_INTEGRATION.md) "Common Integration Issues"

**...fix a bug in the database?**
→ [ARCHITECTURE.md](ARCHITECTURE.md) "Database Layer" + [CONTRIBUTING.md](CONTRIBUTING.md) "Pitfall #5: Database Migrations"

**...submit a pull request?**
→ [CONTRIBUTING.md](CONTRIBUTING.md) "Pull Request Process"

**...understand security vulnerabilities?**
→ [SECURITY.md](SECURITY.md) "Current Security Status"

---

## Code Samples

### Login Flow
```java
// See API_INTEGRATION.md for full details
LoginActivity → AuthRepository.login() → ApiService.registerStudent() 
→ SessionManager.saveToken() → AuthInterceptor injects token → API call succeeds
```

### Room Search
```java
// See ARCHITECTURE.md for full data flow
SearchBox input → RoomListViewModel.setSearchQuery() 
→ RoomRepository.searchRooms() → RoomDao.searchByName() 
→ Room DB query → RecyclerView update
```

### Admin Create Room
```java
// See API_INTEGRATION.md "Image Upload Details"
AdminFormFragment → ImageUtils.compressImage() 
→ ApiService.adminCreateRoom(multipart) 
→ Laravel: store image + return URL 
→ AdminViewModel updates list
```

---

## Team Contacts

| Role | Name | Responsibility |
|------|------|-----------------|
| **Team Lead** | Gab | Architecture, code reviews, approvals |
| **Frontend** | Angela, Jovilyn, Joyce | UI/UX, screens, styling |
| **Backend** | Bryan, Trisha | Laravel API, database, admin features |
| **DevOps** | (TBD) | Deployment, ngrok tunneling, CI/CD |

For questions, reach out to the relevant team member or your lead (Gab).

---

## Documentation Standards

All documentation is:

- **Accurate:** Generated from actual code or verified against codebase
- **Current:** Last updated 2026-04-07 (automatically kept in sync)
- **Actionable:** Includes setup commands that actually work
- **Complete:** Covers architecture, API, security, setup, and contributing
- **Cross-Referenced:** Links between related documents

If you find outdated information, please:
1. Note the issue
2. Update the documentation
3. Create a PR with the fix

---

## File Structure

```
FreshGuide/
├── README.md                    # Start here!
├── SETUP_GUIDE.md              # Environment setup
├── ARCHITECTURE.md             # Design & structure
├── SECURITY.md                 # Auth, encryption, vulnerabilities
├── API_INTEGRATION.md          # API reference
├── CONTRIBUTING.md             # Code style, PR process
├── DOCUMENTATION.md            # This file
├── CLAUDE.md                   # Project context snapshot
│
├── app/
│   ├── build.gradle.kts        # Build configuration
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/example/freshguide/
│       │   │   ├── ui/              # Activities & Fragments
│       │   │   ├── viewmodel/       # ViewModels
│       │   │   ├── repository/      # Data layer
│       │   │   ├── database/        # Room database
│       │   │   ├── network/         # API clients
│       │   │   ├── model/           # Entities & DTOs
│       │   │   ├── util/            # Utilities
│       │   │   └── receiver/        # Broadcast receivers
│       │   └── res/                 # Layouts, drawables, values
│       └── test/                    # Unit tests
│
├── docs/                            # Additional guides
├── gradle/                          # Version catalog
├── local.properties                 # LOCAL: API URL (gitignored)
└── ...
```

---

## Updating Documentation

When you make code changes:

1. **If adding a feature:** Update [ARCHITECTURE.md](ARCHITECTURE.md) component list
2. **If adding/changing API endpoint:** Update [API_INTEGRATION.md](API_INTEGRATION.md)
3. **If changing setup process:** Update [SETUP_GUIDE.md](SETUP_GUIDE.md)
4. **If discovering a security issue:** Update [SECURITY.md](SECURITY.md)
5. **If establishing new patterns:** Update [CONTRIBUTING.md](CONTRIBUTING.md) architecture guidelines

Keep [CLAUDE.md](CLAUDE.md) updated with "Last Verified" date and recent updates.

---

## Documentation Maintenance

**Monthly:**
- [ ] Update "Last Verified" dates
- [ ] Check for broken links
- [ ] Review security vulnerabilities section

**Quarterly:**
- [ ] Update README feature list if needed
- [ ] Review architecture for major changes
- [ ] Update risk areas based on production issues

**With Each Release:**
- [ ] Update version number in README
- [ ] Add entry to changelog
- [ ] Verify all links work
- [ ] Ensure setup guide reflects current process

---

## Resources & Links

- **Android Developer:** https://developer.android.com
- **Retrofit Docs:** https://square.github.io/retrofit/
- **Room Database:** https://developer.android.com/training/data-storage/room
- **MVVM Pattern:** https://developer.android.com/jetpack/guide
- **Android Security:** https://developer.android.com/privacy-and-security

---

## Glossary

| Term | Meaning |
|------|---------|
| **MVVM** | Model-View-ViewModel architecture pattern |
| **DAO** | Data Access Object (Room pattern for DB queries) |
| **Entity** | Room database table class (annotated with @Entity) |
| **DTO** | Data Transfer Object (API request/response class) |
| **Repository** | Data abstraction layer (queries DB or API) |
| **ViewModel** | Android component managing UI state (survives config change) |
| **LiveData** | Observable data holder (triggers UI updates) |
| **Fragment** | Reusable UI component (part of Activity) |
| **NavComponent** | Android navigation framework (handles fragment transitions) |
| **Retrofit** | HTTP client for API calls |
| **Room** | SQLite ORM (object-relational mapping) |
| **Sanctum** | Laravel authentication token system |
| **ngrok** | Tunneling service to expose local API publicly |

---

## Frequently Asked Questions

**Q: Where do I set the API base URL?**
A: See [SETUP_GUIDE.md](SETUP_GUIDE.md) section "Configure Local Properties"

**Q: Why does my app crash at login with "API base URL is empty"?**
A: See [SETUP_GUIDE.md](SETUP_GUIDE.md) "Troubleshooting" section

**Q: How do I add a new database table?**
A: See [CONTRIBUTING.md](CONTRIBUTING.md) "Adding a New Feature" step 1-3

**Q: What are the security vulnerabilities?**
A: See [SECURITY.md](SECURITY.md) "Current Security Status" section

**Q: How do I test API endpoints?**
A: See [API_INTEGRATION.md](API_INTEGRATION.md) "Testing API Locally"

**Q: What's the code style for naming classes?**
A: See [CONTRIBUTING.md](CONTRIBUTING.md) "Code Style" section

**Q: How do I submit a pull request?**
A: See [CONTRIBUTING.md](CONTRIBUTING.md) "Pull Request Process"

**Q: What branch should I work on?**
A: Create feature branches from `main`: `git checkout -b feature/your-feature-name`

---

## Version History

| Date | Update | Author |
|------|--------|--------|
| 2026-04-07 | Comprehensive documentation generated | Claude Code |
| - | - | - |

---

**Last Updated:** 2026-04-07  
**Maintained By:** FreshGuide Development Team  
**University of Caloocan City — BSCS 3A, Group 2**

**Questions or Issues?** Reach out to Gab (Team Lead) or your team member.
