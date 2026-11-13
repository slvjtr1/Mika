package com.wangzan101.mika.task

import android.content.Context
import android.graphics.Bitmap
import android.view.Gravity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wangzan101.mika.R
import com.wangzan101.mika.cap.phone.a11y.OverlayPlacement
import com.wangzan101.mika.cap.phone.a11y.OverlayWindow
import com.wangzan101.mika.cap.phone.a11y.ScreenAccessibility
import com.wangzan101.mika.core.agent.TaskState
import com.wangzan101.mika.settings.FeatureFlags
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * "Control in screen": while a task is working, a window covers the whole phone screen, shows live what the agent is
 * doing, and swallows every touch. The only thing that responds is the Stop button at the bottom, which stops the task.
 * The window goes away as soon as the task ends or needs the user (a question, a takeover, a confirmation), so the
 * phone is never left locked. Drawn through the accessibility service; without it nothing is shown and the chat falls
 * back to its own full-size live view.
 */
class ScreenControl(private val context: Context, private val tasks: TaskCenterState) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var window: OverlayWindow? = null
    private var windowOn: ScreenAccessibility? = null
    private var frame by mutableStateOf<Bitmap?>(null)
    private var note by mutableStateOf("")

    private class Wanted(val show: Boolean, val frame: Bitmap?, val note: String, val service: ScreenAccessibility?)

    fun start() {
        val flags = FeatureFlags.get(context)
        scope.launch {
            combine(flags.controlInScreenChanges(), tasks.state, tasks.frames, ScreenAccessibility.instance) { on, state, shown, service ->
                Wanted(on && shown != null && working(state), shown, noteOf(state), service)
            }.collect { update(it) }
        }
    }

    /** Working on its own: running, or briefly held for something that does not need the user. */
    private fun working(state: TaskState): Boolean = when (state) {
        is TaskState.Running -> true
        is TaskState.Held -> !state.reason.needsYou
        else -> false
    }

    private fun noteOf(state: TaskState): String =
        (state as? TaskState.Running)?.let { it.narration ?: it.reason ?: it.note }.orEmpty().lineSequence().firstOrNull().orEmpty()

    private fun update(w: Wanted) {
        frame = w.frame ?: frame
        note = w.note
        val service = w.service
        if (!w.show || service == null) {
            close()
            return
        }
        if (window?.isOpen == true && windowOn === service) return
        close()
        val opened = service.overlays().open(OverlayPlacement(Gravity.TOP or Gravity.LEFT), touchable = true) { Content() } ?: return
        // The agent's own taps go to its own display: this window keeps blocking the user's touches while they run.
        opened.keepBlockingDuringAgentGestures = true
        opened.setFullScreen(true)
        window = opened
        windowOn = service
    }

    private fun close() {
        window?.close()
        window = null
        windowOn = null
    }

    @Composable
    private fun Content() {
        Box(Modifier.fillMaxSize().background(Color.Black).testTag("screen-control")) {
            frame?.let {
                Image(it.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
            }
            BasicText(
                stringResource(R.string.screen_control_locked),
                Modifier.align(Alignment.TopCenter).padding(top = 34.dp)
                    .clip(RoundedCornerShape(12.dp)).background(Color(0xB3000000)).padding(horizontal = 12.dp, vertical = 5.dp),
                style = TextStyle(color = Color.White, fontSize = 12.sp),
            )
            Column(
                Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(start = 24.dp, end = 24.dp, bottom = 44.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (note.isNotBlank()) {
                    BasicText(
                        note,
                        Modifier.padding(bottom = 8.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xB3000000))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        style = TextStyle(color = Color.White, fontSize = 12.sp, textAlign = TextAlign.Center),
                        maxLines = 1, overflow = TextOverflow.Ellipsis,
                    )
                }
                BasicText(
                    stringResource(R.string.screen_control_stop),
                    Modifier.clip(RoundedCornerShape(30.dp)).background(Color(0xFFE53935))
                        .clickable { TaskService.cancel(context) }
                        .padding(horizontal = 64.dp, vertical = 15.dp).testTag("screen-control-stop"),
                    style = TextStyle(color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold),
                )
            }
        }
    }
}
