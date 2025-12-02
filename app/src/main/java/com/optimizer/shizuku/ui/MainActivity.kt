package com.optimizer.shizuku.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
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
            Toast.makeText(this, "Shizuku permission granted!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Shizuku permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initShizuku()
        setupUI()
        updateShizukuStatus()
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
                        Toast.makeText(this, "Cannot request permission", Toast.LENGTH_SHORT).show()
                    }
                }
                ShizukuHelper.ShizukuState.READY -> {
                    Toast.makeText(this, "Already connected!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnCustomCommand.setOnClickListener {
            if (!ShizukuHelper.hasShizukuPermission()) {
                Toast.makeText(this, "Please grant Shizuku permission first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            startActivity(Intent(this, CustomCommandActivity::class.java))
        }

        binding.btnDownloadShizuku.setOnClickListener {
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
                        tvShizukuStatus.text = "Shizuku is not installed"
                        tvShizukuStatus.setTextColor(getColor(R.color.error_red))
                        statusIcon.setImageResource(R.drawable.ic_error)
                        btnRequestPermission.visibility = View.GONE
                        btnDownloadShizuku.visibility = View.VISIBLE
                        cardWarning.visibility = View.VISIBLE
                    }
                    ShizukuHelper.ShizukuState.INSTALLED_NOT_RUNNING -> {
                        tvShizukuStatus.text = "Shizuku installed but not running"
                        tvShizukuStatus.setTextColor(getColor(R.color.error_red))
                        statusIcon.setImageResource(R.drawable.ic_error)
                        btnRequestPermission.visibility = View.GONE
                        btnDownloadShizuku.visibility = View.GONE
                        cardWarning.visibility = View.VISIBLE
                    }
                    ShizukuHelper.ShizukuState.RUNNING_NO_PERMISSION -> {
                        tvShizukuStatus.text = "Shizuku running - Permission required"
                        tvShizukuStatus.setTextColor(getColor(R.color.warning_yellow))
                        statusIcon.setImageResource(R.drawable.ic_warning)
                        btnRequestPermission.visibility = View.VISIBLE
                        btnDownloadShizuku.visibility = View.GONE
                        cardWarning.visibility = View.GONE
                    }
                    ShizukuHelper.ShizukuState.READY -> {
                        val isRootMode = ShizukuHelper.isRootMode()
                        val modeText = if (isRootMode) "Root Mode" else "ADB Mode"
                        tvShizukuStatus.text = "Shizuku connected ($modeText)"
                        tvShizukuStatus.setTextColor(getColor(R.color.success_green))
                        statusIcon.setImageResource(R.drawable.ic_check)
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
                ShizukuHelper.ShizukuState.NOT_INSTALLED -> "Please install Shizuku first"
                ShizukuHelper.ShizukuState.INSTALLED_NOT_RUNNING -> "Please start Shizuku first"
                ShizukuHelper.ShizukuState.RUNNING_NO_PERMISSION -> "Please grant Shizuku permission first"
                else -> "Shizuku is not ready"
            }
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            return
        }

        if (command.requiresRoot && !ShizukuHelper.isRootMode()) {
            AlertDialog.Builder(this)
                .setTitle("Root Required")
                .setMessage("This command requires root access. Shizuku is running in ADB mode.\n\nDo you want to try anyway?")
                .setPositiveButton("Try Anyway") { _, _ ->
                    checkWarningAndRun(command)
                }
                .setNegativeButton("Cancel", null)
                .show()
            return
        }

        checkWarningAndRun(command)
    }

    private fun checkWarningAndRun(command: OptimizationCommands.OptimizationCommand) {
        if (command.warning != null) {
            AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage(command.warning + "\n\nDo you want to continue?")
                .setPositiveButton("Continue") { _, _ ->
                    runCommand(command)
                }
                .setNegativeButton("Cancel", null)
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
                "Command executed successfully!\n\nOutput:\n${result.output}"
            } else {
                "Command executed successfully!"
            }
        } else {
            "Command failed!\n\nError:\n${result.error}"
        }

        AlertDialog.Builder(this)
            .setTitle(commandName)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showShizukuNotRunningDialog() {
        val isInstalled = ShizukuHelper.isShizukuInstalled(this)
        
        val message = if (isInstalled) {
            "Shizuku is installed but not running. Please start Shizuku first.\n\nYou can start Shizuku via:\n1. Wireless Debugging (Android 11+)\n2. ADB over USB\n3. Root (if device is rooted)"
        } else {
            "Shizuku is not installed. Please install Shizuku to use this app.\n\nShizuku allows apps to run shell commands with elevated permissions without root."
        }

        AlertDialog.Builder(this)
            .setTitle(if (isInstalled) "Shizuku Not Running" else "Install Shizuku")
            .setMessage(message)
            .setPositiveButton("Download Shizuku") { _, _ ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://shizuku.rikka.app/download/"))
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
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
