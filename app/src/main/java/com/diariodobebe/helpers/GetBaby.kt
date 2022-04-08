package com.diariodobebe.helpers

import com.diariodobebe.models.Baby
import com.diariodobebe.ui.home.HomeViewModel
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
    }
}