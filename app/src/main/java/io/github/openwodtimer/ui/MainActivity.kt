package io.github.openwodtimer.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.ViewModelProvider
import io.github.openwodtimer.ui.viewModel.helper.SoundHelper
import io.github.openwodtimer.ui.viewModel.helper.VibratorHelper
import io.github.openwodtimer.ui.theme.CrossfitTimerTheme
import io.github.openwodtimer.ui.viewModel.ClockViewModel
import io.github.openwodtimer.ui.viewModel.ClockViewModelFactory
import io.github.openwodtimer.ui.viewModel.TimerUiState
import io.github.openwodtimer.utils.TimerServiceManager
import kotlinx.coroutines.Job

class MainActivity : ComponentActivity() {
    private lateinit var clockViewModel : ClockViewModel
    private var timerUpdateJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val viewModelFactory = ClockViewModelFactory(
            VibratorHelper(this),
            SoundHelper(this)
        )

        clockViewModel = ViewModelProvider(this, viewModelFactory)[ClockViewModel::class.java]
        enableEdgeToEdge()
        setContent {
            CrossfitTimerTheme {
                MainView(clockViewModel = this.clockViewModel)
            }
        }
    }

    @Composable
    fun MainView(
        navController: NavHostController = rememberNavController(),
        clockViewModel: ClockViewModel
    ) {
        Scaffold { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = ClockScreen.Clock.name,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(route= ClockScreen.Clock.name){
                    val uiCurrentTimeString by clockViewModel.currentTimeString.observeAsState("00:00")
                    Clock(
                        onMenuButtonClick = {navController.navigate(ClockScreen.WorkoutMenu.name)},
                        currentTimeString = uiCurrentTimeString
                    )
                }
                composable(route= ClockScreen.WorkoutMenu.name){
                    TimerMenu(
                        onSelected = {wodTimerParam: WodTimerParam ->
                            clockViewModel.setWodTimerParam(wodTimerParam)
                            clockViewModel.onNewTimerSet()
                            setWodTimer(wodTimerParam)
                            navController.navigate(ClockScreen.Timer.name)
                        },
                        defaultWorkoutType = clockViewModel.currentWorkoutType
                    )
                }
               composable(route= ClockScreen.Timer.name){
                   val uiTimerState by clockViewModel.uiTimerState.observeAsState(TimerUiState())
                   val wodTimerParameter = clockViewModel.wodTimerParameter.observeAsState()
                    Timer(
                        timerUiState = uiTimerState,
                        onPlayClick = {resumeTimer()},
                        onPauseClick = {pauseTimer()},
                        onStopButtonClick = {stopWodTimer()},
                        onRestartTimer = {
                            clockViewModel.onNewTimerSet()
                            setWodTimer(wodTimerParameter.value ?: WodTimerParam(0))
                        },
                        onSetNewTimer = {navController.popBackStack(ClockScreen.WorkoutMenu.name, inclusive = false)},
                        workoutType = enumValues<WorkoutType>()[wodTimerParameter.value!!.workoutTypeInt],
                        setRounds = wodTimerParameter.value!!.rounds,
                        onBackToClock = {navController.popBackStack(ClockScreen.Clock.name, inclusive = false)}
                    )
                }
            }
        }
    }

    private fun setWodTimer(timerParams: WodTimerParam){
        timerUpdateJob?.cancel()

        val serviceIntent = Intent(application, io.github.openwodtimer.services.WodTimerService::class.java).apply {
            putExtra(WodTimerParamName.WORKTIME.name, timerParams.workTime)
            putExtra(WodTimerParamName.WORKOUTTYPE.name, timerParams.workoutTypeInt)
            putExtra(WodTimerParamName.RESTTIME.name, timerParams.restTime)
            putExtra(WodTimerParamName.ROUNDS.name, timerParams.rounds)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           application.startForegroundService(serviceIntent)
        }
    }

    private fun stopWodTimer(){
        TimerServiceManager.stopTimer()
    }

    private fun resumeTimer(){
        TimerServiceManager.resumeTimer()
    }

    private fun pauseTimer(){
        TimerServiceManager.pauseTimer()
    }
}

enum class ClockScreen{
    Clock,
    Timer,
    WorkoutMenu
}

enum class WodTimerParamName{
    RESTTIME,
    WORKTIME,
    ROUNDS,
    WORKOUTTYPE
}
