package com.example.educationapp.presentation.screenmodel.parent

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.util.UiText
import com.example.educationapp.core.util.asUiText
import com.example.educationapp.domain.entity.UserProfile
import com.example.educationapp.domain.usecase.GetChildrenUseCase
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ParentChildrenState {
    object Loading : ParentChildrenState
    data class Success(val children: List<UserProfile.Student>) : ParentChildrenState
    data class Error(val error: UiText) : ParentChildrenState
}

class ParentMainScreenModel(
    private val getChildrenUseCase: GetChildrenUseCase,
    private val secureSettings: Settings
) : ScreenModel {

    companion object {
        private const val SELECTED_CHILD_ID_KEY = "parent_selected_child_id"
    }

    private val _childrenState = MutableStateFlow<ParentChildrenState>(ParentChildrenState.Loading)
    val childrenState: StateFlow<ParentChildrenState> = _childrenState.asStateFlow()

    private val _selectedChild = MutableStateFlow<UserProfile.Student?>(null)
    val selectedChild: StateFlow<UserProfile.Student?> = _selectedChild.asStateFlow()

    init {
        loadChildren()
    }

    fun loadChildren() {
        screenModelScope.launch {
            _childrenState.value = ParentChildrenState.Loading
            when (val result = getChildrenUseCase()) {
                is ApiResult.Error -> {
                    _childrenState.value = ParentChildrenState.Error(result.asUiText())
                }
                is ApiResult.Success -> {
                    val childrenList = result.data
                    _childrenState.value = ParentChildrenState.Success(childrenList)
                    
                    if (childrenList.isNotEmpty()) {
                        val savedId = secureSettings.getIntOrNull(SELECTED_CHILD_ID_KEY)
                        val matchedChild = childrenList.find { it.studentId == savedId }
                        if (matchedChild != null) {
                            _selectedChild.value = matchedChild
                        } else {
                            // Default to first child
                            selectChild(childrenList.first())
                        }
                    } else {
                        _selectedChild.value = null
                    }
                }
            }
        }
    }

    fun selectChild(child: UserProfile.Student) {
        _selectedChild.value = child
        secureSettings[SELECTED_CHILD_ID_KEY] = child.studentId
    }
}
