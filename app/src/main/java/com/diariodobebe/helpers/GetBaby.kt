package com.diariodobebe.helpers

import android.app.Activity
import android.content.Context
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

        fun updateBaby(ctx: Context, newName: String?, newBirth: Long?, newPicPath: String?, newSex: Int?) {
            val file = getBabyFile(ctx)
            val baby = getBaby(file)
            file.delete()

            baby.name = newName ?: baby.name
            baby.picPath = newPicPath ?: baby.picPath
            baby.sex = newSex ?: baby.sex
            baby.birthDate = newBirth ?: baby.birthDate

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
            val id = (baby.lastEntryId ?: 0) + 1

            val gson = Gson()

            entry.id = id

            baby.entryList!!.add(entry)
            baby.lastEntryId = id
            val json = gson.toJson(baby)

            babyFile.delete()
            babyFile.writeText(json, StandardCharsets.UTF_8)
            if (successMessage == null) {
                showToast(activity)
            } else {
                showToast(activity, successMessage)
            }
            activity.finish()
        }

    }
}