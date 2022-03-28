package com.diariodobebe.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.diariodobebe.models.*
import com.google.gson.Gson
import java.nio.charset.StandardCharsets

class HomeViewModel(val app: Application) : AndroidViewModel(app) {
    private val _isLoading = MutableLiveData(true)
    val isLoading = _isLoading.value

    fun getEntries(entryList: MutableList<Entry>, entryIdList: MutableList<String>) {
        app.filesDir.listFiles { file ->
            if (file.extension == "json") {
                val fileText = String(file.readBytes(), StandardCharsets.UTF_8)
                val entry = Gson().fromJson(fileText, Entry::class.java)

                when (entry.type) {
                    Entry.ENTRY_SLEEP -> {
                        entryList.add(entry as Sleep)
                    }
                    Entry.ENTRY_FEEDING -> {
                        entryList.add(entry as Feeding)
                    }
                    Entry.ENTRY_HEALTH -> {
                        entryList.add(entry as HealthEvent)
                    }
                    Entry.ENTRY_MEASUREMENT -> {
                        entryList.add(entry as Measurement)
                    }
                }
                entryIdList.add(entry.id ?: "")
            }
            return@listFiles true
        }
    }
}