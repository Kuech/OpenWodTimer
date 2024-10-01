package com.example.crossfittimer.wodTimer

data class WodTimerData(
    var remainingTimeInMilliSeconds: Long =0,
    var elapsedTimeInMilliSeconds: Long =0,
    var currentWorkoutState: WorkoutState = WorkoutState.PRECOUNTDOWN,
    var currentRound: Int = 0,
)
