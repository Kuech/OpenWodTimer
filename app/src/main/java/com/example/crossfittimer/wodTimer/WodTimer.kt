package com.example.crossfittimer.wodTimer

import android.os.CountDownTimer

open class WodTimer(
    preCountDownTime:Long = 10 * 1000,
    workCountDownTime: Long,
) : IWodTimer {

    private var paused = false
    internal var currentRemainingTime:Long = 0
    internal var wodTimerData: WodTimerData = WodTimerData()
    internal open val workoutStateSetMap: Map<WorkoutState, Long> =
        mapOf(WorkoutState.PRECOUNTDOWN to preCountDownTime, WorkoutState.WORK to workCountDownTime)
    internal var onTickListener: IWodTimer.OnTickListener? = null
    internal var onFinishedListener: IWodTimer.OnFinishedListener? = null
    internal var onPausedListener: IWodTimer.OnPausedListener? = null
    internal var onNewRoundListener: IWodTimer.OnNewRoundListener? = null
    internal var countDownTimer: CountDownTimer? = null

    override fun start(){
        setNewRound(0)
        startCountDownTimer(currentRemainingTime+1000)
    }

    override fun pause(){
        if(!paused){
            wodTimerData.remainingTimeInMilliSeconds -= (wodTimerData.remainingTimeInMilliSeconds % 1000)
            countDownTimer?.cancel()
            setPaused(true)
        }
    }

    override fun resume(){
        if(paused){
            startCountDownTimer(wodTimerData.remainingTimeInMilliSeconds)
            setPaused(false)
        }
    }

    override fun stop(){
        countDownTimer?.cancel()
        endWorkout()
    }

    private fun setPaused(value:Boolean){
        paused=value
        this.onPausedListener?.onPaused(value)
    }

    internal fun startCountDownTimer(timeRemaining:Long){
        countDownTimer = object : CountDownTimer(timeRemaining, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                wodTimerData.remainingTimeInMilliSeconds = millisUntilFinished-500
                wodTimerData.elapsedTimeInMilliSeconds = (currentRemainingTime - millisUntilFinished+1000)
                onTickListener?.onTick(wodTimerData.remainingTimeInMilliSeconds, wodTimerData.elapsedTimeInMilliSeconds)
            }
            override fun onFinish() {
                countDownFinished()
            }
        }.start()
    }

    override fun setOnTickListener(listener: IWodTimer.OnTickListener) {
        this.onTickListener = listener
    }

    override fun setOnFinishedListener(listener: IWodTimer.OnFinishedListener) {
        this.onFinishedListener = listener
    }

    override fun setOnPausedListener(listener: IWodTimer.OnPausedListener) {
        this.onPausedListener = listener
    }

    override fun setOnNewRoundListener(listener: IWodTimer.OnNewRoundListener) {
        this.onNewRoundListener = listener
    }


    internal open fun countDownFinished(){
        if(wodTimerData.currentWorkoutState==WorkoutState.WORK){
            stop()
        }else{
            wodTimerData.currentWorkoutState = WorkoutState.WORK
            setNewRound(0)
            start()
        }
    }

    internal fun setNewRound(currentNewRound: Int){
        currentRemainingTime = workoutStateSetMap[wodTimerData.currentWorkoutState]!!
        this.onNewRoundListener?.onNewRound(currentNewRound, wodTimerData.currentWorkoutState, currentRemainingTime)
    }

    internal fun endWorkout(){
        this.onFinishedListener?.onFinished()
    }
}