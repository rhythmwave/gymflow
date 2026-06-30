package com.rhythmwave.gymflow.ui.program

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhythmwave.gymflow.data.local.dao.*
import com.rhythmwave.gymflow.data.local.entity.*
import com.rhythmwave.gymflow.domain.model.GoalProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class GoalInfo(
    val profile: GoalProfile,
    val priority: Int
)

data class ProgramUiState(
    val isLoading: Boolean = true,
    val program: ProgramEntity? = null,
    val days: List<ProgramDayEntity> = emptyList(),
    val goals: List<GoalInfo> = emptyList(),
    val todayIndex: Int = -1
)

@HiltViewModel
class ProgramViewModel @Inject constructor(
    private val programDao: ProgramDao,
    private val programDayDao: ProgramDayDao,
    private val goalDao: GoalDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgramUiState())
    val uiState: StateFlow<ProgramUiState> = _uiState.asStateFlow()

    init {
        loadProgram()
    }

    fun loadProgram() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val program = programDao.getActiveProgram().first()
            if (program == null) {
                _uiState.value = ProgramUiState(isLoading = false)
                return@launch
            }

            val days = programDayDao.getByProgram(program.id).first()
            val goals = goalDao.getActiveByProgram(program.id).first()

            val cal = Calendar.getInstance()
            val todayIndex = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7

            _uiState.value = ProgramUiState(
                isLoading = false,
                program = program,
                days = days,
                goals = goals.map { GoalInfo(it.profile, it.priority) },
                todayIndex = todayIndex
            )
        }
    }
}
