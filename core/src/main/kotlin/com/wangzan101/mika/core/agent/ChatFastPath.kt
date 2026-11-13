package com.wangzan101.mika.core.agent

/**
 * Plain chat ("hi", "how are you", "what is a black hole") does not need the agent: no tools, no knowledge, no
 * skills index, no memory, no intent check, no learning afterwards. Such a message goes to the model with a few lines
 * of instructions and costs a few hundred tokens instead of the full agent setup.
 *
 * The local check ([candidate]) costs nothing and only rules out messages that obviously ask for an action; the model
 * makes the final call and answers [TASK_MARK] when the message needs the phone, a website or live data, in which case
 * the full agent runs as before.
 */
object ChatFastPath {
    const val TASK_MARK = "[[TASK]]"

    /** Longest message still tried as chat; longer ones are usually instructions. */
    private const val MAX_CHARS = 240

    val PROMPT = """
        You are Mika, a friendly assistant on the user's Android phone. This is chat mode: you have no tools here.
        Reply briefly and naturally, in the language the user writes in (English if unclear).
        If the message asks you to do anything on the phone, in an app or website, with files, or needs live or
        current information (open, send, message, call, search, check, look up, book, order, buy, set, remind,
        schedule, play, download, install, turn on/off, latest news, weather, prices), reply with exactly $TASK_MARK
        and nothing else.
    """.trimIndent()

    /** Words that almost always mean "go do something", in English, Arabic and Chinese. */
    private val ACTION = Regex(
        "\\b(open|launch|send|text|message|call|dial|search|google|look ?up|find|check|book|order|buy|pay|set|remind|" +
            "schedule|alarm|timer|play|download|install|upload|post|reply|forward|delete|turn (on|off)|enable|disable|" +
            "navigate|go to|screenshot|record|weather|news|price)\\b|" +
            "افتح|شغل|ارسل|أرسل|راسل|اتصل|كلم|ابحث|دور|احجز|اطلب|اشتر|ادفع|ذكرني|منبه|مؤقت|نزل|حمل|ثبت|انشر|رد على|احذف|" +
            "طقس|سعر|اخبار|أخبار|" +
            "打开|发送|发给|打电话|搜索|查一下|预订|下单|购买|设置|提醒|闹钟|播放|下载|安装|删除|天气",
        RegexOption.IGNORE_CASE,
    )
    private val LINK = Regex("https?://|www\\.", RegexOption.IGNORE_CASE)

    /** Worth trying as plain chat: short, no link, no obvious action word. */
    fun candidate(text: String): Boolean {
        val t = text.trim()
        if (t.isEmpty() || t.length > MAX_CHARS) return false
        if (LINK.containsMatchIn(t)) return false
        return !ACTION.containsMatchIn(t)
    }

    /** The model asked for the full agent. */
    fun wantsTask(reply: String): Boolean = reply.trim().startsWith(TASK_MARK)
}
