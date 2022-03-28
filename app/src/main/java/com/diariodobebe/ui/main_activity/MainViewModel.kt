package com.diariodobebe.ui.main_activity

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.diariodobebe.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainViewModel(val app: Application) : AndroidViewModel(app) {
    private val _isLoading = MutableStateFlow(true)
    private val _shouldShowIntro = MutableStateFlow(false)
    val isLoading = _isLoading.value
    val shouldShowIntro = _shouldShowIntro.value

    init {
        viewModelScope.launch {
            val containsId =
                app.getSharedPreferences(app.getString(R.string.PREFS), Context.MODE_PRIVATE)
                    .contains(app.getString(R.string.PREF_USER_ID))

            _shouldShowIntro.value = !containsId

            delay(1000)

            _isLoading.value = false
        }
    }
}