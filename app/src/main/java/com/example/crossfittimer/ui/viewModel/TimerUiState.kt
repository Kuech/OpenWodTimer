package com.example.crossfittimer.ui.viewModel

import com.example.crossfittimer.wodTimer.WorkoutState

data class TimerUiState(
    val timeProgress:Float = 0f,
    val roundProgress:Float = 0f,
    val displayTimerValue: String = "00:00",
    val currentRounds: Int = 0,
    val currentWorkoutState: WorkoutState = WorkoutState.PRECOUNTDOWN,
    val paused: Boolean = false,
    val finished: Boolean = false
)