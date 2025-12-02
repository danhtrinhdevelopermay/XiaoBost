package com.optimizer.shizuku.utils

object OptimizationCommands {

    data class OptimizationCommand(
        val id: String,
        val name: String,
        val description: String,
        val command: String,
        val category: Category,
        val requiresRoot: Boolean = false
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
            name = "Fixed Performance Mode",
            description = "Enable maximum CPU performance mode",
            command = "cmd power set-fixed-performance-mode-enabled true",
            category = Category.PERFORMANCE
        ),
        OptimizationCommand(
            id = "perf_disable_animations",
            name = "Disable Animations",
            description = "Turn off all system animations for faster UI",
            command = """
                settings put global animator_duration_scale 0.0
                settings put global transition_animation_scale 0.0
                settings put global window_animation_scale 0.0
            """.trimIndent(),
            category = Category.PERFORMANCE
        ),
        OptimizationCommand(
            id = "perf_disable_blur",
            name = "Disable Window Blur",
            description = "Turn off blur effects to save GPU resources",
            command = "settings put global disable_window_blurs 1",
            category = Category.PERFORMANCE
        ),
        OptimizationCommand(
            id = "perf_max_refresh",
            name = "Max Refresh Rate (120Hz)",
            description = "Force maximum display refresh rate",
            command = """
                settings put system peak_refresh_rate 120.0
                settings put system min_refresh_rate 120.0
            """.trimIndent(),
            category = Category.PERFORMANCE
        ),
        OptimizationCommand(
            id = "perf_trim_cache",
            name = "Clear System Cache",
            description = "Clear cached data to free up space and improve speed",
            command = "pm trim-caches 999999999999",
            category = Category.PERFORMANCE
        ),
        OptimizationCommand(
            id = "perf_dexopt",
            name = "Force DEX Optimization",
            description = "Optimize all apps for better performance (takes time)",
            command = "cmd package bg-dexopt-job",
            category = Category.PERFORMANCE
        ),
        OptimizationCommand(
            id = "perf_cpu_performance",
            name = "CPU Performance Governor",
            description = "Set CPU to maximum performance mode (requires root)",
            command = """
                for cpu in /sys/devices/system/cpu/cpu*/cpufreq/scaling_governor; do
                    echo "performance" > ${'$'}cpu 2>/dev/null
                done
            """.trimIndent(),
            category = Category.PERFORMANCE,
            requiresRoot = true
        ),
        OptimizationCommand(
            id = "perf_samsung_cpu",
            name = "Samsung Enhanced CPU",
            description = "Enable Samsung enhanced CPU responsiveness",
            command = "settings put global sem_enhanced_cpu_responsiveness 1",
            category = Category.PERFORMANCE
        )
    )

    val batteryCommands = listOf(
        OptimizationCommand(
            id = "bat_low_power",
            name = "Enable Battery Saver",
            description = "Enable system battery saver mode",
            command = "settings put global low_power 1",
            category = Category.BATTERY
        ),
        OptimizationCommand(
            id = "bat_disable_saver",
            name = "Disable Battery Saver",
            description = "Disable system battery saver mode",
            command = "settings put global low_power 0",
            category = Category.BATTERY
        ),
        OptimizationCommand(
            id = "bat_force_doze",
            name = "Force Doze Mode",
            description = "Force device into deep sleep mode immediately",
            command = "dumpsys deviceidle force-idle",
            category = Category.BATTERY
        ),
        OptimizationCommand(
            id = "bat_unforce_doze",
            name = "Exit Doze Mode",
            description = "Exit from forced doze mode",
            command = "dumpsys deviceidle unforce",
            category = Category.BATTERY
        ),
        OptimizationCommand(
            id = "bat_disable_aod",
            name = "Disable Always-On Display",
            description = "Turn off always-on display to save battery",
            command = "settings put secure doze_always_on 0",
            category = Category.BATTERY
        ),
        OptimizationCommand(
            id = "bat_disable_lift_wake",
            name = "Disable Lift to Wake",
            description = "Disable screen wake when lifting device",
            command = "settings put secure doze_quick_pickup_gesture 0",
            category = Category.BATTERY
        ),
        OptimizationCommand(
            id = "bat_powersave_governor",
            name = "CPU Powersave Governor",
            description = "Set CPU to powersave mode (requires root)",
            command = """
                for cpu in /sys/devices/system/cpu/cpu*/cpufreq/scaling_governor; do
                    echo "powersave" > ${'$'}cpu 2>/dev/null
                done
            """.trimIndent(),
            category = Category.BATTERY,
            requiresRoot = true
        ),
        OptimizationCommand(
            id = "bat_samsung_low_heat",
            name = "Samsung Low Heat Mode",
            description = "Enable low heat mode on Samsung devices",
            command = "settings put global sem_low_heat_mode 1",
            category = Category.BATTERY
        )
    )

    val ramCommands = listOf(
        OptimizationCommand(
            id = "ram_drop_caches",
            name = "Clear RAM Cache",
            description = "Drop memory caches to free up RAM (requires root)",
            command = """
                sync
                echo 3 > /proc/sys/vm/drop_caches
            """.trimIndent(),
            category = Category.RAM,
            requiresRoot = true
        ),
        OptimizationCommand(
            id = "ram_kill_bg",
            name = "Kill Background Apps",
            description = "Force stop all background applications",
            command = "am kill-all",
            category = Category.RAM
        ),
        OptimizationCommand(
            id = "ram_limit_bg_processes",
            name = "Limit Background Processes",
            description = "Limit maximum cached background processes to 16",
            command = "settings put global activity_manager_constants max_cached_processes=16",
            category = Category.RAM
        ),
        OptimizationCommand(
            id = "ram_gc_all",
            name = "Force Garbage Collection",
            description = "Force garbage collection on all apps",
            command = "am gc",
            category = Category.RAM
        )
    )

    val gamingCommands = listOf(
        OptimizationCommand(
            id = "game_max_perf",
            name = "Gaming Mode - Max Performance",
            description = "Enable all performance settings for gaming",
            command = """
                cmd power set-fixed-performance-mode-enabled true
                settings put global animator_duration_scale 0.0
                settings put global transition_animation_scale 0.0
                settings put global window_animation_scale 0.0
                settings put global disable_window_blurs 1
                settings put system peak_refresh_rate 120.0
                settings put system min_refresh_rate 120.0
                am kill-all
            """.trimIndent(),
            category = Category.GAMING
        ),
        OptimizationCommand(
            id = "game_disable_thermal",
            name = "Disable Thermal Throttling",
            description = "Disable thermal throttling (may cause overheating, use with caution)",
            command = """
                settings put global sem_low_heat_mode 0
                settings put global speed_mode 1
            """.trimIndent(),
            category = Category.GAMING
        ),
        OptimizationCommand(
            id = "game_boost_cpu",
            name = "CPU Boost Mode",
            description = "Enable CPU boost for maximum gaming performance",
            command = "settings put global boost_cpu 1",
            category = Category.GAMING
        )
    )

    val generalCommands = listOf(
        OptimizationCommand(
            id = "gen_restore_animations",
            name = "Restore Animations",
            description = "Restore all system animations to default",
            command = """
                settings put global animator_duration_scale 1.0
                settings put global transition_animation_scale 1.0
                settings put global window_animation_scale 1.0
            """.trimIndent(),
            category = Category.GENERAL
        ),
        OptimizationCommand(
            id = "gen_disable_fixed_perf",
            name = "Disable Fixed Performance",
            description = "Disable fixed performance mode",
            command = "cmd power set-fixed-performance-mode-enabled false",
            category = Category.GENERAL
        ),
        OptimizationCommand(
            id = "gen_cpu_info",
            name = "Show CPU Info",
            description = "Display current CPU information",
            command = "cat /proc/cpuinfo | grep -E 'processor|model name|cpu MHz|Hardware'",
            category = Category.GENERAL
        ),
        OptimizationCommand(
            id = "gen_mem_info",
            name = "Show Memory Info",
            description = "Display current memory usage",
            command = "cat /proc/meminfo | head -10",
            category = Category.GENERAL
        ),
        OptimizationCommand(
            id = "gen_battery_stats",
            name = "Show Battery Stats",
            description = "Display battery status information",
            command = "dumpsys battery",
            category = Category.GENERAL
        ),
        OptimizationCommand(
            id = "gen_cpu_governor",
            name = "Show CPU Governor",
            description = "Display current CPU governor",
            command = "cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor 2>/dev/null || echo 'Cannot read governor'",
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
