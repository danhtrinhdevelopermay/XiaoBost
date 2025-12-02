package com.optimizer.shizuku.ui

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.optimizer.shizuku.R
import com.optimizer.shizuku.databinding.ActivityBackgroundAppsBinding
import com.optimizer.shizuku.utils.ShizukuHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BackgroundAppsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBackgroundAppsBinding
    private lateinit var appListAdapter: AppListAdapter
    private val selectedApps = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBackgroundAppsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupRecyclerView()
        setupButtons()
        loadApps()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            it.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_pulse))
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    private fun setupRecyclerView() {
        appListAdapter = AppListAdapter(
            onAppSelected = { packageName, isSelected ->
                if (isSelected) {
                    selectedApps.add(packageName)
                } else {
                    selectedApps.remove(packageName)
                }
                updateSelectedCount()
            }
        )
        
        binding.recyclerViewApps.apply {
            layoutManager = LinearLayoutManager(this@BackgroundAppsActivity)
            adapter = appListAdapter
        }
    }

    private fun setupButtons() {
        binding.btnDisableBackground.setOnClickListener {
            it.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_pulse))
            if (selectedApps.isEmpty()) {
                Toast.makeText(this, R.string.no_apps_selected, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!ShizukuHelper.hasShizukuPermission()) {
                Toast.makeText(this, R.string.shizuku_permission_required, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showConfirmDialog(isDisable = true)
        }

        binding.btnEnableBackground.setOnClickListener {
            it.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_pulse))
            if (selectedApps.isEmpty()) {
                Toast.makeText(this, R.string.no_apps_selected, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!ShizukuHelper.hasShizukuPermission()) {
                Toast.makeText(this, R.string.shizuku_permission_required, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showConfirmDialog(isDisable = false)
        }

        binding.btnSelectAll.setOnClickListener {
            it.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_pulse))
            appListAdapter.selectAll()
            selectedApps.clear()
            selectedApps.addAll(appListAdapter.getAllPackageNames())
            updateSelectedCount()
        }

        binding.btnDeselectAll.setOnClickListener {
            it.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_pulse))
            appListAdapter.deselectAll()
            selectedApps.clear()
            updateSelectedCount()
        }
    }

    private fun showConfirmDialog(isDisable: Boolean) {
        val message = if (isDisable) {
            getString(R.string.confirm_disable_background, selectedApps.size)
        } else {
            getString(R.string.confirm_enable_background, selectedApps.size)
        }

        MaterialAlertDialogBuilder(this, R.style.GamingDialogTheme)
            .setTitle(if (isDisable) R.string.disable_background else R.string.enable_background)
            .setMessage(message)
            .setPositiveButton(R.string.confirm) { _, _ ->
                if (isDisable) {
                    disableBackgroundForSelectedApps()
                } else {
                    enableBackgroundForSelectedApps()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun loadApps() {
        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerViewApps.visibility = View.GONE

        CoroutineScope(Dispatchers.IO).launch {
            val apps = getThirdPartyApps()
            
            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.GONE
                binding.recyclerViewApps.visibility = View.VISIBLE
                
                if (apps.isEmpty()) {
                    binding.emptyStateContainer.visibility = View.VISIBLE
                } else {
                    binding.emptyStateContainer.visibility = View.GONE
                    appListAdapter.submitList(apps)
                }
            }
        }
    }

    private fun getThirdPartyApps(): List<AppInfo> {
        val pm = packageManager
        val installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        
        return installedApps
            .filter { appInfo ->
                appInfo.packageName != packageName &&
                appInfo.packageName != "rikka.shizuku" &&
                !appInfo.packageName.startsWith("com.android.") &&
                !appInfo.packageName.startsWith("android") &&
                !appInfo.packageName.startsWith("com.google.android.providers.") &&
                !appInfo.packageName.startsWith("com.google.android.inputmethod") &&
                !appInfo.packageName.startsWith("com.google.android.webview") &&
                appInfo.packageName != "com.google.android.gsf" &&
                appInfo.packageName != "com.google.android.gsf.login" &&
                appInfo.packageName != "com.google.android.gms" &&
                appInfo.packageName != "com.google.android.packageinstaller"
            }
            .map { appInfo ->
                val isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                AppInfo(
                    packageName = appInfo.packageName,
                    appName = pm.getApplicationLabel(appInfo).toString(),
                    icon = pm.getApplicationIcon(appInfo),
                    isSystemApp = isSystemApp
                )
            }
            .sortedWith(compareBy({ it.isSystemApp }, { it.appName.lowercase() }))
    }

    private fun updateSelectedCount() {
        binding.tvSelectedCount.text = getString(R.string.selected_count, selectedApps.size)
    }

    private fun disableBackgroundForSelectedApps() {
        binding.progressBar.visibility = View.VISIBLE
        
        CoroutineScope(Dispatchers.IO).launch {
            var successCount = 0
            var failCount = 0
            
            for (packageName in selectedApps) {
                val command = "cmd appops set $packageName RUN_IN_BACKGROUND deny && cmd appops set $packageName RUN_ANY_IN_BACKGROUND deny"
                
                val result = ShizukuHelper.executeCommand(command)
                if (result.success || result.error.isEmpty()) {
                    successCount++
                } else {
                    failCount++
                }
            }
            
            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.GONE
                showResultDialog(successCount, failCount, isDisable = true)
            }
        }
    }

    private fun enableBackgroundForSelectedApps() {
        binding.progressBar.visibility = View.VISIBLE
        
        CoroutineScope(Dispatchers.IO).launch {
            var successCount = 0
            var failCount = 0
            
            for (packageName in selectedApps) {
                val command = "cmd appops set $packageName RUN_IN_BACKGROUND allow && cmd appops set $packageName RUN_ANY_IN_BACKGROUND allow"
                
                val result = ShizukuHelper.executeCommand(command)
                if (result.success || result.error.isEmpty()) {
                    successCount++
                } else {
                    failCount++
                }
            }
            
            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.GONE
                showResultDialog(successCount, failCount, isDisable = false)
            }
        }
    }

    private fun showResultDialog(successCount: Int, failCount: Int, isDisable: Boolean) {
        val action = if (isDisable) getString(R.string.disabled) else getString(R.string.enabled)
        val message = getString(R.string.background_result, action, successCount, failCount)
        
        MaterialAlertDialogBuilder(this, R.style.GamingDialogTheme)
            .setTitle(R.string.result)
            .setMessage(message)
            .setPositiveButton(R.string.ok) { _, _ ->
                appListAdapter.deselectAll()
                selectedApps.clear()
                updateSelectedCount()
            }
            .show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}

data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: android.graphics.drawable.Drawable,
    val isSystemApp: Boolean = false
)
