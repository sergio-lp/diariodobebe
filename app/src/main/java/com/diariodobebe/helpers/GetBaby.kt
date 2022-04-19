package com.diariodobebe.helpers

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.diariodobebe.R
import com.diariodobebe.models.Baby
import com.diariodobebe.models.Entry
import com.diariodobebe.ui.main_activity.home.HomeViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import java.nio.charset.StandardCharsets

class GetBaby {

    companion object {
        fun getBaby(file: File): Baby {
            return GsonBuilder().registerTypeAdapter(Baby::class.java, HomeViewModel.Deserializer())
                .create()
                .fromJson(String(file.readBytes(), StandardCharsets.UTF_8), Baby::class.java)
        }

        fun getBabyFile(context: Context): File {
            return File(
                context.filesDir,
                context.getSharedPreferences(
                    context.getString(R.string.PREFS),
                    Context.MODE_PRIVATE
                ).getString("baby", "")?.replace(" ", "") + ".json"
            )
        }

        fun updateBaby(
            ctx: Context,
            newName: String?,
            newBirth: Long?,
            newPicPath: String?,
            newSex: Int?
        ) {
            val file = getBabyFile(ctx)
            val baby = getBaby(file)

            baby.name = newName ?: baby.name
            baby.picPath = newPicPath ?: baby.picPath
            baby.sex = newSex ?: baby.sex
            baby.birthDate = newBirth ?: baby.birthDate

            file.delete()
            val json = Gson().toJson(baby)
            file.writeText(
                json
            )
        }

        private fun showToast(
            context: Context,
            message: String = context.getString(R.string.entry_insert_successful)
        ) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        fun insertEntry(entry: Entry, activity: Activity, successMessage: String? = null) {
            val babyFile = getBabyFile(activity)
            val baby = getBaby(babyFile)
            if (entry.id == null) {
                val id = (baby.lastEntryId ?: 0) + 1
                entry.id = id
            }

            if (entry.id != null) {
                baby.entryList?.removeAll { it.id == entry.id }
            }
            baby.entryList?.add(entry)
            baby.lastEntryId = entry.id
            val gson = Gson()
            val json = gson.toJson(baby)
            Log.e("TAG", "insertEntry: $json")

            babyFile.delete()
            babyFile.writeText(json, StandardCharsets.UTF_8)
            if (successMessage == null) {
                showToast(activity)
            } else {
                showToast(activity, successMessage)
            }
            activity.finish()
        }

        fun removeEntries(ctx: Context, vararg entriesIds: Int?) {
            val file = getBabyFile(ctx)
            val baby = getBaby(file)
            val entryList = baby.entryList ?: run {
                return
            }

            entryList.removeAll {
                entriesIds.contains(it.id)
            }

            baby.entryList = entryList
            val json = Gson().toJson(baby)

            file.delete()
            file.writeText(json, StandardCharsets.UTF_8)
        }

    }
}