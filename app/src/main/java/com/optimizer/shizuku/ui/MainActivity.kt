package com.optimizer.shizuku.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.optimizer.shizuku.R
import com.optimizer.shizuku.databinding.ActivityMainBinding
import com.optimizer.shizuku.utils.CommandResult
import com.optimizer.shizuku.utils.OptimizationCommands
import com.optimizer.shizuku.utils.ShizukuHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var commandAdapter: CommandAdapter
    private var currentCategory = OptimizationCommands.Category.PERFORMANCE

    private val requestPermissionResultListener = Shizuku.OnRequestPermissionResultListener { _, grantResult ->
        if (grantResult == PackageManager.PERMISSION_GRANTED) {
            updateShizukuStatus()
            Toast.makeText(this, "Đã cấp quyền Shizuku!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Quyền Shizuku bị từ chối", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initShizuku()
        setupUI()
        updateShizukuStatus()
        startGlowAnimation()
    }

    private fun startGlowAnimation() {
        val glowAnim = AnimationUtils.loadAnimation(this, R.anim.glow_pulse)
        binding.statusIndicator.startAnimation(glowAnim)
    }

    private fun initShizuku() {
        ShizukuHelper.init { state ->
            runOnUiThread {
                updateUIForState(state)
            }
        }
        
        try {
            Shizuku.addRequestPermissionResultListener(requestPermissionResultListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupUI() {
        setupTabs()
        setupRecyclerView()
        setupButtons()
    }

    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentCategory = when (tab?.position) {
                    0 -> OptimizationCommands.Category.PERFORMANCE
                    1 -> OptimizationCommands.Category.BATTERY
                    2 -> OptimizationCommands.Category.RAM
                    3 -> OptimizationCommands.Category.GAMING
                    4 -> OptimizationCommands.Category.GENERAL
                    else -> OptimizationCommands.Category.PERFORMANCE
                }
                loadCommands()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupRecyclerView() {
        commandAdapter = CommandAdapter { command ->
            executeCommand(command)
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = commandAdapter
        }
        loadCommands()
    }

    private fun setupButtons() {
        binding.btnRequestPermission.setOnClickListener {
            it.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_pulse))
            val state = ShizukuHelper.getState(this)
            when (state) {
                ShizukuHelper.ShizukuState.NOT_INSTALLED,
                ShizukuHelper.ShizukuState.INSTALLED_NOT_RUNNING -> {
                    showShizukuNotRunningDialog()
                }
                ShizukuHelper.ShizukuState.RUNNING_NO_PERMISSION -> {
                    if (ShizukuHelper.canRequestPermission()) {
                        ShizukuHelper.requestShizukuPermission()
                    } else {
                        Toast.makeText(this, "Không thể yêu cầu quyền", Toast.LENGTH_SHORT).show()
                    }
                }
                ShizukuHelper.ShizukuState.READY -> {
                    Toast.makeText(this, "Đã kết nối rồi!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnCustomCommand.setOnClickListener {
            it.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_pulse))
            if (!ShizukuHelper.hasShizukuPermission()) {
                Toast.makeText(this, "Vui lòng cấp quyền Shizuku trước", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            startActivity(Intent(this, CustomCommandActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.btnBackgroundApps.setOnClickListener {
            it.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_pulse))
            if (!ShizukuHelper.hasShizukuPermission()) {
                Toast.makeText(this, "Vui lòng cấp quyền Shizuku trước", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            startActivity(Intent(this, BackgroundAppsActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.btnDownloadShizuku.setOnClickListener {
            it.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_pulse))
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://shizuku.rikka.app/download/"))
            startActivity(intent)
        }
    }

    private fun loadCommands() {
        val commands = OptimizationCommands.getCommandsByCategory(currentCategory)
        commandAdapter.submitList(commands)
    }

    private fun updateShizukuStatus() {
        val state = ShizukuHelper.getState(this)
        updateUIForState(state)
    }

    private fun updateUIForState(state: ShizukuHelper.ShizukuState) {
        runOnUiThread {
            binding.apply {
                when (state) {
                    ShizukuHelper.ShizukuState.NOT_INSTALLED -> {
                        tvShizukuStatus.text = "Shizuku chưa được cài đặt"
                        tvShizukuStatus.setTextColor(getColor(R.color.error_red))
                        statusIcon.setImageResource(R.drawable.ic_error_neon)
                        statusContainer.setBackgroundResource(R.drawable.bg_card_status_error)
                        btnRequestPermission.visibility = View.GONE
                        btnDownloadShizuku.visibility = View.VISIBLE
                        cardWarning.visibility = View.VISIBLE
                    }
                    ShizukuHelper.ShizukuState.INSTALLED_NOT_RUNNING -> {
                        tvShizukuStatus.text = "Shizuku đã cài nhưng chưa chạy"
                        tvShizukuStatus.setTextColor(getColor(R.color.error_red))
                        statusIcon.setImageResource(R.drawable.ic_error_neon)
                        statusContainer.setBackgroundResource(R.drawable.bg_card_status_error)
                        btnRequestPermission.visibility = View.GONE
                        btnDownloadShizuku.visibility = View.GONE
                        cardWarning.visibility = View.VISIBLE
                    }
                    ShizukuHelper.ShizukuState.RUNNING_NO_PERMISSION -> {
                        tvShizukuStatus.text = "Shizuku đang chạy - Cần cấp quyền"
                        tvShizukuStatus.setTextColor(getColor(R.color.warning_yellow))
                        statusIcon.setImageResource(R.drawable.ic_warning_neon)
                        statusContainer.setBackgroundResource(R.drawable.bg_card_status_warning)
                        btnRequestPermission.visibility = View.VISIBLE
                        btnDownloadShizuku.visibility = View.GONE
                        cardWarning.visibility = View.GONE
                    }
                    ShizukuHelper.ShizukuState.READY -> {
                        val isRootMode = ShizukuHelper.isRootMode()
                        val modeText = if (isRootMode) "Chế Độ Root" else "Chế Độ ADB"
                        tvShizukuStatus.text = "TURBO ĐÃ KẾT NỐI ($modeText)"
                        tvShizukuStatus.setTextColor(getColor(R.color.success_green))
                        statusIcon.setImageResource(R.drawable.ic_check_neon)
                        statusContainer.setBackgroundResource(R.drawable.bg_card_status_success)
                        btnRequestPermission.visibility = View.GONE
                        btnDownloadShizuku.visibility = View.GONE
                        cardWarning.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun executeCommand(command: OptimizationCommands.OptimizationCommand) {
        val state = ShizukuHelper.getState(this)
        
        if (state != ShizukuHelper.ShizukuState.READY) {
            val message = when (state) {
                ShizukuHelper.ShizukuState.NOT_INSTALLED -> "Vui lòng cài đặt Shizuku trước"
                ShizukuHelper.ShizukuState.INSTALLED_NOT_RUNNING -> "Vui lòng khởi động Shizuku trước"
                ShizukuHelper.ShizukuState.RUNNING_NO_PERMISSION -> "Vui lòng cấp quyền Shizuku trước"
                else -> "Shizuku chưa sẵn sàng"
            }
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            return
        }

        if (command.requiresRoot && !ShizukuHelper.isRootMode()) {
            MaterialAlertDialogBuilder(this, R.style.GamingDialogTheme)
                .setTitle("Cần Root")
                .setMessage("Lệnh này cần quyền root. Shizuku đang chạy ở chế độ ADB.\n\nBạn có muốn thử không?")
                .setPositiveButton("Thử Anyway") { _, _ ->
                    checkWarningAndRun(command)
                }
                .setNegativeButton("Hủy", null)
                .show()
            return
        }

        checkWarningAndRun(command)
    }

    private fun checkWarningAndRun(command: OptimizationCommands.OptimizationCommand) {
        if (command.warning != null) {
            MaterialAlertDialogBuilder(this, R.style.GamingDialogTheme)
                .setTitle("Cảnh Báo")
                .setMessage(command.warning + "\n\nBạn có muốn tiếp tục không?")
                .setPositiveButton("Tiếp Tục") { _, _ ->
                    runCommand(command)
                }
                .setNegativeButton("Hủy", null)
                .show()
        } else {
            runCommand(command)
        }
    }

    private fun runCommand(command: OptimizationCommands.OptimizationCommand) {
        binding.progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            val result = ShizukuHelper.executeCommand(command.command)

            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.GONE
                showResultDialog(command.name, result)
            }
        }
    }

    private fun showResultDialog(commandName: String, result: CommandResult) {
        val message = if (result.success) {
            if (result.output.isNotEmpty()) {
                "Thực thi lệnh thành công!\n\nKết quả:\n${result.output}"
            } else {
                "Thực thi lệnh thành công!"
            }
        } else {
            "Thực thi lệnh thất bại!\n\nLỗi:\n${result.error}"
        }

        MaterialAlertDialogBuilder(this, R.style.GamingDialogTheme)
            .setTitle(commandName)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showShizukuNotRunningDialog() {
        val isInstalled = ShizukuHelper.isShizukuInstalled(this)
        
        val message = if (isInstalled) {
            "Shizuku đã được cài đặt nhưng chưa chạy. Vui lòng khởi động Shizuku trước.\n\nBạn có thể khởi động Shizuku qua:\n1. Gỡ lỗi không dây (Android 11+)\n2. ADB qua USB\n3. Root (nếu thiết bị đã root)"
        } else {
            "Shizuku chưa được cài đặt. Vui lòng cài đặt Shizuku để sử dụng ứng dụng này.\n\nShizuku cho phép ứng dụng chạy lệnh shell với quyền nâng cao mà không cần root."
        }

        MaterialAlertDialogBuilder(this, R.style.GamingDialogTheme)
            .setTitle(if (isInstalled) "Shizuku Chưa Chạy" else "Cài Đặt Shizuku")
            .setMessage(message)
            .setPositiveButton("Tải Shizuku") { _, _ ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://shizuku.rikka.app/download/"))
                startActivity(intent)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        updateShizukuStatus()
    }

    override fun onDestroy() {
        super.onDestroy()
        ShizukuHelper.cleanup()
        try {
            Shizuku.removeRequestPermissionResultListener(requestPermissionResultListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
