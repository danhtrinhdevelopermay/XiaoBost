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
│   │   │   │   ├── MainActivity.kt - Màn hình chính với tabs cho các loại lệnh
│   │   │   │   ├── CustomCommandActivity.kt - Chạy lệnh shell tùy chỉnh
│   │   │   │   └── CommandAdapter.kt - Adapter cho RecyclerView
│   │   │   └── utils/
│   │   │       ├── ShizukuHelper.kt - Tích hợp Shizuku API với state management
│   │   │       └── OptimizationCommands.kt - Các lệnh tối ưu hóa đầy đủ
│   │   ├── res/ - Resources (layouts, drawables, values)
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts - App-level Gradle config
├── .github/workflows/
│   ├── build-apk.yml - CI để build APK tự động
│   └── release.yml - Tạo release khi push tag
├── gradle/ - Gradle wrapper
├── build.gradle.kts - Project-level Gradle config
└── settings.gradle.kts
```

## Key Components

### ShizukuHelper.kt
- State management với enum ShizukuState (NOT_INSTALLED, INSTALLED_NOT_RUNNING, RUNNING_NO_PERMISSION, READY)
- Kiểm tra package Shizuku đã cài đặt chưa
- Binder lifecycle management với init() và cleanup()
- Thực thi lệnh shell với command chaining
- Permission handling

### OptimizationCommands.kt
Các lệnh shell được phân loại với cảnh báo cho lệnh nguy hiểm:
- **Performance**: Fixed performance mode, disable animations, max refresh rate, DEX optimization
- **Battery**: Battery saver, doze mode, disable AOD, powersave governor
- **RAM**: Clear cache, kill background apps, limit processes
- **Gaming**: Max performance combo, disable thermal throttling
- **General**: System info, restore defaults

## Build Instructions

### Local Build (requires Android SDK)
```bash
cd ShizukuOptimizer
./gradlew assembleDebug
```

### GitHub Actions (Recommended)
1. Push project to GitHub
2. GitHub Actions will automatically build APK
3. Download from Actions > Artifacts

### Create Release
```bash
git tag v1.0
git push origin v1.0
```

## Recent Changes
- 2024-12-02: Khởi tạo project với Shizuku API integration
- Thêm ShizukuState enum để phân biệt 4 trạng thái Shizuku
- Thêm cảnh báo cho các lệnh nguy hiểm
- Fix multi-command chaining với &&
- Cấu hình GitHub Actions workflows

## User Preferences
- Language: Vietnamese
- Focus: Android Kotlin development
- Build system: GitHub Actions

## Notes
- Đây là Android project, cần push lên GitHub để build APK
- Không thể build/chạy trực tiếp trên Replit (không có Android SDK)
