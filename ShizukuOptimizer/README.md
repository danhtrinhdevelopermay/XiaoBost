# Shizuku Optimizer

Ứng dụng Android Kotlin sử dụng Shizuku API để chạy các lệnh shell tối ưu hóa hiệu suất điện thoại.

## Tính năng

### Tối ưu hóa hiệu suất (Performance)
- Fixed Performance Mode - Bật chế độ hiệu suất tối đa
- Disable Animations - Tắt hoạt ảnh hệ thống
- Disable Window Blur - Tắt hiệu ứng mờ
- Max Refresh Rate (120Hz) - Đặt tần số quét màn hình tối đa
- Clear System Cache - Xóa bộ nhớ cache hệ thống
- Force DEX Optimization - Tối ưu hóa ứng dụng
- CPU Performance Governor - Đặt CPU ở chế độ hiệu suất cao (cần root)

### Tiết kiệm pin (Battery)
- Enable/Disable Battery Saver - Bật/tắt chế độ tiết kiệm pin
- Force Doze Mode - Buộc thiết bị vào chế độ ngủ sâu
- Disable Always-On Display - Tắt màn hình luôn bật
- Disable Lift to Wake - Tắt đánh thức khi nhấc máy
- CPU Powersave Governor - Đặt CPU ở chế độ tiết kiệm pin (cần root)

### Quản lý RAM
- Clear RAM Cache - Xóa bộ nhớ cache RAM (cần root)
- Kill Background Apps - Dừng ứng dụng nền
- Limit Background Processes - Giới hạn tiến trình nền
- Force Garbage Collection - Buộc thu gom rác bộ nhớ

### Chế độ Gaming
- Gaming Mode - Max Performance - Bật tất cả cài đặt hiệu suất cho game
- Disable Thermal Throttling - Tắt giới hạn nhiệt độ
- CPU Boost Mode - Bật tăng tốc CPU

### Lệnh tùy chỉnh
- Cho phép người dùng nhập và chạy lệnh shell tùy ý
- Lịch sử lệnh đã chạy
- Các lệnh nhanh có sẵn

## Yêu cầu

- Android 7.0 (API 24) trở lên
- [Shizuku](https://shizuku.rikka.app/) đã cài đặt và đang chạy
- Một số lệnh yêu cầu quyền root

## Cài đặt

### Tải APK từ GitHub Actions
1. Vào tab **Actions** trên GitHub
2. Chọn workflow build mới nhất
3. Tải APK từ phần **Artifacts**

### Build từ source
```bash
git clone https://github.com/your-username/ShizukuOptimizer.git
cd ShizukuOptimizer
./gradlew assembleDebug
```

APK sẽ được tạo tại: `app/build/outputs/apk/debug/app-debug.apk`

## Cách sử dụng

1. Cài đặt và khởi chạy [Shizuku](https://shizuku.rikka.app/)
2. Mở Shizuku Optimizer
3. Cấp quyền Shizuku khi được yêu cầu
4. Chọn tab tương ứng (Performance, Battery, RAM, Gaming, General)
5. Nhấn nút "Run" để thực thi lệnh

## Lưu ý

- Một số lệnh có thể không hoạt động trên tất cả thiết bị
- Các lệnh yêu cầu root sẽ được đánh dấu với biểu tượng khiên đỏ
- Sử dụng cẩn thận các lệnh tắt thermal throttling vì có thể gây quá nhiệt
- Sau khi khởi động lại, một số cài đặt có thể bị reset về mặc định

## GitHub Actions

Project đã được cấu hình sẵn GitHub Actions để tự động build APK:

- **build-apk.yml**: Build APK mỗi khi push code hoặc tạo pull request
- **release.yml**: Tạo release tự động khi push tag phiên bản (v1.0, v1.1, ...)

### Tạo Release
```bash
git tag v1.0
git push origin v1.0
```

## Công nghệ sử dụng

- Kotlin
- Shizuku API
- Material Design 3
- ViewBinding
- Coroutines

## License

MIT License
