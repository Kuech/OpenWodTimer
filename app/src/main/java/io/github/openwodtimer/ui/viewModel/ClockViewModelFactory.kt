package io.github.openwodtimer.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.openwodtimer.ui.viewModel.helper.SoundHelper
import io.github.openwodtimer.ui.viewModel.helper.VibratorHelper

class ClockViewModelFactory(
    private val vibratorHelper: VibratorHelper,
    private val soundHelper: SoundHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClockViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClockViewModel(vibratorHelper, soundHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
