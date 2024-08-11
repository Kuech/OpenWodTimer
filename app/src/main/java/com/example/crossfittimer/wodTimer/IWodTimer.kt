package com.example.crossfittimer.wodTimer

interface IWodTimer {
    fun start()
    fun pause()
    fun resume()
    fun stop()

    fun setOnTickListener(listener: OnTickListener)
    fun setOnFinishedListener(listener: OnFinishedListener)
    fun setOnPausedListener(listener: OnPausedListener)
    fun setOnNewRoundListener(listener: OnNewRoundListener)

    interface OnTickListener{
        fun onTick(remainingTime: Long, elapsedTime: Long)
    }
    interface OnFinishedListener{
        fun onFinished()
    }
    interface OnPausedListener{
        fun onPaused(paused: Boolean)
    }

    interface OnNewRoundListener{
        fun onNewRound(currentRound: Int, currentWorkoutState: WorkoutState, setTime: Long)
    }
}