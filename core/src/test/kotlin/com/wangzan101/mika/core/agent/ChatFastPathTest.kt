package com.wangzan101.mika.core.agent

import com.wangzan101.mika.core.assembly.toolDispatcher
import com.wangzan101.mika.core.testing.answerReply
import com.wangzan101.mika.core.tool.FakeScreen
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ChatFastPathTest {
    @Test fun greetingIsOneSmallRequest() = runTest {
        val model = FakeModel(answerReply("أهلاً! كيف أقدر أساعدك؟"))
        val agent = AgentLoop(model, toolDispatcher(FakeScreen()), "BASE", chatFastPath = true)
        val r = agent.run("هلا")
        assertTrue(r is TaskState.Succeeded)
        assertTrue(agent.answeredAsChat)
        assertEquals(1, model.requests.size)
        assertEquals(ChatFastPath.PROMPT, model.system)
        assertEquals(listOf<String>(), model.offeredTools.single())
    }

    @Test fun taskMarkFallsBackToTheAgent() = runTest {
        val model = FakeModel(answerReply(ChatFastPath.TASK_MARK), answerReply("done"))
        val agent = AgentLoop(model, toolDispatcher(FakeScreen()), "BASE", chatFastPath = true)
        agent.run("how is the weather in my city")
        assertTrue(!agent.answeredAsChat)
    }

    @Test fun commands() {
        listOf("open youtube", "افتح يوتيوب", "please install whatsapp", "打开微信").forEach { assertTrue(it, ChatFastPath.startsWithCommand(it)) }
        listOf("settings are weird", "how can i install YouTube", "سولف معي", "hi").forEach { assertTrue(it, !ChatFastPath.startsWithCommand(it)) }
    }

    @Test fun candidates() {
        listOf("هلا", "hi", "مرحبا كيف حالك", "what is a black hole").forEach { assertTrue(it, ChatFastPath.candidate(it)) }
        listOf("افتح يوتيوب", "open youtube", "https://x.com").forEach { assertTrue(it, !ChatFastPath.candidate(it)) }
    }
}
