package com.wangzan101.mika.core.agent

import com.wangzan101.mika.core.assembly.toolDispatcher
import com.wangzan101.mika.core.model.ModelError
import com.wangzan101.mika.core.model.ModelHttp
import com.wangzan101.mika.core.testing.answerReply
import com.wangzan101.mika.core.tool.FakeScreen
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LeanModeTest {
    private val groq = """{"error":{"message":"Request too large for model `openai/gpt-oss-20b` in organization `org_x` service tier `on_demand` on tokens per minute (TPM): Limit 8000, Requested 12931, please reduce your message size and try again.","type":"tokens","code":"rate_limit_exceeded"}}"""

    @Test fun groqTooLargeIsRecognised() {
        val e = ModelHttp.openAiError(413, null, groq, "openai/gpt-oss-20b")
        assertTrue(e is ModelError.RequestTooLarge)
        assertEquals(8000, (e as ModelError.RequestTooLarge).limit)
        assertEquals(12931, e.requested)
    }

    @Test fun tooLargeRestartsLeanAndRemembersTheLimit() = runTest {
        val model = FakeModel(ModelError.RequestTooLarge(8000, 12931), answerReply("done"))
        var learned: Int? = null
        val agent = AgentLoop(model, toolDispatcher(FakeScreen()), "BASE", onRequestLimit = { learned = it })
        val r = agent.run("open youtube")
        assertTrue(r.toString(), r is TaskState.Succeeded)
        assertEquals(8000, learned)
        assertEquals(LeanMode.PROMPT, model.system)
        assertTrue(model.offeredTools.last().all { it.length < 40 })
    }

    @Test fun leanPromptAndToolsAreSmall() = runTest {
        val model = FakeModel(answerReply("done"))
        AgentLoop(model, toolDispatcher(FakeScreen()), "BASE", requestLimit = 8000).run("open youtube")
        assertEquals(LeanMode.PROMPT, model.system)
    }
}
