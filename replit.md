# Shizuku Optimizer - Android Kotlin Project

## Overview
Ứng dụng Android Kotlin sử dụng Shizuku API để chạy các lệnh shell tối ưu hóa hiệu suất điện thoại. Ứng dụng cho phép người dùng chạy các lệnh shell có sẵn hoặc tùy chỉnh thông qua Shizuku.

## Ngôn Ngữ Ứng Dụng
**Tiếng Việt** - Toàn bộ giao diện ứng dụng đã được Việt hóa hoàn toàn, bao gồm:
- Tất cả chuỗi văn bản trong strings.xml
- Tên và mô tả các lệnh tối ưu trong OptimizationCommands.kt
- Thông báo Toast và Dialog trong các Activity

## UI Design - Gaming Turbo Style
Giao diện được thiết kế theo phong cách gaming turbo sang trọng với:

### Color Palette
- **Primary**: Neon Cyan (#00E5FF)
- **Accent**: Neon Red (#FF1744)
- **Secondary**: Neon Purple (#B14EFF)
- **Background**: Dark gradient (#0A0E17 → #111827)
- **Success**: Neon Green (#00FF88)
- **Warning**: Neon Yellow (#FFE600)

### UI Features
- Dark theme với hiệu ứng glow neon
- Gradient backgrounds và cards với viền sáng
- Button với ripple effects và pulse animations
- Tab indicator với gradient rainbow
- Terminal style output với macOS-style window controls
- Smooth slide transitions giữa các màn hình
- Item fade-in animations trong RecyclerView

### Key Drawables
- `bg_gradient_main.xml` - Background gradient chính
- `bg_card_gaming.xml` - Card style gaming
- `bg_button_neon.xml` - Button với gradient cyan
- `bg_button_accent.xml` - Button với gradient red
- `bg_tab_indicator.xml` - Tab indicator rainbow

## Project Structure
```
./
├── app/
│   ├── src/main/
│   │   ├── java/com/optimizer/shizuku/
│   │   │   ├── ui/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── CustomCommandActivity.kt
│   │   │   │   ├── BackgroundAppsActivity.kt
│   │   │   │   ├── CommandAdapter.kt
│   │   │   │   └── AppListAdapter.kt
│   │   │   └── utils/
│   │   │       ├── ShizukuHelper.kt
│   │   │       └── OptimizationCommands.kt
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   ├── activity_main.xml
│   │   │   │   ├── activity_custom_command.xml
│   │   │   │   ├── activity_background_apps.xml
│   │   │   │   ├── item_command.xml
│   │   │   │   └── item_app.xml
│   │   │   ├── drawable/
│   │   │   │   ├── bg_*.xml (Gaming backgrounds)
│   │   │   │   └── ic_*.xml (Gaming icons)
│   │   │   ├── anim/
│   │   │   │   ├── button_pulse.xml
│   │   │   │   ├── item_fade_in.xml
│   │   │   │   ├── glow_pulse.xml
│   │   │   │   └── slide_*.xml
│   │   │   └── values/
│   │   │       ├── colors.xml (Gaming color palette)
│   │   │       ├── strings.xml (Tiếng Việt)
│   │   │       ├── themes.xml (Dark gaming theme)
│   │   │       └── styles.xml (Gaming styles)
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── build.gradle.kts
└── settings.gradle.kts
```

## Key Components

### ShizukuHelper.kt
- State management với enum ShizukuState
- Binder lifecycle management
- Command execution với reflection cho Shizuku 13.1.5+ (createShizukuProcess)

### OptimizationCommands.kt
- Performance, Battery, RAM, Gaming, General commands
- Tất cả tên và mô tả lệnh bằng tiếng Việt
- Cảnh báo cho lệnh nguy hiểm

### BackgroundAppsActivity.kt
- Quản lý ứng dụng chạy nền
- Hiển thị danh sách ứng dụng bên thứ ba (không bao gồm app hệ thống)
- Tắt/bật quyền chạy nền cho các app được chọn
- Sử dụng lệnh: `cmd appops set <package> RUN_IN_BACKGROUND deny/allow`

## Build Instructions

### GitHub Actions (Recommended)
1. Push project to GitHub
2. GitHub Actions sẽ tự động build APK
3. Download từ Actions > Artifacts

### Local Build
```bash
./gradlew assembleDebug
```

## Recent Changes
- 2025-12-02: Khởi tạo project
- 2025-12-02: Sửa lỗi gradle-wrapper.jar thiếu
- 2025-12-02: Sửa lỗi Shizuku.newProcess() private trong Shizuku 13.1.5 (dùng reflection)
- 2025-12-02: Thêm tính năng quản lý ứng dụng chạy nền (BackgroundAppsActivity)
- 2025-12-02: **Redesign UI phong cách Gaming Turbo** với:
  - Dark theme với neon glow effects
  - Gradient backgrounds (cyan/purple/red)
  - Animated buttons và transitions
  - Terminal style với macOS window controls
  - Gaming-style icons và badges
  - Pulse và fade animations
- 2025-12-02: **Việt hóa toàn bộ ứng dụng**:
  - Dịch strings.xml sang tiếng Việt
  - Dịch tên và mô tả lệnh trong OptimizationCommands.kt
  - Dịch các thông báo Toast và Dialog trong MainActivity.kt và CustomCommandActivity.kt

## User Preferences
- Language: Vietnamese (Tiếng Việt)
- Build system: GitHub Actions
- UI Style: Gaming Turbo / Neon Dark Theme
