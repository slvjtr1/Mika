package com.wangzan101.mika.core.agent

import com.wangzan101.mika.core.net.ProxyFailure
import com.wangzan101.mika.core.guard.GuardVerdict
import com.wangzan101.mika.core.model.ModelError
import com.wangzan101.mika.core.tool.Outcome
import kotlinx.serialization.json.JsonPrimitive

/**
 * 停止规则:任务什么时候、以什么结局结束(放弃、守卫叫停、模型出错、没给出回答、步数用完)。办完不在这里:
 * 不带工具调用的回复就是回答,由循环核对后交出。纯函数;结局里给用户看的说明不发给模型。
 */
internal object StopRules {
    /** 一个动作执行完之后:放弃了就按它结束;否则守卫([stop],屏幕守卫或不动屏幕的工具的守卫)叫停了就停。都没有为 null,接着做。 */
    fun afterAction(outcome: Outcome?, stop: GuardVerdict?): TaskState? = when (outcome) {
        is Outcome.Failure -> TaskState.Failed(outcome.message, FailKind.GaveUp)
        else -> stop?.let(::guard)
    }

    /** 步数用完后的最后一轮调了工具(只给了 end_not_done):放弃按它结束,否则算步数用完。 */
    fun afterFinalTurn(outcome: Outcome?, maxSteps: Int): TaskState = when (outcome) {
        is Outcome.Failure -> TaskState.Failed(outcome.message, FailKind.GaveUp)
        else -> stepLimit(maxSteps)
    }

    fun stepLimit(maxSteps: Int) = TaskState.Failed("Reached the $maxSteps-step limit; the task was not completed.", FailKind.StepLimit)

    /** 收尾请求本身失败或仍没交出结果时，按触发收尾的原因落结局，不谎报步数用完。 */
    fun closeoutFailed(reason: Closeout, maxSteps: Int): TaskState.Failed = when (reason) {
        Closeout.StepLimit -> stepLimit(maxSteps)
        Closeout.Completed -> TaskState.Failed("The task progress is complete, but the model did not give a result.", FailKind.Model)
        Closeout.NoNewData -> TaskState.Failed("No new data was read ${NewDataTracker.LIMIT} times in a row, so the task stopped.", FailKind.Guard)
    }

    /** 提醒过一次,模型仍然回了空话(或又被输出上限截断)。 */
    fun noAnswer() = TaskState.Failed("The model stopped working without giving an answer.", FailKind.Model)

    /** 退回过一次,模型的工具调用又以文字形式回来了(见 [LeakedToolCall]):不把原样的标记当回答交出。 */
    fun toolCallAsText() = TaskState.Failed("The server did not recognize the model's tool call; it came back as plain text and could not be run.", FailKind.Model)

    /** 模型调用失败(重试过仍不行,或重试也没用)。 */
    fun modelFailed(e: Throwable): TaskState.Failed = when (e) {
        is ModelError.TaskBudgetExceeded -> TaskState.Failed(userReason(e), FailKind.TokenLimit)
        is ModelError.DailyBudgetExceeded -> TaskState.Failed(userReason(e), FailKind.TokenLimit)
        is ModelError.ProactiveBudgetExceeded -> TaskState.Failed(userReason(e), FailKind.TokenLimit)
        is ModelError.RateLimited -> TaskState.Failed(userReason(e), FailKind.Model, FailureCode.ModelRateLimited)
        is ModelError -> TaskState.Failed(userReason(e), FailKind.Model)
        else -> TaskState.Failed("Model call failed: ${e.message ?: e::class.simpleName}", FailKind.Model)
    }

    fun guard(v: GuardVerdict): TaskState.Failed {
        val treeBlind = v.evidence["treeBlind"] == JsonPrimitive(true)
        val why = when (v.code) {
            "repeating_action" -> "the same action was repeated without any effect"
            "no_progress" -> if (treeBlind) "the element tree could not locate the target and the screen did not visibly change after repeated actions, so retries were stopped"
                else "the screen did not change after several actions"
            "read_loop" -> "the page did not change and repeated reads did not find the content"
            "too_many_failures" -> "several actions failed in a row"
            "sandbox_loop" -> "the same command kept failing when repeated"
            else -> "an operation safeguard was triggered"
        }
        return TaskState.Failed("Stopped safely (${v.code}): $why.", FailKind.Guard)
    }
}

internal fun userReason(e: ModelError): String = when (e) {
    is ModelError.Auth -> "The model service rejected the request: the API key is invalid or lacks permission. Check in Settings that the API key is correct and allowed to use this model."
    is ModelError.QuotaExceeded -> "The model account has run out of quota. Check your provider account balance or plan."
    is ModelError.TaskBudgetExceeded -> "Reached this task's budget of ${e.limit} tokens; the task was not completed."
    is ModelError.DailyBudgetExceeded ->
        "Provider \"${e.providerName}\" reached today's budget of ${e.limit} tokens; the task was not completed. It resets automatically when the local date changes."
    // 主动能力的调用不在任务里发,正常不会走到这里;万一走到,只如实说明,不提示用户去改什么
    is ModelError.ProactiveBudgetExceeded -> "Proactive features reached today's budget. It resets automatically when the local date changes."
    is ModelError.ModelNotFound -> {
        // 404 不一定是模型名不对(地址不对,或网关、中转服务说的别的情况):有原话时照原话说,不断定模型不存在
        val check = if (e.model.isEmpty()) "Check the model name and Base URL in Settings › Model."
            else "Go to Settings › Model and check the model name (use the full model ID, for example gemini-flash-latest for Gemini) or switch to the matching preset."
        when {
            e.detail.isNotEmpty() ->
                (if (e.model.isEmpty()) "" else "Calling model \"${e.model}\": ") +
                    "The server replied: ${quoted(e.detail)}. The model name may be wrong, or the service may be temporarily unavailable. $check"
            e.model.isEmpty() -> "Model not found. $check"
            else -> "Model \"${e.model}\" does not exist at this provider. $check"
        }
    }
    is ModelError.ModelNotAvailable ->
        "This account has not enabled or cannot use model \"${e.model.ifEmpty { "current model" }}\". Pick another model in Settings › Model, or enable it in your provider dashboard."
    is ModelError.ToolsUnsupported ->
        "Model \"${e.model.ifEmpty { "current model" }}\" does not support tool calls, and Mika needs tools to work. Pick a model with tool calling in Settings › Model."
    is ModelError.RequestTooLarge ->
        "The model service only accepts requests up to ${e.limit ?: "a small number of"} tokens (often a free-tier limit per minute), and this step needs more even in lean mode. Use a model or plan with a higher limit (for example a free Gemini API key), or split the task."
    is ModelError.ContextTooLong -> "The conversation is longer than the model's context window. Use a model with a longer context, or split the task into smaller parts."
    is ModelError.ContentRefused -> "The model refused this content. Rephrase the task or use another model."
    is ModelError.ReasoningEffortRejected ->
        "The model does not accept the selected reasoning effort. The server replied: ${quoted(e.detail)}. In Settings › Model, set reasoning effort to Default or a level the server supports."
    is ModelError.RateLimited -> "The model service is rate limiting requests, even after retries. Try again later, or check your account's rate limits."
    is ModelError.Network -> "Could not connect to the model service, even after retries. Check your network and the Base URL."
    is ModelError.Proxy -> when (e.failure) {
        ProxyFailure.Unreachable ->
            "Could not reach the network proxy, or the proxy did not forward the request, even after retries. Check that the proxy is running and that the address and port in Settings › Network proxy are correct; clear it if you don't need a proxy."
        ProxyFailure.AuthFailed -> "The network proxy rejected the username or password (HTTP 407). Check them in Settings › Network proxy."
    }
    is ModelError.Server -> "The model service returned an error (HTTP ${e.status}). Try again later."
    is ModelError.StreamCut ->
        "The connection was closed upstream before the model's reply finished, even after retries. This is usually an unstable network or a relay/gateway idle timeout that is too short (common when the model thinks for a long time). Try again later."
    is ModelError.BadResponse -> "${e.message}. Check that the Base URL and API type match."
    is ModelError.ChatGptLimitReached ->
        "Your ChatGPT allowance is used up; try again later. See usage at chatgpt.com/settings/usage. The Plus 5-hour allowance is shared with other apps that sign in with ChatGPT. You can also switch to another model."
    is ModelError.ChatGptNotEligible ->
        "This account can't use its ChatGPT allowance in other apps (it needs Plus or Pro, and your region and account settings must allow it). ${if (e.detail.isEmpty()) "" else "Server said: ${quoted(e.detail)}. "}You can switch to another model."
    is ModelError.SignInExpired -> "Your ${e.account} sign-in has expired. Sign in with ${e.account} again in Settings › Model."
    is ModelError.AccountPlanNotAllowed ->
        "Your ${e.account} subscription tier can't use this API (HTTP 403). ${if (e.detail.isEmpty()) "" else "Server said: ${quoted(e.detail)}. "}Use a ${e.account} API key instead, or pick another model in Settings › Model."
}

/** 引在句中的服务端原话:去掉末尾一个句号(原话自带的),由引它的句子补上自己的「。」;省略号(截短的「…」、「...」)不动。 */
private fun quoted(detail: String): String =
    when {
        detail.endsWith("..") -> detail
        detail.endsWith(".") || detail.endsWith("。") -> detail.dropLast(1)
        else -> detail
    }
