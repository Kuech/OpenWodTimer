package com.example.crossfittimer.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import com.chargemap.compose.numberpicker.NumberPicker
import com.example.crossfittimer.ui.theme.CrossfitTimerTheme

@Composable
fun TimerMenu(
    modifier: Modifier = Modifier,
    onSelected: (WodTimerParam) -> Unit = {},
    defaultWorkoutType: WorkoutType
) {
    var selectedIndex by remember { mutableStateOf(defaultWorkoutType) }
    var expand by remember { mutableStateOf(false) }
    Box(
        modifier
            .padding(8.dp)
            .border(
                border = BorderStroke(1.dp, Color.Black),
                shape = RoundedCornerShape(4.dp)
            )
            .clickable {
                expand = true
            },
        contentAlignment = Alignment.Center
    )
    {
        Text(
            text=selectedIndex.name,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        )
        DropdownMenu(
            expanded = expand,
            onDismissRequest = {
                expand = false
            },
            properties = PopupProperties(
                focusable = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            ),
            modifier = Modifier
                .padding(2.dp)
                .fillMaxWidth(.4f)
        ) {
            WorkoutType.entries.forEach { workoutType ->
                DropdownMenuItem(
                    text = { Text(workoutType.name) },
                    onClick = {
                        selectedIndex = workoutType
                        expand = false
                    }
                )
            }
        }
    }
    val wodTimerParam = WodTimerParam(selectedIndex.ordinal)
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when(selectedIndex){
            WorkoutType.AMRAP -> {
                wodTimerParam.workTime = 10 * 60000
                SimpleWorkoutOption(
                    defaultMinuteInt = 10,
                    defaultSecondInt = 0,
                    labelString = "As much reps in ",
                    returnValue = {value->
                        wodTimerParam.workTime = value
                    }
                )
            }
            WorkoutType.FORTIME -> {
                wodTimerParam.workTime = 15 * 60000
                SimpleWorkoutOption(
                    defaultMinuteInt = 15,
                    defaultSecondInt = 0,
                    labelString = "For ",
                    returnValue = {value->
                        wodTimerParam.workTime = value
                    }
                )
            }
            WorkoutType.EMOM -> {
                wodTimerParam.workTime = 1 * 60000
                wodTimerParam.rounds = 10
                MultiRoundWorkoutOption(
                    defaultWorkMinuteInt = 1,
                    defaultWorkSecondsInt = 0,
                    defaultRestMinuteInt = 0,
                    defaultRestSecondsInt = 0,
                    defaultRoundsInt = 10,
                    returnValue = {value->
                        wodTimerParam.workTime = value.workTime
                        wodTimerParam.restTime = value.restTime
                        wodTimerParam.rounds = value.rounds
                    }
                )
            }
            WorkoutType.TABATA -> {
                wodTimerParam.workTime = 45 * 1000
                wodTimerParam.restTime = 15 * 1000
                wodTimerParam.rounds = 20
                MultiRoundWorkoutOption(
                    defaultWorkMinuteInt = 0,
                    defaultWorkSecondsInt = 45,
                    defaultRestMinuteInt = 0,
                    defaultRestSecondsInt = 15,
                    defaultRoundsInt = 20,
                    returnValue = {value->
                        wodTimerParam.workTime = value.workTime
                        wodTimerParam.restTime = value.restTime
                        wodTimerParam.rounds = value.rounds
                    }
                )
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Button(onClick = {
            onSelected(wodTimerParam)
        }) {
            Text("Start!")
        }
    }

}

@Composable
fun RoundInputNumberPicker(
    onRoundsValueChange: (rounds:Int) -> Unit,
    defaultRoundsValueInt:Int,
    labelString: String

){
    var pickerValue by remember { mutableIntStateOf(defaultRoundsValueInt) }
    Row(modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically){
        Text(text = labelString, fontSize = 18.sp)
        Spacer(modifier = Modifier.width(8.dp))
        NumberPicker(
            value = pickerValue,
            onValueChange = {value->
                pickerValue = value
                onRoundsValueChange(value)
            },
            range = 1..999,
            dividersColor = MaterialTheme.colorScheme.primary, //Temp for dark mode
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Rounds", fontSize = 18.sp)
    }
}

@Composable
fun TimeInputNumberPicker(
    onTimeInLongChange: (timeLong:Long) -> Unit,
    defaultMinuteValueInt:Int,
    defaultSecondsValueInt:Int,
    labelString:String){
    var minutes:Int by remember { mutableIntStateOf(defaultMinuteValueInt) }
    var seconds:Int by remember { mutableIntStateOf(defaultSecondsValueInt) }

    Row(modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically){
        Text(text = labelString, fontSize = 18.sp)
        Spacer(modifier = Modifier.width(8.dp))
        NumberPicker(
            value = minutes,
            onValueChange = {value->
                minutes = value
                onTimeInLongChange((minutes*60000+seconds*1000).toLong())
            },
            range = 0..59,
            dividersColor = MaterialTheme.colorScheme.primary, //Temp for dark mode
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = ":", fontSize = 18.sp)
        Spacer(modifier = Modifier.width(8.dp))
        NumberPicker(
            value = seconds,
            onValueChange = {value->
                seconds = value
                onTimeInLongChange((minutes*60000+seconds*1000).toLong())
            },
            range = 0..59,
            dividersColor = MaterialTheme.colorScheme.primary, //Temp for dark mode
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.primary
            )
        )
    }

}

@Composable
fun SimpleWorkoutOption(
    defaultMinuteInt: Int,
    defaultSecondInt: Int,
    labelString: String,
    returnValue: (workCountdownLong:Long) -> Unit){
    TimeInputNumberPicker(
        defaultMinuteValueInt = defaultMinuteInt,
        defaultSecondsValueInt = defaultSecondInt,
        labelString = labelString,
        onTimeInLongChange = {timeLong ->
            returnValue(timeLong)
        }
    )
}

@Composable
fun MultiRoundWorkoutOption(
    defaultWorkMinuteInt: Int,
    defaultWorkSecondsInt: Int,
    defaultRestMinuteInt: Int,
    defaultRestSecondsInt: Int,
    defaultRoundsInt: Int,
    returnValue: (wodTimerParam: WodTimerParam) -> Unit){
    val outWodTimerParam:WodTimerParam by remember { mutableStateOf(WodTimerParam(0,0)) }
    TimeInputNumberPicker(
        defaultMinuteValueInt = defaultWorkMinuteInt,
        defaultSecondsValueInt = defaultWorkSecondsInt,
        labelString = "Work For",
        onTimeInLongChange = {timeLong ->
            outWodTimerParam.workTime = timeLong
            returnValue(outWodTimerParam)
        }
    )
    TimeInputNumberPicker(
        defaultMinuteValueInt = defaultRestMinuteInt,
        defaultSecondsValueInt = defaultRestSecondsInt,
        labelString = "Rest For",
        onTimeInLongChange = {timeLong ->
            outWodTimerParam.restTime = timeLong
            returnValue(outWodTimerParam)
        }
    )
    RoundInputNumberPicker(
        onRoundsValueChange = { rounds: Int ->
            outWodTimerParam.rounds = rounds
            returnValue(outWodTimerParam)
        },
        defaultRoundsValueInt = defaultRoundsInt,
        labelString = "For"
    )
}

data class WodTimerParam(
    val workoutTypeInt: Int,
    var workTime: Long = 0L,
    var restTime: Long = 0L,
    var rounds: Int = 0
)

@Preview(showBackground = true, backgroundColor = android.graphics.Color.DKGRAY.toLong())
@Composable
fun PrintCurrentTimePreviewDarkMode() {
    CrossfitTimerTheme(darkTheme = true, dynamicColor = true) {
        TimerMenu(defaultWorkoutType = WorkoutType.EMOM)
    }
}

@Preview(showBackground = true)
@Composable
fun PrintCurrentTimePreviewLightMode() {
    CrossfitTimerTheme(darkTheme = true, dynamicColor = true) {
        TimerMenu(defaultWorkoutType = WorkoutType.EMOM)
    }
}
