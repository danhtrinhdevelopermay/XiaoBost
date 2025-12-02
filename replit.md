# Shizuku Optimizer - Android Kotlin Project

## Overview
Ứng dụng Android Kotlin sử dụng Shizuku API để chạy các lệnh shell tối ưu hóa hiệu suất điện thoại. Ứng dụng cho phép người dùng chạy các lệnh shell có sẵn hoặc tùy chỉnh thông qua Shizuku.

## Project Structure
```
ShizukuOptimizer/
├── app/
│   ├── src/main/
│   │   ├── java/com/optimizer/shizuku/
│   │   │   ├── ui/
│   │   │   │   ├── MainActivity.kt - Màn hình chính
│   │   │   │   ├── CustomCommandActivity.kt - Chạy lệnh tùy chỉnh
│   │   │   │   └── CommandAdapter.kt - Adapter cho RecyclerView
│   │   │   └── utils/
│   │   │       ├── ShizukuHelper.kt - Tích hợp Shizuku API
│   │   │       └── OptimizationCommands.kt - Các lệnh tối ưu hóa
│   │   ├── res/ - Resources (layouts, drawables, values)
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts - App-level Gradle config
├── .github/workflows/
│   ├── build-apk.yml - CI để build APK
│   └── release.yml - Tạo release tự động
├── gradle/ - Gradle wrapper
├── build.gradle.kts - Project-level Gradle config
└── settings.gradle.kts
```

## Key Components

### ShizukuHelper.kt
- Kiểm tra trạng thái Shizuku
- Yêu cầu quyền Shizuku
- Thực thi lệnh shell thông qua Shizuku

### OptimizationCommands.kt
Các lệnh shell được phân loại:
- **Performance**: Fixed performance mode, disable animations, max refresh rate
- **Battery**: Battery saver, doze mode, disable AOD
- **RAM**: Clear cache, kill background apps
- **Gaming**: Max performance combo, disable thermal throttling
- **General**: System info, restore defaults

## Build Instructions

### Local Build
```bash
cd ShizukuOptimizer
./gradlew assembleDebug
```

### GitHub Actions
- Push code để trigger build tự động
- Tạo tag (v1.0) để tạo release

## Recent Changes
- 2024-12-02: Khởi tạo project với Shizuku API integration
- Thêm các lệnh tối ưu hóa cho Performance, Battery, RAM, Gaming
- Cấu hình GitHub Actions để build APK tự động

## User Preferences
- Language: Vietnamese
- Focus: Android Kotlin development
- Build system: GitHub Actions
