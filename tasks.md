# Tasks — FreshGuide

> Last updated: 2026-03-10

## In Progress

## Pending
- [ ] Configure emulators for phone + tablet testing [high]
- [ ] Phase 3: Exit route logic + directional arrows [medium]
- [ ] Phase 4: Safety reminders + evacuation instructions [medium]
- [ ] Phase 5: Location selection UI [medium]
- [ ] Registration screen (Create New Account flow) [low]
- [ ] Admin screen UI polish — layouts are functional stubs [medium]

## Before March 18 (backend integration)
- [ ] Set real backend URL in `ApiClient.java` (currently `10.0.2.2:8000` — emulator localhost) [high]
- [ ] Wire bottom nav items to correct fragment destinations (Schedule/Settings/Profile nav IDs must match `nav_graph.xml`) [high]
- [ ] Test sync bootstrap end-to-end (student login → sync → room list loads) [high]

## Blocked

## Completed
- [x] Home page campus map — 2026-03-10
  - [x] `CampusMapView.java` — custom Canvas view, 6 building polygons (MAIN, COURT, LIB, REG, ENT, EXIT)
  - [x] Isometric faceted style: each building split into 4 triangles (N/E/S/W shading) for 3-D diamond look
  - [x] Dynamic sizing via `onSizeChanged` — square design space fitted to view width, centred vertically (works on any screen/aspect ratio)
  - [x] Building tap → navigates to filtered room list for that building
  - [x] Pinch-zoom + pan with clamp; compass FAB re-centres map
  - [x] Orange accent bars (left edge pathway markers)
  - [x] Labels below buildings; MAIN label above building, drawn in second pass so no shape overlays it
  - [x] `fragment_home.xml` redesigned — full FreshGuide logo, search bar, floor chips, map + FAB overlay
  - [x] Floor chips populated dynamically from Room DB (distinct floor numbers)
  - [x] Chip style: outlined pill, green border, fills green on selection
- [x] Room integration — 2026-03-10
  - [x] `RoomDao.searchByBuilding(code, query)` — JOIN rooms/floors/buildings LiveData query
  - [x] `RoomRepository.searchRoomsByBuilding()` wrapper
  - [x] `RoomListViewModel` — MediatorLiveData combining query + buildingCode filters
  - [x] `RoomListFragment` — reads `buildingCode`/`buildingName` args, shows "Rooms in X" header
  - [x] `nav_graph.xml` — added `buildingCode` + `buildingName` args to `roomListFragment`
- [x] Backend seed — 6 UCC campus buildings with Ground Floor each — 2026-03-10
  - [x] `CampusDataSeeder.php` — MAIN, COURT, LIB, REG, ENT, EXIT with descriptions
  - [x] `php artisan migrate:fresh --seed` verified clean
- [x] Project initialized — CLAUDE.md, tasks.md, .gitignore, README.md — 2026-02-28
- [x] Phase 1: Multiple screen support — 2026-02-28
  - [x] Resource qualifiers (layout-sw600dp, values-sw600dp, values/dimens.xml)
  - [x] Color palette and themes (green #29A829, orange #FFA500, Material3)
  - [x] Adaptive launcher icon — ic_launcher_foreground.xml (vector, centered in safe zone)
  - [x] Logo mark vectorized from PNG via potrace → ic_logo_mark.xml
  - [x] logo_with_text.png scaled to all 5 density folders (mdpi → xxxhdpi)
  - [x] Responsive layouts — max-width 480dp card, no stretching on tablet/desktop
- [x] Splash screen — 3-step animation: mark → full logo → spinner → login — 2026-02-28
- [x] Login screen — username/password/toggle/sign in/create account — 2026-02-28
- [x] .gitignore — build outputs, .gradle, .idea state, keystore, OS files — 2026-02-28
- [x] README.md — logo, overview, team credits, phase tracker — 2026-02-28
- [x] Initial commit pushed to GitHub (GABlane/Fresh_Guide) — 2026-02-28
- [x] Dashboard screen — header, greeting, search, 2×2 action cards, recently viewed, bottom nav — 2026-02-28
