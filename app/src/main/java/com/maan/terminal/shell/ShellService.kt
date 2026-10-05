package com.maan.terminal.shell

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class ShellService : Service() {
    private var shellProcess: Process? = null
    private var writer: OutputStreamWriter? = null
    private val TAG = "ShellService"

    override fun onCreate() {
        super.onCreate()
        startShell()
    }

    private fun startShell() {
        try {
            shellProcess = Runtime.getRuntime().exec("/system/bin/sh")
            writer = OutputStreamWriter(shellProcess!!.outputStream)

            Thread {
                val reader = BufferedReader(InputStreamReader(shellProcess!!.inputStream))
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    Log.d(TAG, "Shell output: $line")
                }
            }.start()

            Thread {
                val reader = BufferedReader(InputStreamReader(shellProcess!!.errorStream))
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    Log.e(TAG, "Shell error: $line")
                }
            }.start()

        } catch (e: Exception) {
            Log.e(TAG, "Error starting shell", e)
        }
    }

    fun executeCommand(command: String): String {
        writer?.write("$command\n")
        writer?.flush()
        return "Command sent"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        try {
            writer?.close()
            shellProcess?.destroy()
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping shell", e)
        }
        super.onDestroy()
    }
}