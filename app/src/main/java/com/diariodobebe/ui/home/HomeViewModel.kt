package com.diariodobebe.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.diariodobebe.models.*
import com.google.gson.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.lang.reflect.Type
import java.nio.charset.StandardCharsets

class HomeViewModel(val app: Application) : AndroidViewModel(app) {
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()
    var baby: Baby? = null

    fun getEntries(entryList: MutableList<Entry>, entryIdList: MutableList<Int>) {
        _isLoading.value = true
        app.filesDir.listFiles { file ->
            if (file.extension == "json") {
                val fileText = String(file.readBytes(), StandardCharsets.UTF_8)

                val gson = GsonBuilder()
                    .registerTypeAdapter(Baby::class.java, Deserializer()).create()

                baby = gson.fromJson(fileText, Baby::class.java)


                val list = baby!!.entryList!!.sortedByDescending { it.date } as MutableList

                for (entry in list) {
                    if (!entryIdList.contains(entry.id)) {
                        entryList.add(entry as Feeding)
                    }

                    entryIdList.add(entry.id ?: 0)
                }
            }

            return@listFiles true
        }

        _isLoading.value = false
    }

    class Deserializer : JsonDeserializer<Baby> {

        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): Baby {
            val entryList: MutableList<Entry> = mutableListOf()
            val entryListJson = json!!.asJsonObject.getAsJsonArray("entryList")

            for (e in entryListJson) {
                var entry: Entry? = null
                val entryJson = e.asJsonObject
                when (entryJson["type"].asInt) {
                    Entry.EntryType.ENTRY_FEEDING -> {
                        entry = Gson().fromJson(e, Feeding::class.java)
                    }
                    Entry.EntryType.ENTRY_DIAPER -> {
                        entry = Gson().fromJson(e, Diaper::class.java)
                    }
                    Entry.EntryType.ENTRY_EVENT -> {
                        entry = Gson().fromJson(e, Event::class.java)
                    }
                    Entry.EntryType.ENTRY_HEALTH -> {
                        entry = Gson().fromJson(e, Health::class.java)
                    }
                    Entry.EntryType.ENTRY_MEASUREMENT -> {
                        entry = Gson().fromJson(e, Measure::class.java)
                    }
                    Entry.EntryType.ENTRY_SLEEP -> {
                        entry = Gson().fromJson(e, Sleep::class.java)
                    }
                }

                if (entry != null) {
                    entryList.add(entry)
                }
            }

            val baby = Gson().fromJson(json, Baby::class.java)
            baby.entryList = entryList

            return baby
        }

    }
}