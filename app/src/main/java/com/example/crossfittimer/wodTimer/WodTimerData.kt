package com.example.crossfittimer.wodTimer

data class WodTimerData(
    var remainingTime: Long =0,
    var elapsedTime: Long =0,
    var workoutState: WorkoutState = WorkoutState.PRECOUNTDOWN,
    var rounds: Int = 0,
)
