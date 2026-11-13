package com.wangzan101.mika.voice

import kotlin.math.abs

/**
 * Cuts silence out of a recording before it is sent to a model for transcription. Audio is billed by length, so
 * the pause before speaking, the pause after, and long gaps in between are paid for without carrying any words.
 * Leading and trailing silence goes (keeping a short margin) and every gap longer than [MAX_GAP_MS] is shortened to
 * [MAX_GAP_MS]. Works on 16-bit mono PCM in 20 ms frames; a frame is speech when its mean level passes a threshold
 * set from the quietest part of this recording, so a noisy room does not count as speech everywhere.
 */
object SilenceTrim {
    private const val FRAME_MS = 20
    private const val MARGIN_MS = 200
    private const val MAX_GAP_MS = 400
    /** Floor for the speech threshold (out of 32767). */
    private const val MIN_THRESHOLD = 350

    fun trim(pcm: ShortArray, rate: Int = Wav.RATE): ShortArray {
        val frame = rate * FRAME_MS / 1000
        if (frame <= 0 || pcm.size < frame * 5) return pcm
        val count = pcm.size / frame
        val levels = IntArray(count) { f ->
            var sum = 0L
            for (i in f * frame until (f + 1) * frame) sum += abs(pcm[i].toInt())
            (sum / frame).toInt()
        }
        val sorted = levels.sorted()
        val noise = sorted[count / 10]
        val threshold = maxOf(MIN_THRESHOLD, noise * 3)
        val speech = BooleanArray(count) { levels[it] > threshold }
        val first = speech.indexOfFirst { it }
        if (first < 0) return pcm
        val last = speech.indexOfLast { it }
        val margin = MARGIN_MS / FRAME_MS
        val maxGap = MAX_GAP_MS / FRAME_MS
        val from = (first - margin).coerceAtLeast(0)
        val to = (last + margin).coerceAtMost(count - 1)
        val keep = ArrayList<Int>(to - from + 1)
        var gap = 0
        for (f in from..to) {
            if (speech[f]) gap = 0 else gap++
            if (gap <= maxGap) keep += f
        }
        if (keep.size >= count) return pcm
        val out = ShortArray(keep.size * frame)
        keep.forEachIndexed { i, f -> System.arraycopy(pcm, f * frame, out, i * frame, frame) }
        return out
    }
}
