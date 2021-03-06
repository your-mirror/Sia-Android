/*
 * Copyright (c) 2017 Nicholas van Dyke. All rights reserved.
 */

package com.vandyke.sia.ui.terminal

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import com.vandyke.sia.App
import com.vandyke.sia.R
import com.vandyke.sia.util.StorageUtil
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader

class TerminalViewModel(application: Application) : AndroidViewModel(application) {

    val output = MutableLiveData<String>()

    private var siacFile: File? = null

    init {
        output.value = getApplication<App>().getString(R.string.terminal_warning)
        siacFile = StorageUtil.copyFromAssetsToAppStorage("siac", application)
    }

    fun appendToOutput(text: String) {
        output.postValue(text)
    }

    fun runSiacCommand(command: String) {
        if (siacFile == null) {
            appendToOutput("\nCould not run siac.\n")
            return
        }
        val fullCommand = command.split(" ".toRegex()).toMutableList()
        fullCommand.add(0, siacFile!!.absolutePath)
        val pb = ProcessBuilder(fullCommand)
        pb.redirectErrorStream(true)
        val siac = pb.start()

        launch(CommonPool) {
            try {
                val stdOut = SpannableStringBuilder()
                stdOut.append("\n" + command + "\n")
                stdOut.setSpan(StyleSpan(Typeface.BOLD), 0, stdOut.length, 0)

                val inputReader = BufferedReader(InputStreamReader(siac.inputStream))
                var line: String? = inputReader.readLine()
                while (line != null) {
                    val toBeAppended = line.replace(siacFile!!.absolutePath, "siac")
                    stdOut.append(toBeAppended + "\n")
                    line = inputReader.readLine()
                }
                inputReader.close()

                appendToOutput(stdOut.toString())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}