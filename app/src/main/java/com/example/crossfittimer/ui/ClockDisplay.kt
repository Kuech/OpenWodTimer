package com.example.crossfittimer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.crossfittimer.ui.theme.CrossfitTimerTheme
import com.example.crossfittimer.ui.viewModel.TimerUiState
import com.example.crossfittimer.wodTimer.WorkoutState

@Composable
fun Timer(
    timerUiState: TimerUiState,
    workoutType: WorkoutType,
    setRounds:Int = 0,
    onPlayClick:() -> Unit = {},
    onPauseClick:() -> Unit = {},
    onStopButtonClick:() -> Unit = {},
    onRestartTimer:() -> Unit = {},
    onSetNewTimer:() -> Unit = {},
    onBackToClock:() -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        CircularTimeProgress(
            progress = timerUiState.timeProgress,
            text = timerUiState.displayTimerValue,
            workoutState = timerUiState.currentWorkoutState)
        if(workoutType==WorkoutType.EMOM||workoutType==WorkoutType.TABATA){
            Spacer(modifier = Modifier.height(64.dp))
            CircularRoundProgress(
                progress = timerUiState.roundProgress,
                text = "${timerUiState.currentRounds}/${setRounds}"
            )
        }
    }

    //MenuButton(onMenuButtonClick)
    if(timerUiState.finished){
        EndOfTimerSelectionButtons(
            onRestartTimer=onRestartTimer,
            onReturnToClock = onBackToClock,
            onTimerMenu = onSetNewTimer
        )
    }else{
        ResumePauseButton(
            timerIsPaused = timerUiState.paused,
            onPlayClick = onPlayClick,
            onPauseClick = onPauseClick,
            onStopButtonClick = onStopButtonClick
        )
    }
}

@Composable
fun Clock(
    onMenuButtonClick: () -> Unit = {},
    currentTimeString: String
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = currentTimeString,
            fontSize = 42.sp,
        )
    }

    MenuButton(onMenuButtonClick)
}

@Composable
fun MenuButton(onMenuButtonClick: () -> Unit){
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ){
        Button(
            onClick = onMenuButtonClick
        ) {
            Text(
                text = "Menu"
            )
        }
    }
}

@Composable
fun ResumePauseButton(
    onPlayClick:() -> Unit = {},
    onPauseClick:() -> Unit = {},
    onStopButtonClick:() -> Unit = {},
    timerIsPaused:Boolean
){

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        if(timerIsPaused){
            Button(
                onClick = { onPlayClick() }) {
                Text(text = "Resume")
            }
        }else{
            Button(
                onClick = { onPauseClick() }) {
                Text(text = "Pause")
            }
        }
        Button(
            onClick = {onStopButtonClick() }) {
                Text(text = "Stop")
        }
    }
}

@Composable
fun EndOfTimerSelectionButtons(
    onRestartTimer:() -> Unit = {},
    onTimerMenu:() -> Unit = {},
    onReturnToClock:() -> Unit = {},
){

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        Button(
            onClick = { onRestartTimer() }) {
            Text(text = "Restart Timer") //TODO Change to a more simpler and fitting name
        }
        Button(
            onClick = { onTimerMenu() }) {
            Text(text = "Set New Timer") //TODO Change to a more simpler and fitting name
        }
        Button(
            onClick = { onReturnToClock() }) {
            Text(text = "Back to clock") //TODO Change to a more simpler and fitting name
        }

    }
}

@Composable
fun CircularTimeProgress(progress: Float, text: String, workoutState: WorkoutState = WorkoutState.WORK) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(250.dp)
    ) {
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxSize(),
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = text, fontSize = 42.sp)
            Text(text=workoutState.name, fontSize = 24.sp)
        }
    }
}

@Composable
fun CircularRoundProgress(progress: Float, text: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(100.dp)
    ) {
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxSize(),
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = text, fontSize = 16.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ClockPreview() {
    CrossfitTimerTheme(darkTheme = true) {
        Clock(currentTimeString = "17:00")
    }
}

@Preview(showBackground = true)
@Composable
fun TimerPreview() {
    CrossfitTimerTheme(darkTheme = true) {
        Timer(timerUiState = TimerUiState(
            timeProgress = 0.5f,
            displayTimerValue = "05:00",
            paused = false,
            finished = false
        ),
            workoutType = WorkoutType.EMOM
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TimerPreviewAtEnd() {
    CrossfitTimerTheme(darkTheme = true) {
        Timer(timerUiState = TimerUiState(
            timeProgress = 1f,
            displayTimerValue = "00:00",
            paused = false,
            finished = true
        ),
            workoutType = WorkoutType.AMRAP
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TimerPreviewAtPaused() {
    CrossfitTimerTheme(darkTheme = true) {
        Timer(timerUiState = TimerUiState(
            timeProgress = .75f,
            displayTimerValue = "02:50",
            paused = true,
            finished = false
        ),
            workoutType = WorkoutType.AMRAP
        )
    }
}
