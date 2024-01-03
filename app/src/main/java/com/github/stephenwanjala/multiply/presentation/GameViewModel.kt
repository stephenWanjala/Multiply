package com.github.stephenwanjala.multiply.presentation

import androidx.lifecycle.ViewModel
import com.github.stephenwanjala.multiply.domain.usecase.GameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameUseCase: GameUseCase
) : ViewModel() {

}