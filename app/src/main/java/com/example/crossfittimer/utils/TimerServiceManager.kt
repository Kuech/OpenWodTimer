package com.example.crossfittimer.utils

import com.example.crossfittimer.services.WodTimerService
import com.example.crossfittimer.ui.WorkoutType
import com.example.crossfittimer.ui.viewModel.ClockViewModel
import com.example.crossfittimer.wodTimer.WodTimerData
import com.example.crossfittimer.wodTimer.WorkoutState

object TimerServiceManager {
    private var viewModel: ClockViewModel?=null
    private var service: WodTimerService?=null

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

    fun registerService(s: WodTimerService){
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
