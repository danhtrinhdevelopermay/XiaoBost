package com.optimizer.shizuku.utils

object OptimizationCommands {

    data class OptimizationCommand(
        val id: String,
        val name: String,
        val description: String,
        val command: String,
        val category: Category,
        val requiresRoot: Boolean = false,
        val warning: String? = null
    )

    enum class Category {
        PERFORMANCE,
        BATTERY,
        RAM,
        GAMING,
        GENERAL
    }

    val performanceCommands = listOf(
        OptimizationCommand(
            id = "perf_fixed_mode",
            name = "Chế Độ Hiệu Năng Cố Định",
            description = "Bật chế độ hiệu năng CPU tối đa",
            command = "cmd power set-fixed-performance-mode-enabled true",
            category = Category.PERFORMANCE
        ),
        OptimizationCommand(
            id = "perf_disable_animations",
            name = "Tắt Hiệu Ứng Chuyển Động",
            description = "Tắt tất cả hiệu ứng chuyển động để UI nhanh hơn",
            command = "settings put global animator_duration_scale 0.0 && settings put global transition_animation_scale 0.0 && settings put global window_animation_scale 0.0",
            category = Category.PERFORMANCE
        ),
        OptimizationCommand(
            id = "perf_disable_blur",
            name = "Tắt Hiệu Ứng Mờ",
            description = "Tắt hiệu ứng mờ để tiết kiệm GPU",
            command = "settings put global disable_window_blurs 1",
            category = Category.PERFORMANCE
        ),
        OptimizationCommand(
            id = "perf_max_refresh",
            name = "Tần Số Quét Tối Đa (120Hz)",
            description = "Ép tần số quét màn hình tối đa",
            command = "settings put system peak_refresh_rate 120.0 && settings put system min_refresh_rate 120.0",
            category = Category.PERFORMANCE
        ),
        OptimizationCommand(
            id = "perf_trim_cache",
            name = "Xóa Bộ Nhớ Cache Hệ Thống",
            description = "Xóa dữ liệu cache để giải phóng dung lượng và tăng tốc (1GB)",
            command = "pm trim-caches 1073741824",
            category = Category.PERFORMANCE
        ),
        OptimizationCommand(
            id = "perf_dexopt",
            name = "Buộc Tối Ưu DEX",
            description = "Tối ưu tất cả ứng dụng để hiệu năng tốt hơn (mất 5-30 phút)",
            command = "cmd package bg-dexopt-job",
            category = Category.PERFORMANCE,
            warning = "Quá trình này mất nhiều thời gian. Điện thoại có thể chậm trong lúc tối ưu."
        ),
        OptimizationCommand(
            id = "perf_cpu_performance",
            name = "CPU Chế Độ Hiệu Năng",
            description = "Đặt CPU ở chế độ hiệu năng tối đa (cần root)",
            command = "for cpu in /sys/devices/system/cpu/cpu*/cpufreq/scaling_governor; do echo performance > \$cpu 2>/dev/null; done",
            category = Category.PERFORMANCE,
            requiresRoot = true
        ),
        OptimizationCommand(
            id = "perf_samsung_cpu",
            name = "Samsung Tăng Cường CPU",
            description = "Bật tăng cường phản hồi CPU Samsung (chỉ Samsung)",
            command = "settings put global sem_enhanced_cpu_responsiveness 1",
            category = Category.PERFORMANCE
        ),
        OptimizationCommand(
            id = "perf_xiaomi_speed_mode",
            name = "Xiaomi Speed Mode (Bật)",
            description = "Bật Xiaomi Speed Mode để hiệu năng tối đa (chỉ Xiaomi/MIUI/HyperOS)",
            command = "settings put secure speed_mode_enable 1 && settings put system speed_mode 1 && settings put global speed_mode_on 1",
            category = Category.PERFORMANCE
        ),
        OptimizationCommand(
            id = "perf_xiaomi_speed_mode_off",
            name = "Xiaomi Speed Mode (Tắt)",
            description = "Tắt Xiaomi Speed Mode (chỉ Xiaomi/MIUI/HyperOS)",
            command = "settings put secure speed_mode_enable 0 && settings put system speed_mode 0 && settings put global speed_mode_on 0",
            category = Category.PERFORMANCE
        ),
        OptimizationCommand(
            id = "perf_xiaomi_high_power",
            name = "Xiaomi Chế Độ Hiệu Năng Cao",
            description = "Bật chế độ nguồn tối đa trên thiết bị Xiaomi (chỉ Xiaomi/MIUI)",
            command = "settings put global power_mode 2",
            category = Category.PERFORMANCE
        ),
        OptimizationCommand(
            id = "perf_xiaomi_disable_power_limit",
            name = "Xiaomi Tắt Giới Hạn Nguồn",
            description = "Tắt giới hạn nguồn CPU/GPU trên Xiaomi (chỉ Xiaomi/MIUI)",
            command = "settings put global powercfg_enable 0",
            category = Category.PERFORMANCE,
            warning = "Điều này có thể gây hao pin và nóng máy nhiều hơn."
        )
    )

    val batteryCommands = listOf(
        OptimizationCommand(
            id = "bat_low_power",
            name = "Bật Tiết Kiệm Pin",
            description = "Bật chế độ tiết kiệm pin hệ thống",
            command = "settings put global low_power 1",
            category = Category.BATTERY
        ),
        OptimizationCommand(
            id = "bat_disable_saver",
            name = "Tắt Tiết Kiệm Pin",
            description = "Tắt chế độ tiết kiệm pin hệ thống",
            command = "settings put global low_power 0",
            category = Category.BATTERY
        ),
        OptimizationCommand(
            id = "bat_force_doze",
            name = "Buộc Chế Độ Doze",
            description = "Buộc thiết bị vào chế độ ngủ sâu ngay lập tức",
            command = "dumpsys deviceidle force-idle",
            category = Category.BATTERY
        ),
        OptimizationCommand(
            id = "bat_unforce_doze",
            name = "Thoát Chế Độ Doze",
            description = "Thoát khỏi chế độ doze bắt buộc",
            command = "dumpsys deviceidle unforce",
            category = Category.BATTERY
        ),
        OptimizationCommand(
            id = "bat_disable_aod",
            name = "Tắt Màn Hình Luôn Bật",
            description = "Tắt màn hình luôn bật để tiết kiệm pin",
            command = "settings put secure doze_always_on 0",
            category = Category.BATTERY
        ),
        OptimizationCommand(
            id = "bat_disable_lift_wake",
            name = "Tắt Nhấc Để Mở Màn Hình",
            description = "Tắt tính năng mở màn hình khi nhấc điện thoại",
            command = "settings put secure doze_quick_pickup_gesture 0",
            category = Category.BATTERY
        ),
        OptimizationCommand(
            id = "bat_powersave_governor",
            name = "CPU Chế Độ Tiết Kiệm",
            description = "Đặt CPU ở chế độ tiết kiệm (cần root)",
            command = "for cpu in /sys/devices/system/cpu/cpu*/cpufreq/scaling_governor; do echo powersave > \$cpu 2>/dev/null; done",
            category = Category.BATTERY,
            requiresRoot = true
        ),
        OptimizationCommand(
            id = "bat_samsung_low_heat",
            name = "Samsung Chế Độ Giảm Nhiệt",
            description = "Bật chế độ giảm nhiệt trên thiết bị Samsung",
            command = "settings put global sem_low_heat_mode 1",
            category = Category.BATTERY
        )
    )

    val ramCommands = listOf(
        OptimizationCommand(
            id = "ram_drop_caches",
            name = "Xóa Cache RAM",
            description = "Xóa bộ nhớ cache để giải phóng RAM (cần root)",
            command = "sync && echo 3 > /proc/sys/vm/drop_caches",
            category = Category.RAM,
            requiresRoot = true
        ),
        OptimizationCommand(
            id = "ram_kill_bg",
            name = "Tắt Ứng Dụng Nền",
            description = "Buộc dừng tất cả ứng dụng chạy nền",
            command = "am kill-all",
            category = Category.RAM
        ),
        OptimizationCommand(
            id = "ram_limit_bg_processes",
            name = "Giới Hạn Tiến Trình Nền",
            description = "Giới hạn tối đa tiến trình nền cache là 16",
            command = "settings put global activity_manager_constants max_cached_processes=16",
            category = Category.RAM
        ),
        OptimizationCommand(
            id = "ram_gc_all",
            name = "Buộc Dọn Rác Bộ Nhớ",
            description = "Buộc dọn rác bộ nhớ trên tất cả ứng dụng",
            command = "am gc",
            category = Category.RAM
        )
    )

    val gamingCommands = listOf(
        OptimizationCommand(
            id = "game_max_perf",
            name = "Chế Độ Gaming - Hiệu Năng Tối Đa",
            description = "Bật tất cả cài đặt hiệu năng cho gaming",
            command = "cmd power set-fixed-performance-mode-enabled true && settings put global animator_duration_scale 0.0 && settings put global transition_animation_scale 0.0 && settings put global window_animation_scale 0.0 && settings put global disable_window_blurs 1 && settings put system peak_refresh_rate 120.0 && settings put system min_refresh_rate 120.0 && am kill-all",
            category = Category.GAMING,
            warning = "Điều này sẽ tắt tất cả ứng dụng nền và tối đa hóa hiệu năng."
        ),
        OptimizationCommand(
            id = "game_disable_thermal",
            name = "Tắt Giảm Nhiệt Độ",
            description = "Tắt giảm hiệu năng do nhiệt (chỉ thiết bị Samsung)",
            command = "settings put global sem_low_heat_mode 0 && settings put global speed_mode 1",
            category = Category.GAMING,
            warning = "CẢNH BÁO: Có thể gây nóng máy. Sử dụng cẩn thận và theo dõi nhiệt độ."
        ),
        OptimizationCommand(
            id = "game_boost_cpu",
            name = "Chế Độ Tăng Tốc CPU",
            description = "Bật tăng tốc CPU để hiệu năng gaming tối đa",
            command = "settings put global boost_cpu 1",
            category = Category.GAMING
        ),
        OptimizationCommand(
            id = "game_xiaomi_max",
            name = "Xiaomi Gaming Mode TỐI ĐA",
            description = "Bật tất cả cài đặt hiệu năng Xiaomi cho gaming",
            command = "settings put secure speed_mode_enable 1 && settings put system speed_mode 1 && settings put global speed_mode_on 1 && settings put global power_mode 2 && settings put global powercfg_enable 0 && settings put global animator_duration_scale 0.0 && settings put global transition_animation_scale 0.0 && settings put global window_animation_scale 0.0 && settings put global disable_window_blurs 1 && am kill-all",
            category = Category.GAMING,
            warning = "Điều này sẽ bật Speed Mode, High Power Mode, tắt giới hạn nguồn và tắt tất cả ứng dụng nền. Có thể gây nóng máy."
        ),
        OptimizationCommand(
            id = "game_xiaomi_restore",
            name = "Xiaomi Gaming Mode TẮT",
            description = "Tắt tất cả cài đặt hiệu năng gaming Xiaomi",
            command = "settings put secure speed_mode_enable 0 && settings put system speed_mode 0 && settings put global speed_mode_on 0 && settings put global power_mode 0 && settings put global powercfg_enable 1 && settings put global animator_duration_scale 1.0 && settings put global transition_animation_scale 1.0 && settings put global window_animation_scale 1.0 && settings put global disable_window_blurs 0",
            category = Category.GAMING
        )
    )

    val generalCommands = listOf(
        OptimizationCommand(
            id = "gen_restore_animations",
            name = "Khôi Phục Hiệu Ứng Chuyển Động",
            description = "Khôi phục tất cả hiệu ứng chuyển động về mặc định",
            command = "settings put global animator_duration_scale 1.0 && settings put global transition_animation_scale 1.0 && settings put global window_animation_scale 1.0",
            category = Category.GENERAL
        ),
        OptimizationCommand(
            id = "gen_disable_fixed_perf",
            name = "Tắt Hiệu Năng Cố Định",
            description = "Tắt chế độ hiệu năng cố định",
            command = "cmd power set-fixed-performance-mode-enabled false",
            category = Category.GENERAL
        ),
        OptimizationCommand(
            id = "gen_cpu_info",
            name = "Xem Thông Tin CPU",
            description = "Hiển thị thông tin CPU hiện tại",
            command = "cat /proc/cpuinfo | grep -E 'processor|model name|cpu MHz|Hardware'",
            category = Category.GENERAL
        ),
        OptimizationCommand(
            id = "gen_mem_info",
            name = "Xem Thông Tin Bộ Nhớ",
            description = "Hiển thị tình trạng sử dụng bộ nhớ hiện tại",
            command = "cat /proc/meminfo | head -10",
            category = Category.GENERAL
        ),
        OptimizationCommand(
            id = "gen_battery_stats",
            name = "Xem Thông Tin Pin",
            description = "Hiển thị thông tin trạng thái pin",
            command = "dumpsys battery",
            category = Category.GENERAL
        ),
        OptimizationCommand(
            id = "gen_cpu_governor",
            name = "Xem Chế Độ CPU",
            description = "Hiển thị chế độ điều khiển CPU hiện tại",
            command = "cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor 2>/dev/null || echo 'Không thể đọc governor'",
            category = Category.GENERAL
        ),
        OptimizationCommand(
            id = "gen_restore_all",
            name = "Khôi Phục Tất Cả Mặc Định",
            description = "Khôi phục tất cả cài đặt về giá trị mặc định",
            command = "cmd power set-fixed-performance-mode-enabled false && settings put global animator_duration_scale 1.0 && settings put global transition_animation_scale 1.0 && settings put global window_animation_scale 1.0 && settings put global disable_window_blurs 0 && settings put global low_power 0",
            category = Category.GENERAL
        )
    )

    val allCommands: List<OptimizationCommand>
        get() = performanceCommands + batteryCommands + ramCommands + gamingCommands + generalCommands

    fun getCommandsByCategory(category: Category): List<OptimizationCommand> {
        return allCommands.filter { it.category == category }
    }

    fun getCommandById(id: String): OptimizationCommand? {
        return allCommands.find { it.id == id }
    }
}
