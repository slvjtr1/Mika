package com.wangzan101.mika.assist

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wangzan101.mika.R
import com.wangzan101.mika.task.TaskService
import java.util.UUID

/**
 * Opened by the system assistant gesture (long-press the power or side button, or the assist gesture) once Mika is
 * the phone's digital assistant app. A small panel over whatever is on screen: it starts listening right away with
 * the phone's own speech recognizer (on device, no tokens), or the user can type. What is said or typed becomes a
 * task in a new conversation and the panel closes; progress shows in the floating capsule, the notification, or the
 * full-screen control when that is on.
 */
class AssistActivity : ComponentActivity() {
    private var text by mutableStateOf("")
    private var listening by mutableStateOf(false)

    private val speech = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        listening = false
        val heard = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()?.trim().orEmpty()
        if (heard.isNotEmpty()) send(heard)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { Panel() }
        if (savedInstanceState == null) listen()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        listen()
    }

    private fun listen() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            .putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.assist_listening))
        if (intent.resolveActivity(packageManager) == null) return
        listening = true
        runCatching { speech.launch(intent) }.onFailure { listening = false }
    }

    private fun send(goal: String) {
        val t = goal.trim()
        if (t.isEmpty()) return
        TaskService.start(this, t, UUID.randomUUID().toString())
        finish()
    }

    @androidx.compose.runtime.Composable
    private fun Panel() {
        Box(Modifier.fillMaxSize().background(Color(0x66000000)).clickable { finish() }) {
            Column(
                Modifier.align(Alignment.BottomCenter).fillMaxWidth().imePadding().navigationBarsPadding().padding(12.dp)
                    .clip(RoundedCornerShape(26.dp)).background(Color(0xFF1C1C1E)).clickable(enabled = false) {}
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                BasicText(
                    stringResource(if (listening) R.string.assist_listening else R.string.assist_title),
                    style = TextStyle(color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        Modifier.weight(1f).clip(RoundedCornerShape(20.dp)).background(Color(0xFF2C2C2E))
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                    ) {
                        if (text.isEmpty()) BasicText(stringResource(R.string.assist_hint), style = TextStyle(color = Color(0xFF8E8E93), fontSize = 16.sp))
                        BasicTextField(
                            text, { text = it },
                            textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                            cursorBrush = SolidColor(Color.White),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(onSend = { send(text) }),
                            modifier = Modifier.fillMaxWidth().testTag("assist-input"),
                        )
                    }
                    Round(if (text.isBlank()) "🎤" else "➤", "assist-go") { if (text.isBlank()) listen() else send(text) }
                }
            }
        }
    }

    @androidx.compose.runtime.Composable
    private fun Round(mark: String, tag: String, onClick: () -> Unit) {
        Box(
            Modifier.size(48.dp).clip(CircleShape).background(Color(0xFF0A84FF)).clickable(onClick = onClick).testTag(tag),
            contentAlignment = Alignment.Center,
        ) { BasicText(mark, style = TextStyle(color = Color.White, fontSize = 20.sp)) }
    }
}
