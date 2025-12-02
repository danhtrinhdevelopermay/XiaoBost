package com.optimizer.shizuku.utils

import android.content.pm.PackageManager
import rikka.shizuku.Shizuku
import java.io.BufferedReader
import java.io.InputStreamReader

object ShizukuHelper {

    private const val REQUEST_CODE_PERMISSION = 1001

    fun isShizukuInstalled(): Boolean {
        return try {
            Shizuku.pingBinder()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun isShizukuRunning(): Boolean {
        return try {
            Shizuku.pingBinder()
        } catch (e: Exception) {
            false
        }
    }

    fun hasShizukuPermission(): Boolean {
        return try {
            if (Shizuku.isPreV11()) {
                false
            } else {
                Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
            }
        } catch (e: Exception) {
            false
        }
    }

    fun requestShizukuPermission() {
        try {
            if (!Shizuku.isPreV11()) {
                Shizuku.requestPermission(REQUEST_CODE_PERMISSION)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun executeCommand(command: String): CommandResult {
        return try {
            if (!hasShizukuPermission()) {
                return CommandResult(false, "", "Shizuku permission not granted")
            }

            val process = Shizuku.newProcess(arrayOf("sh", "-c", command), null, null)
            
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
            Shizuku.getUid()
        } catch (e: Exception) {
            -1
        }
    }

    fun isRootMode(): Boolean {
        return getShizukuUid() == 0
    }
}

data class CommandResult(
    val success: Boolean,
    val output: String,
    val error: String
)
