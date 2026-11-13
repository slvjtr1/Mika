package com.wangzan101.mika.task

import android.content.Context
import android.graphics.Bitmap
import android.view.Gravity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
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
import com.wangzan101.mika.cap.phone.display.PipInput
import com.wangzan101.mika.core.agent.TaskState
import com.wangzan101.mika.settings.FeatureFlags
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * Screen control while a task works.
 *
 * - "Control in screen": a window covers the whole phone, shows the agent's screen live and swallows every touch.
 * - "Drive my real screen" (experimental): the agent works on the real screen; a transparent window swallows the
 *   user's touches (the agent's own taps pass through) with a small bar at the top.
 *
 * Both have STOP (cancels the task) and Take control (pauses the agent and gives touch back; on the agent's own
 * screen the user's touches are passed to it). Leave control lets the agent carry on. Everything goes away when the
 * task ends or needs the user (a question, a takeover, a confirmation), so the phone is never left locked.
 */
class ScreenControl(private val context: Context, private val tasks: TaskCenterState) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var window: OverlayWindow? = null
    private var windowKind: Kind? = null
    private var windowOn: ScreenAccessibility? = null
    private var frame by mutableStateOf<Bitmap?>(null)
    private var note by mutableStateOf("")
    private var input by mutableStateOf<PipInput?>(null)
    /** The user pressed Take control and has not pressed Leave control yet. */
    private val userHasControl = MutableStateFlow(false)

    private enum class Kind { Covered, Real, UserOnCovered, UserOnReal }

    private class Wanted(val kind: Kind?, val frame: Bitmap?, val note: String, val service: ScreenAccessibility?, val input: PipInput?)

    fun start() {
        val flags = FeatureFlags.get(context)
        val modes = combine(flags.controlInScreenChanges(), flags.controlMainScreenChanges()) { a, b -> a to b }
        scope.launch {
            combine(modes, tasks.state, tasks.frames, ScreenAccessibility.instance, userHasControl) { (covered, real), state, shown, service, user ->
                val active = working(state) || (user && state is TaskState.Held)
                if (state is TaskState.Terminal || state is TaskState.Idle) userHasControl.value = false
                val kind = when {
                    !active -> null
                    // Only once the agent works on a screen (its display exists), not for a chat reply.
                    real && shown != null -> if (user) Kind.UserOnReal else Kind.Real
                    real -> null
                    covered && shown != null -> if (user) Kind.UserOnCovered else Kind.Covered
                    else -> null
                }
                Wanted(kind, shown, noteOf(state), service, tasks.pipInput.value)
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
        input = w.input
        val service = w.service
        val kind = w.kind
        if (kind == null || service == null) {
            close()
            return
        }
        if (window?.isOpen == true && windowOn === service && windowKind == kind) return
        close()
        val overlays = service.overlays()
        val opened = when (kind) {
            Kind.Covered, Kind.UserOnCovered -> overlays.open(OverlayPlacement(Gravity.TOP or Gravity.LEFT)) { Covered(kind == Kind.UserOnCovered) }
                ?.also {
                    // The agent taps its own display: keep swallowing the user's touches meanwhile.
                    it.keepBlockingDuringAgentGestures = true
                    it.setFullScreen(true)
                }
            // The agent taps the real screen: let its gestures through this window.
            Kind.Real -> overlays.open(OverlayPlacement(Gravity.TOP or Gravity.LEFT)) { RealLock() }?.also { it.setFullScreen(true) }
            Kind.UserOnReal -> overlays.open(OverlayPlacement(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, y = dp(56))) { LeaveBar() }
        } ?: return
        window = opened
        windowKind = kind
        windowOn = service
    }

    private fun dp(v: Int) = (v * context.resources.displayMetrics.density).toInt()

    /** Status bar height (covers the notch / camera cutout), in dp; the top controls sit below it. */
    private val topInset: Int by lazy {
        val r = context.resources
        val id = r.getIdentifier("status_bar_height", "dimen", "android")
        val px = if (id > 0) r.getDimensionPixelSize(id) else dp(32)
        (px / r.displayMetrics.density).toInt().coerceAtLeast(24)
    }

    private fun close() {
        window?.close()
        window = null
        windowKind = null
        windowOn = null
    }

    private fun takeControl() {
        userHasControl.value = true
        TaskService.takeControl(context)
    }

    private fun leaveControl() {
        userHasControl.value = false
        TaskService.resume(context)
    }

    private fun stop() {
        userHasControl.value = false
        TaskService.cancel(context)
    }

    /** The agent's own screen, full size. While the user has control their touches are passed to it. */
    @Composable
    private fun Covered(userControl: Boolean) {
        val scope = rememberCoroutineScope()
        Box(Modifier.fillMaxSize().background(Color.Black).testTag("screen-control"), contentAlignment = Alignment.Center) {
            frame?.let { f ->
                val target = input
                val touch = if (userControl && target != null) Modifier.pointerInput(target) { forward(target, scope) } else Modifier
                Image(
                    f.asImageBitmap(), contentDescription = null,
                    modifier = Modifier.aspectRatio(f.width.toFloat() / f.height.coerceAtLeast(1)).then(touch),
                )
            }
            Pill(
                stringResource(if (userControl) R.string.screen_control_you else R.string.screen_control_locked),
                Modifier.align(Alignment.TopCenter).padding(top = (topInset + 10).dp),
            )
            Column(
                Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(start = 20.dp, end = 20.dp, bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (note.isNotBlank() && !userControl) Pill(note, Modifier.padding(bottom = 8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (userControl) Button(stringResource(R.string.screen_control_leave), Color(0xFF2E7D32), "screen-control-leave", ::leaveControl)
                    else Button(stringResource(R.string.screen_control_take), Color(0xFF424242), "screen-control-take", ::takeControl)
                    Button(stringResource(R.string.screen_control_stop), Color(0xFFE53935), "screen-control-stop", ::stop)
                }
            }
        }
    }

    /** Real screen: transparent, swallows touches; a small bar at the very top stays out of most apps' way. */
    @Composable
    private fun RealLock() {
        Box(Modifier.fillMaxSize().background(Color(0x14000000)).pointerInput(Unit) { awaitEachGesture { awaitFirstDown().consume() } }) {
            Row(
                Modifier.align(Alignment.TopCenter).padding(top = (topInset + 8).dp).clip(RoundedCornerShape(16.dp)).background(Color(0xCC000000))
                    .padding(horizontal = 8.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically,
            ) {
                BasicText(stringResource(R.string.screen_control_locked_short), style = TextStyle(color = Color.White, fontSize = 11.sp))
                Small(stringResource(R.string.screen_control_take), Color(0xFF616161), "screen-control-take", ::takeControl)
                Small(stringResource(R.string.screen_control_stop), Color(0xFFE53935), "screen-control-stop", ::stop)
            }
        }
    }

    /** Real screen while the user has control: just the buttons, everything else works normally. */
    @Composable
    private fun LeaveBar() {
        Row(
            Modifier.clip(RoundedCornerShape(24.dp)).background(Color(0xE6000000)).padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(stringResource(R.string.screen_control_leave), Color(0xFF2E7D32), "screen-control-leave", ::leaveControl)
            Button(stringResource(R.string.screen_control_stop), Color(0xFFE53935), "screen-control-stop", ::stop)
        }
    }

    @Composable
    private fun Pill(text: String, modifier: Modifier = Modifier) {
        BasicText(
            text,
            modifier.clip(RoundedCornerShape(12.dp)).background(Color(0xB3000000)).padding(horizontal = 12.dp, vertical = 5.dp),
            style = TextStyle(color = Color.White, fontSize = 12.sp, textAlign = TextAlign.Center),
            maxLines = 1, overflow = TextOverflow.Ellipsis,
        )
    }

    @Composable
    private fun Button(text: String, color: Color, tag: String, onClick: () -> Unit) {
        BasicText(
            text,
            Modifier.clip(RoundedCornerShape(28.dp)).background(color).clickable(onClick = onClick)
                .padding(horizontal = 26.dp, vertical = 14.dp).testTag(tag),
            style = TextStyle(color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold),
        )
    }

    @Composable
    private fun Small(text: String, color: Color, tag: String, onClick: () -> Unit) {
        BasicText(
            text,
            Modifier.clip(RoundedCornerShape(10.dp)).background(color).clickable(onClick = onClick)
                .padding(horizontal = 10.dp, vertical = 4.dp).testTag(tag),
            style = TextStyle(color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold),
        )
    }
}

/** Taps and one-finger swipes on the shown frame, passed to the agent's screen (0..1 coordinates). */
private suspend fun androidx.compose.ui.input.pointer.PointerInputScope.forward(input: PipInput, scope: CoroutineScope) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        down.consume()
        val points = mutableListOf(down.position)
        var last = down.uptimeMillis
        var moved = false
        while (true) {
            val change = awaitPointerEvent().changes.firstOrNull { it.id == down.id } ?: break
            if (!change.pressed) break
            change.consume()
            points += change.position
            last = change.uptimeMillis
            if ((change.position - down.position).getDistance() > viewConfiguration.touchSlop) moved = true
        }
        val pts = points.map { p -> (p.x / size.width).coerceIn(0f, 1f) to (p.y / size.height).coerceIn(0f, 1f) }
        if (!moved) {
            val (fx, fy) = pts.last()
            scope.launch { input.tap(fx, fy) }
        } else {
            scope.launch { input.swipe(pts, (last - down.uptimeMillis).coerceIn(80L, 600L)) }
        }
    }
}
