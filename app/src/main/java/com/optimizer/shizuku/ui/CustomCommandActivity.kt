package com.optimizer.shizuku.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.optimizer.shizuku.databinding.ActivityCustomCommandBinding
import com.optimizer.shizuku.utils.ShizukuHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CustomCommandActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomCommandBinding
    private val commandHistory = mutableListOf<String>()
    private var historyIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomCommandBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.btnExecute.setOnClickListener {
            executeCommand()
        }

        binding.btnClear.setOnClickListener {
            binding.etCommand.text?.clear()
            binding.tvOutput.text = ""
        }

        binding.btnPrevious.setOnClickListener {
            showPreviousCommand()
        }

        binding.btnNext.setOnClickListener {
            showNextCommand()
        }

        binding.chipLs.setOnClickListener { insertCommand("ls -la") }
        binding.chipPs.setOnClickListener { insertCommand("ps aux") }
        binding.chipTop.setOnClickListener { insertCommand("top -n 1") }
        binding.chipDf.setOnClickListener { insertCommand("df -h") }
        binding.chipCatProc.setOnClickListener { insertCommand("cat /proc/cpuinfo") }
        binding.chipGetprop.setOnClickListener { insertCommand("getprop") }
    }

    private fun insertCommand(command: String) {
        binding.etCommand.setText(command)
        binding.etCommand.setSelection(command.length)
    }

    private fun executeCommand() {
        val command = binding.etCommand.text?.toString()?.trim()
        
        if (command.isNullOrEmpty()) {
            Toast.makeText(this, "Please enter a command", Toast.LENGTH_SHORT).show()
            return
        }

        if (!ShizukuHelper.hasShizukuPermission()) {
            Toast.makeText(this, "Shizuku permission not granted", Toast.LENGTH_SHORT).show()
            return
        }

        commandHistory.add(command)
        historyIndex = commandHistory.size

        binding.progressBar.visibility = View.VISIBLE
        binding.btnExecute.isEnabled = false

        CoroutineScope(Dispatchers.IO).launch {
            val result = ShizukuHelper.executeCommand(command)

            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.GONE
                binding.btnExecute.isEnabled = true

                val outputText = StringBuilder()
                outputText.append("$ $command\n\n")

                if (result.success) {
                    if (result.output.isNotEmpty()) {
                        outputText.append(result.output)
                    } else {
                        outputText.append("(Command executed successfully with no output)")
                    }
                } else {
                    outputText.append("ERROR:\n")
                    outputText.append(result.error)
                }

                binding.tvOutput.text = outputText.toString()
                binding.scrollView.post {
                    binding.scrollView.fullScroll(View.FOCUS_DOWN)
                }
            }
        }
    }

    private fun showPreviousCommand() {
        if (commandHistory.isEmpty()) return
        
        historyIndex = if (historyIndex > 0) historyIndex - 1 else 0
        binding.etCommand.setText(commandHistory[historyIndex])
        binding.etCommand.setSelection(binding.etCommand.text?.length ?: 0)
    }

    private fun showNextCommand() {
        if (commandHistory.isEmpty()) return
        
        if (historyIndex < commandHistory.size - 1) {
            historyIndex++
            binding.etCommand.setText(commandHistory[historyIndex])
            binding.etCommand.setSelection(binding.etCommand.text?.length ?: 0)
        } else {
            historyIndex = commandHistory.size
            binding.etCommand.text?.clear()
        }
    }
}
