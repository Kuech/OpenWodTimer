package io.github.openwodtimer.ui.viewModel

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openwodtimer.ui.viewModel.helper.SoundHelper
import io.github.openwodtimer.ui.viewModel.helper.VibratorHelper
import io.github.openwodtimer.ui.WodTimerParam
import io.github.openwodtimer.ui.WorkoutType
import io.github.openwodtimer.utils.TimerServiceManager
import io.github.openwodtimer.wodTimer.WorkoutState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.concurrent.TimeUnit

class ClockViewModel(
    private val vibratorHelper: VibratorHelper,
    private val soundHelper: SoundHelper
) : ViewModel(){

    private var _currentTimeString = MutableLiveData("")
    val currentTimeString: LiveData<String> = _currentTimeString

    private var _uiTimerState = MutableLiveData(TimerUiState())
    val uiTimerState: LiveData<TimerUiState> = _uiTimerState

    private var _wodTimerParameter = MutableLiveData(WodTimerParam(0))
    var wodTimerParameter: LiveData<WodTimerParam> = _wodTimerParameter

    private var timerUpdateJob: Job? = null
    private var currentRemainingTime: Long = 0
    var currentWorkoutType = WorkoutType.AMRAP

    init{
        TimerServiceManager.registerViewModel(this)
        this.printCurrentTime()
    }

    override fun onCleared() {
        TimerServiceManager.unregisterViewModel()
        this.timerUpdateJob?.cancel()
        this.soundHelper.release()
        super.onCleared()
    }

    fun updateWodTimerData(remainingTime: Long, elapsedTime: Long) {
        val progress = (remainingTime - (remainingTime%1000)).toFloat() / currentRemainingTime
        val displayedTime = if(currentWorkoutType == WorkoutType.FORTIME && _uiTimerState.value?.currentWorkoutState != WorkoutState.PRECOUNTDOWN) {
            formatMilliseconds(elapsedTime)
        }else{
            formatMilliseconds(remainingTime)
        }

        _uiTimerState.value = _uiTimerState.value?.copy(timeProgress = progress, displayTimerValue = displayedTime)

        if(remainingTime in 1000..4000){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibratorHelper.vibrate(250)
            }
            soundHelper.playG5BeepSound()
            Log.d("sound", "g5")
        }
        if(remainingTime in 0..1000){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibratorHelper.vibrate(1000)
            }
            soundHelper.playA6BeepSound()
            Log.d("sound", "a6")
        }
    }

    fun onWodTimerPaused(paused: Boolean){
        _uiTimerState.value = _uiTimerState.value?.copy(paused=paused)
    }

    fun onWodTimerFinished(){
        updateWodTimerData(0L, currentRemainingTime)
        onWodTimerNewRound(0, WorkoutState.DONE, 0)
        _uiTimerState.value = _uiTimerState.value?.copy(finished = true)
    }

    fun onWodTimerNewRound(currentRound: Int, currentWorkoutState: WorkoutState, currentRemainingTime: Long){
        this.currentRemainingTime = currentRemainingTime
        val roundProgress = if(currentRound==0){
            0f
        }else{
            currentRound.toFloat() / wodTimerParameter.value?.rounds!!
        }
        _uiTimerState.value = _uiTimerState.value?.copy(currentRounds = currentRound, currentWorkoutState = currentWorkoutState, roundProgress = roundProgress)
    }

    fun onNewTimerSet(){
        _uiTimerState.value = _uiTimerState.value?.copy(finished = false)
    }

    fun setWodTimerParam(wodTimerParam: WodTimerParam){
        currentWorkoutType = enumValues<WorkoutType>()[wodTimerParam.workoutTypeInt]
        this._wodTimerParameter.postValue(wodTimerParam)
    }

    private fun formatMilliseconds(milliseconds: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    private fun printCurrentTime(){
        this.timerUpdateJob = viewModelScope.launch {
            while(true){
                val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                _currentTimeString.value = simpleDateFormat.format(Calendar.getInstance().time)
                delay(1000)
            }
        }
    }
}