package com.diariodobebe.ui.main_activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.diariodobebe.R
import com.diariodobebe.models.Baby
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets

class MainViewModel(val app: Application) : AndroidViewModel(app) {
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()
    private val _hasAlreadyLogged = MutableStateFlow(false)
    val hasAlreadyLogged = _hasAlreadyLogged.asStateFlow()
    var baby: Baby? = null

    fun loadBaby() {
        _isLoading.value = true
        if (app.filesDir.listFiles().isNullOrEmpty()) {
            _isLoading.value = false
        }
        app.filesDir.listFiles { file ->
            if (file.extension == "json") {
                val fileText = String(file.readBytes(), StandardCharsets.UTF_8)
                baby = Gson().fromJson(fileText, Baby::class.java)
            }
            return@listFiles true
        }

        _isLoading.value = false
    }

    init {
        viewModelScope.launch {
            _hasAlreadyLogged.value =
                app.getSharedPreferences(app.getString(R.string.PREFS), Context.MODE_PRIVATE)
                    .contains(app.getString(R.string.PREF_OPENED))

            delay(1000)
            _isLoading.value = false
        }
    }
}