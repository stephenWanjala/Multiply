package com.github.stephenwanjala.multiply.presentation.util

import android.os.CountDownTimer

class CountDownTimerWrapper(
    private val totalTimeMillis: Long,
    private val intervalMillis: Long,
    private val onTick: (Long) -> Unit,
    private val onFinish: () -> Unit
) : CountDownTimer(totalTimeMillis, intervalMillis) {

    override fun onTick(millisUntilFinished: Long) {
        onTick.invoke(millisUntilFinished)
    }

    override fun onFinish() {
        onFinish.invoke()
    }
}
