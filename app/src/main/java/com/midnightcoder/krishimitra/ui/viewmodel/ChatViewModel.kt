package com.midnightcoder.krishimitra.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.midnightcoder.krishimitra.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val chatRepository: ChatRepository) : ViewModel() {
    private val  _chatResponse= MutableStateFlow<ChatResponseState>(ChatResponseState.Empty)
    val chatResponse: StateFlow<ChatResponseState> = _chatResponse

    fun sendMessage(message:String){
        viewModelScope.launch {
            if (message.isBlank()) return@launch
            _chatResponse.value=ChatResponseState.Loading
            try {
                _chatResponse.value=ChatResponseState.Success(chatRepository.sendMessage(message))
            }catch (e: Exception){
                _chatResponse.value= ChatResponseState.Error(e.localizedMessage)
            }
        }
    }
}

sealed class ChatResponseState(){
    data class Success(val response:String):ChatResponseState()
    data class Error(val message:String):ChatResponseState()
    object Loading:ChatResponseState()
    object Empty:ChatResponseState()

}