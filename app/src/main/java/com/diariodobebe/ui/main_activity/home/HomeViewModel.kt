package com.diariodobebe.ui.main_activity.home

import android.app.Application
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import com.diariodobebe.R
import com.diariodobebe.helpers.PremiumStatus
import com.diariodobebe.models.*
import com.diariodobebe.ui.main_activity.EmptyActivity
import com.google.android.material.snackbar.Snackbar
import com.google.gson.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.lang.reflect.Type
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.TimeUnit

class HomeViewModel(val app: Application) : AndroidViewModel(app) {
    private val step = 10
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()
    var baby: Baby? = null

    private val DIAS_PARA_VERSAO_FREE: Int = 10 //DIAS QUE USUÁRIO FREE PODE VER NO DIÁRIO


    fun getEntries(
        entryList: MutableList<Entry>,
        entryIdList: MutableList<Int>,
        lastIndex: Int,
        view: View
    ): List<Int> {
        _isLoading.value = true
        val returnList = mutableListOf<Int>()
        app.filesDir.listFiles { file ->
            if (file.extension == "json") {
                val fileText = String(file.readBytes(), StandardCharsets.UTF_8)

                val gson = GsonBuilder()
                    .registerTypeAdapter(Baby::class.java, Deserializer()).create()

                baby = gson.fromJson(fileText, Baby::class.java)

                baby!!.entryList!!.sortByDescending { it.date }

                for (i in lastIndex..(lastIndex + step)) {
                    if (i < baby!!.entryList!!.size) {
                        val entry = baby!!.entryList!![i]
                        if (!entryIdList.contains(entry.id)) {
                            if (!PremiumStatus.isPremium(app)) {
                                val today = Calendar.getInstance()
                                val diffInMillis = today.timeInMillis - (entry.date!!)
                                val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis)

                                if (diffInDays <= DIAS_PARA_VERSAO_FREE) {
                                    entryList.add(entry)
                                    returnList.add(entryList.indexOf(entry))
                                    entryIdList.add(entry.id ?: 0)
                                } else {
                                    Snackbar.make(
                                        view,
                                        app.getString(R.string.free_day_limit_warning, DIAS_PARA_VERSAO_FREE),
                                        Snackbar.LENGTH_LONG
                                    ).setAction(app.getString(R.string.be_premium)) {
                                        val intent = Intent(app, EmptyActivity::class.java)
                                        intent.putExtra(
                                            EmptyActivity.FRAGMENT,
                                            EmptyActivity.PREMIUM
                                        )
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                        app.startActivity(intent)
                                    }
                                        .show()
                                }
                            } else {
                                entryList.add(entry)
                                returnList.add(entryList.indexOf(entry))
                                entryIdList.add(entry.id ?: 0)
                            }
                        }
                    } else {
                        break
                    }
                }

                entryList.sortByDescending { it.date }
            }
            return@listFiles true
        }

        _isLoading.value = false

        return returnList
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
                    Entry.EntryType.ENTRY_PICTURE -> {
                        entry = Gson().fromJson(e, Photo::class.java)
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