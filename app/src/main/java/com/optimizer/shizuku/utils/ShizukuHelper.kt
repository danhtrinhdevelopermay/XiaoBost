package com.optimizer.shizuku.utils

import android.content.Context
import android.content.pm.PackageManager
import rikka.shizuku.Shizuku
import java.io.BufferedReader
import java.io.InputStreamReader

object ShizukuHelper {

    private const val REQUEST_CODE_PERMISSION = 1001
    private const val SHIZUKU_PACKAGE = "moe.shizuku.privileged.api"
    
    private fun createShizukuProcess(cmd: Array<String>, env: Array<String>?, dir: String?): Process {
        return Shizuku::class.java.getDeclaredMethod(
            "newProcess",
            Array<String>::class.java,
            Array<String>::class.java,
            String::class.java
        ).apply { 
            isAccessible = true 
        }.invoke(null, cmd, env, dir) as Process
    }

    enum class ShizukuState {
        NOT_INSTALLED,
        INSTALLED_NOT_RUNNING,
        RUNNING_NO_PERMISSION,
        READY
    }

    private var binderReceivedListener: Shizuku.OnBinderReceivedListener? = null
    private var binderDeadListener: Shizuku.OnBinderDeadListener? = null
    private var statusCallback: ((ShizukuState) -> Unit)? = null

    fun init(callback: ((ShizukuState) -> Unit)? = null) {
        statusCallback = callback
        
        binderReceivedListener = Shizuku.OnBinderReceivedListener {
            statusCallback?.invoke(getState(null))
        }
        
        binderDeadListener = Shizuku.OnBinderDeadListener {
            statusCallback?.invoke(getState(null))
        }

        try {
            Shizuku.addBinderReceivedListener(binderReceivedListener!!)
            Shizuku.addBinderDeadListener(binderDeadListener!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun cleanup() {
        try {
            binderReceivedListener?.let { Shizuku.removeBinderReceivedListener(it) }
            binderDeadListener?.let { Shizuku.removeBinderDeadListener(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binderReceivedListener = null
        binderDeadListener = null
        statusCallback = null
    }

    fun isShizukuInstalled(context: Context): Boolean {
        return try {
            context.packageManager.getPackageInfo(SHIZUKU_PACKAGE, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        } catch (e: Exception) {
            false
        }
    }

    fun isBinderAlive(): Boolean {
        return try {
            Shizuku.pingBinder()
        } catch (e: Exception) {
            false
        }
    }

    fun hasShizukuPermission(): Boolean {
        return try {
            if (!isBinderAlive()) {
                return false
            }
            if (Shizuku.isPreV11()) {
                false
            } else {
                Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
            }
        } catch (e: Exception) {
            false
        }
    }

    fun getState(context: Context?): ShizukuState {
        val isInstalled = context?.let { isShizukuInstalled(it) } ?: true
        
        if (!isInstalled) {
            return ShizukuState.NOT_INSTALLED
        }
        
        if (!isBinderAlive()) {
            return ShizukuState.INSTALLED_NOT_RUNNING
        }
        
        if (!hasShizukuPermission()) {
            return ShizukuState.RUNNING_NO_PERMISSION
        }
        
        return ShizukuState.READY
    }

    fun canRequestPermission(): Boolean {
        return isBinderAlive() && !Shizuku.isPreV11()
    }

    fun requestShizukuPermission(): Boolean {
        return try {
            if (!isBinderAlive()) {
                return false
            }
            if (Shizuku.isPreV11()) {
                return false
            }
            if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                return true
            }
            Shizuku.requestPermission(REQUEST_CODE_PERMISSION)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun executeCommand(command: String): CommandResult {
        return try {
            if (!isBinderAlive()) {
                return CommandResult(false, "", "Shizuku is not running")
            }
            
            if (!hasShizukuPermission()) {
                return CommandResult(false, "", "Shizuku permission not granted")
            }

            val normalizedCommand = command.lines()
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .joinToString(" && ")

            val process = createShizukuProcess(arrayOf("sh", "-c", normalizedCommand), null, null)
            
            val outputReader = BufferedReader(InputStreamReader(process.inputStream))
            val errorReader = BufferedReader(InputStreamReader(process.errorStream))
            
            val output = StringBuilder()
            val error = StringBuilder()
            
            var line: String?
            while (outputReader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }
            
            while (errorReader.readLine().also { line = it } != null) {
                error.append(line).append("\n")
            }
            
            val exitCode = process.waitFor()
            
            outputReader.close()
            errorReader.close()
            process.destroy()

            if (exitCode == 0) {
                CommandResult(true, output.toString().trim(), "")
            } else {
                CommandResult(false, output.toString().trim(), error.toString().trim())
            }
        } catch (e: Exception) {
            CommandResult(false, "", "Error: ${e.message}")
        }
    }

    fun getShizukuUid(): Int {
        return try {
            if (!isBinderAlive()) {
                return -1
            }
            Shizuku.getUid()
        } catch (e: Exception) {
            -1
        }
    }

    fun isRootMode(): Boolean {
        return getShizukuUid() == 0
    }

    fun getShizukuVersion(): Int {
        return try {
            if (!isBinderAlive()) {
                return -1
            }
            Shizuku.getVersion()
        } catch (e: Exception) {
            -1
        }
    }
}

data class CommandResult(
    val success: Boolean,
    val output: String,
    val error: String
)
