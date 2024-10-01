package io.github.openwodtimer.utils

import io.github.openwodtimer.ui.WorkoutType
import io.github.openwodtimer.ui.viewModel.ClockViewModel
import io.github.openwodtimer.wodTimer.WorkoutState

object TimerServiceManager {
    private var viewModel: ClockViewModel?=null
    private var service: io.github.openwodtimer.services.WodTimerService?=null

    fun setViewModelWorkoutType(workoutType: WorkoutType){
        viewModel?.currentWorkoutType = workoutType
    }

    fun updateWodTimerData(remainingTime:Long, elapsedTime:Long) {
        viewModel?.updateWodTimerData(remainingTime, elapsedTime)
    }

    fun wodTimerOnFinished(){
        viewModel?.onWodTimerFinished()
    }
    fun wodTimerOnPaused(paused: Boolean){
        viewModel?.onWodTimerPaused(paused)
    }

    fun wodTimerOnNewRound(currentRound: Int, currentWorkoutState: WorkoutState, remainingTime: Long){
        viewModel?.onWodTimerNewRound(currentRound,currentWorkoutState,remainingTime)
    }

    fun registerViewModel(vm: ClockViewModel){
        viewModel = vm
    }

    fun unregisterViewModel(){
        viewModel = null
    }

    fun registerService(s: io.github.openwodtimer.services.WodTimerService){
        service = s
    }

    fun unregisterService(){
        service = null
    }

    fun pauseTimer(){
        service?.wodTimer?.pause()
    }

    fun stopTimer(){
        service?.wodTimer?.stop()
    }

    fun resumeTimer(){
        service?.wodTimer?.resume()
    }
}
