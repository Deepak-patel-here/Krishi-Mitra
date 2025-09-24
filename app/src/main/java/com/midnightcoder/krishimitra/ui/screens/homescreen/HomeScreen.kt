package com.midnightcoder.krishimitra.ui.screens.homescreen

import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.midnightcoder.krishimitra.ui.viewmodel.ChatResponseState
import com.midnightcoder.krishimitra.ui.viewmodel.ChatViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.midnightcoder.krishimitra.R
import com.midnightcoder.krishimitra.domain.usecase.ConvertToBitmap

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalAnimationApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun HomeScreen(
    onImageSelected: () -> Unit,
    modifier: Modifier = Modifier,
    chatViewModel: ChatViewModel = hiltViewModel()
) {
    val colorScheme = MaterialTheme.colorScheme
    var message by rememberSaveable { mutableStateOf("") }
    var hasStartedChat by rememberSaveable { mutableStateOf(false) }
    val messages = rememberSaveable { mutableStateListOf<ChatMessage>() }
    val coroutineScope = rememberCoroutineScope()
    val keyboard = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by rememberSaveable { mutableStateOf(false) }
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val image= ConvertToBitmap.toBitMap(it,context)
                messages.add(ChatMessage("", true, image))
                if(!hasStartedChat)hasStartedChat=true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        showSheet=false

    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) {success ->
        if (success) {
            imageUri?.let {
                val image = ConvertToBitmap.toBitMap(it, context)
                messages.add(ChatMessage("", true, image))
                if(!hasStartedChat)hasStartedChat=true

            }
        }
        showSheet = false

    }


    val chatResponse = chatViewModel.chatResponse.collectAsStateWithLifecycle()

    LaunchedEffect(chatResponse.value) {
        when (val state = chatResponse.value) {
            is ChatResponseState.Loading -> {
                // Show typing indicator
                if (messages.lastOrNull()?.text != "typing...") {
                    messages.add(ChatMessage("typing...", false))
                }
            }

            is ChatResponseState.Success -> {
                // Remove typing indicator
                if (messages.lastOrNull()?.text == "typing...") {
                    messages.removeLast()
                }
                // Add AI response
                messages.add(ChatMessage(state.response, false))
            }

            is ChatResponseState.Error -> {
                if (messages.lastOrNull()?.text == "typing...") {
                    messages.removeLast()
                }
                messages.add(ChatMessage("⚠️ Error: ${state.message}", false))
            }

            else -> Unit
        }
    }


    //------------------------------Speech-to-text---------------------------------//
    var showMicDialog by rememberSaveable { mutableStateOf(false) }
    var rms by rememberSaveable { mutableStateOf(0f) }
    val startListening = rememberSpeechRecognizer(
        onResult = { text ->
            hasStartedChat = true
            showMicDialog = false
            messages.add(ChatMessage(text, true))
            chatViewModel.sendMessage(text)
        },
        onError = { err ->
            showMicDialog = false
            messages.add(ChatMessage("❌ $err", false))
        },
        onRmsChanged = { value ->
            rms = value
        }
    )


    val features = listOf(
        R.drawable.ai_guidance to { messages.add(ChatMessage("Opening crop guidance...", false)) },
        R.drawable.upload_crop to { messages.add(ChatMessage("Open gallery/camera...", false)) },
        R.drawable.record to { messages.add(ChatMessage("Voice input ready...", false)) },
        R.drawable.ai_suggestion to {
            messages.add(
                ChatMessage(
                    "Fetching AI suggestions...",
                    false
                )
            )
        }
    )

    val pagerState = rememberPagerState(pageCount = { features.size })
    LaunchedEffect(Unit) {
        while (true) {
            delay(3500)
            val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
            pagerState.animateScrollToPage(nextPage)
        }
    }

    // Animate FAB offset depending on chat box position
    val fabBottomPadding by animateDpAsState(targetValue = if (hasStartedChat) 140.dp else 24.dp)
    val isBottomAligned = hasStartedChat && messages.isNotEmpty()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showMicDialog = true
                    startListening()
                },
                containerColor = colorScheme.primary,
                modifier = Modifier.padding(bottom = fabBottomPadding)
            ) {
                Icon(Icons.Default.Mic, contentDescription = "Voice Input", tint = Color.White)
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        containerColor = colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                //----------------------------------------------Welcome + Carousel-----------------------------------------//
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Welcome to Krishi Mitra",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onBackground
                            )
                            Text(
                                text = "Get instant advice for your crops",
                                fontSize = 14.sp,
                                color = colorScheme.onBackground.copy(alpha = 0.7f),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                        Card(modifier = Modifier.size(50.dp), shape = RoundedCornerShape(10.dp)) {

                            IconButton(onClick = { /* TODO: Open profile */ }) {
                                Icon(
                                    imageVector = Icons.Default.Person, // You can replace with profile icon
                                    contentDescription = "Profile",
                                    tint = colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }



                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {

                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                        ) { page ->
                            Card(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .fillMaxWidth()
                                    .clickable {
                                        features[page].second.invoke()
                                        hasStartedChat = true
                                    },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = colorScheme.secondaryContainer),
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(features[page].first),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }
                }

                //-----------------------------------------------------Chat messages----------------------------------------------------//
                if (hasStartedChat) {
                    Spacer(modifier = Modifier.height(20.dp))
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(bottom = 120.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        reverseLayout = true
                    ) {
                        items(messages.reversed()) { msg ->
                            if (msg.text == "typing...") {
                                TypingIndicator()
                            } else {
                                ChatBubbleThemeAdaptive(message = msg, bitmap = msg.image)
                            }

                        }
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            //-------------------------------------Animated Chat Input---------------------------------------//
            AnimatedContent(
                targetState = isBottomAligned,
                modifier = Modifier.fillMaxSize(),
                label = "chatBoxTransition"
            ) { started ->
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .align(if (started) Alignment.BottomCenter else Alignment.Center)
                            .padding(16.dp)
                            .fillMaxWidth(0.95f)
                    ) {
                        Text(
                            text = "Ask about your crops...",
                            fontSize = 16.sp,
                            color = colorScheme.onBackground.copy(alpha = 0.6f),
                            modifier = Modifier
                                .padding(bottom = 6.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(6.dp))

                        Surface(
                            shape = RoundedCornerShape(28.dp),
                            tonalElevation = 4.dp,
                            shadowElevation = 8.dp,
                            color = colorScheme.surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                IconButton(onClick = {
                                    showSheet=true
                                }) {
                                    Icon(
                                        Icons.Default.Photo,
                                        contentDescription = "Gallery",
                                        tint = colorScheme.primary
                                    )
                                }

                                OutlinedTextField(
                                    value = message,
                                    onValueChange = {
                                        message = it
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 6.dp),
                                    placeholder = {
                                        Text(
                                            "Type your message...",
                                            color = colorScheme.onSurface.copy(alpha = 0.5f)
                                        )
                                    },
                                    shape = RoundedCornerShape(20.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color.Transparent,
                                        unfocusedBorderColor = Color.Transparent,
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        cursorColor = colorScheme.primary,
                                    ),
                                    maxLines = 3
                                )

                                IconButton(onClick = {
                                    if (message.isNotBlank()) {
                                        coroutineScope.launch {
                                            messages.add(ChatMessage(message.trim(), true))
                                            chatViewModel.sendMessage(message.trim())
                                            message = ""
                                        }

                                    }
                                    if (!hasStartedChat) hasStartedChat = true
                                    keyboard?.hide()
                                }) {
                                    Icon(
                                        Icons.Default.Send,
                                        contentDescription = "Send",
                                        tint = colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
            if (showMicDialog) {
                VoiceInputDialog(
                    onDismiss = { showMicDialog = false },
                    rmsValue = rms
                )
            }
        }
        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Upload Image", fontSize = 18.sp, fontWeight = FontWeight.Bold)

                    Divider()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                imageLauncher.launch("image/*")
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Photo, contentDescription = "Gallery")
                        Spacer(Modifier.width(12.dp))
                        Text("Choose from Gallery")
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val uri = createImageUri(context)
                                imageUri = uri
                                cameraLauncher.launch(uri!!)
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Camera")
                        Spacer(Modifier.width(12.dp))
                        Text("Take a Photo")
                    }
                }
            }


        }

    }
}

fun createImageUri(context: Context): Uri? {
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "captured_${System.currentTimeMillis()}.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    }
    return context.contentResolver.insert(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    )
}


