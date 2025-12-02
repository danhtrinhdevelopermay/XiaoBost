# Shizuku Optimizer - Android Kotlin Project

## Overview
Ứng dụng Android Kotlin sử dụng Shizuku API để chạy các lệnh shell tối ưu hóa hiệu suất điện thoại. Ứng dụng cho phép người dùng chạy các lệnh shell có sẵn hoặc tùy chỉnh thông qua Shizuku.

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
│   │   │   │   ├── activity_background_apps.xml
│   │   │   │   ├── item_app.xml
│   │   │   │   └── ...
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

## User Preferences
- Language: Vietnamese
- Build system: GitHub Actions
