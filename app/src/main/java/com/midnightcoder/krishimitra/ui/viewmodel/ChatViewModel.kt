package com.midnightcoder.krishimitra.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.midnightcoder.krishimitra.domain.repository.ChatRepository
import com.midnightcoder.krishimitra.domain.repository.ImageDetectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val imageDetectionRepository: ImageDetectionRepository
) : ViewModel() {
    private val _chatResponse = MutableStateFlow<ChatResponseState>(ChatResponseState.Empty)
    val chatResponse: StateFlow<ChatResponseState> = _chatResponse

    private val _imageDetection = MutableStateFlow<ImageDetectionState<String>>(ImageDetectionState.Empty)
    val imageDetection: StateFlow<ImageDetectionState<String>> = _imageDetection

    fun sendMessage(message: String) {
        viewModelScope.launch {
            if (message.isBlank()) return@launch
            _chatResponse.value = ChatResponseState.Loading
            try {
                _chatResponse.value = ChatResponseState.Success(chatRepository.sendMessage(message))
            } catch (e: Exception) {
                _chatResponse.value = ChatResponseState.Error(e.localizedMessage)
            }
        }
    }

    fun detectImage(image: Bitmap) {

    }

}

sealed class ChatResponseState() {
    data class Success(val response: String) : ChatResponseState()
    data class Error(val message: String) : ChatResponseState()
    object Loading : ChatResponseState()
    object Empty : ChatResponseState()
}

sealed class ImageDetectionState<out T> {
    data class Success<T>(val response: T) : ImageDetectionState<T>()
    data class Error(val message: String) : ImageDetectionState<Nothing>()
    object Loading : ImageDetectionState<Nothing>()
    object Empty : ImageDetectionState<Nothing>()
}
