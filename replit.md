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
│   │   │   │   └── CommandAdapter.kt
│   │   │   └── utils/
│   │   │       ├── ShizukuHelper.kt
│   │   │       └── OptimizationCommands.kt
│   │   ├── res/
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── .github/workflows/
│   ├── build-apk.yml
│   └── release.yml
├── gradle/
├── build.gradle.kts
└── settings.gradle.kts
```

## Key Components

### ShizukuHelper.kt
- State management với enum ShizukuState
- Binder lifecycle management
- Command execution với chaining

### OptimizationCommands.kt
- Performance, Battery, RAM, Gaming, General commands
- Cảnh báo cho lệnh nguy hiểm

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
- 2024-12-02: Khởi tạo project
- Di chuyển ra thư mục gốc để dễ push GitHub

## User Preferences
- Language: Vietnamese
- Build system: GitHub Actions
