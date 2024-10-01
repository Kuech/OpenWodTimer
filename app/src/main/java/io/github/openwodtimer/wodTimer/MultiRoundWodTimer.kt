package io.github.openwodtimer.wodTimer

class MultiRoundWodTimer(
    preCountDownTime: Long = 10 * 1000,
    val restCountDownTime: Long,
    workCountDownTime: Long,
    var rounds: Int
) : WodTimer(preCountDownTime, workCountDownTime) {

    private var currentWorkoutStateIndex=0
    override val workoutStateSetMap = mapOf(
        WorkoutState.PRECOUNTDOWN to preCountDownTime,
        WorkoutState.WORK to workCountDownTime,
        WorkoutState.REST to restCountDownTime
    )

    override fun start(){
        setNewRound(wodTimerData.currentRound)
        startCountDownTimer(currentRemainingTime+1000)
    }

    override fun countDownFinished() {
        if(rounds==0){
            stop()
        }else{
            wodTimerData.currentWorkoutState = nextWorkoutState()
            start()
        }
    }

    private fun nextWorkoutState(): WorkoutState {
        val workoutStateList = listOf(WorkoutState.WORK, WorkoutState.REST)

        if(restCountDownTime==0L){
            nextRound()
            return WorkoutState.WORK
        }

        currentWorkoutStateIndex++
        val currentIndex = currentWorkoutStateIndex % workoutStateList.size

        if(currentIndex == 0){
            nextRound()
        }
        return workoutStateList[currentIndex]
    }

    private fun nextRound(){
        rounds--
        wodTimerData.currentRound++
    }
}

