package com.example.jetpackaudiorecorder

import AudioRecorder
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetpackaudiorecorder.ui.theme.JetpackAudioRecorderTheme
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val audioRecorder = AudioRecorder(this)
        val audioPlayer = AudioPlayer(this)
        val rootPath = obbDir
        val rawName = "rekaman"

        setContent {
            val isRecording = remember {
                mutableStateOf(false)
            }
            val isPlaying = remember {
                mutableStateOf(false)
            }
            val selectedFileName = remember {
                mutableStateOf<File?>(null)
            }
            val files = remember {
                mutableStateListOf<File>()
            }

            LaunchedEffect(true) {
                FileUtil
                    .updateFileList(rootPath, rawName)
                    .also {
                        files.clear()
                        files.addAll(it)
                    }
            }

            LaunchedEffect(key1 = selectedFileName.value) {
                audioPlayer.stop()
                isPlaying.value = false
            }

            JetpackAudioRecorderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        ) {
                            LazyColumn {
                                items(files) { item ->
                                    TextButton(
                                        onClick = {
                                            selectedFileName.value = item
                                        },
                                        colors = ButtonDefaults.textButtonColors(
                                            containerColor = when {
                                                selectedFileName.value?.name == item.name -> MaterialTheme.colorScheme.inversePrimary
                                                else -> Color.Unspecified
                                            }
                                        ),
                                        shape = RectangleShape
                                    ) {
                                        Text(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 24.dp),
                                            text = item.name
                                        )
                                    }
                                }
                            }
                        }
                        Button(
                            onClick = {
                                when {
                                    isRecording.value -> {
                                        audioRecorder.stop()
                                        FileUtil
                                            .updateFileList(rootPath, rawName)
                                            .also {
                                                files.clear()
                                                files.addAll(it)
                                            }
                                    }

                                    else -> {
                                        File(
                                            obbDir,
                                            FileUtil.getFileName(rootPath, rawName)
                                        ).also { audioRecorder.start(it) }
                                    }
                                }

                                isRecording.value = !isRecording.value
                            }
                        ) {
                            Text(
                                text = when {
                                    isRecording.value -> "Stop"
                                    else -> "Record"
                                }
                            )
                        }

                        Button(
                            onClick = {
                                when {
                                    isPlaying.value -> {
                                        audioPlayer.stop()
                                    }

                                    else -> {
                                        selectedFileName.value?.let {
                                            audioPlayer.playFile(it)
                                            audioPlayer.watch(
                                                onComplete = {
                                                    isPlaying.value = false
                                                }
                                            )
                                        }
                                    }
                                }

                                isPlaying.value = !isPlaying.value
                            },
                            enabled = selectedFileName.value != null
                        ) {
                            Text(
                                text = when {
                                    isPlaying.value -> "Stop"
                                    else -> "Play"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}