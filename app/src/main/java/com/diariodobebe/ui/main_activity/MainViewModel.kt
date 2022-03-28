package com.diariodobebe.ui.main_activity

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.diariodobebe.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(val app: Application) : AndroidViewModel(app) {
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()
    private val _hasAlreadyLogged = MutableStateFlow(false)
    val hasAlreadyLogged = _hasAlreadyLogged.asStateFlow()

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