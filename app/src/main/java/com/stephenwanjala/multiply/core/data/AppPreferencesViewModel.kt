package com.stephenwanjala.multiply.core.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stephenwanjala.multiply.ui.theme.AppTheme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppPreferencesViewModel(
    private val repository: GamePreferencesRepository
) : ViewModel() {

    val appTheme = repository.appTheme.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = AppTheme.SPACE
    )

    fun setAppTheme(theme: AppTheme) {
        viewModelScope.launch { repository.saveAppTheme(theme) }
    }
}
