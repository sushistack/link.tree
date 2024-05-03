package com.sushistack.linktree.config.p6spy

import com.p6spy.engine.common.P6Util
import com.p6spy.engine.spy.P6SpyOptions
import com.p6spy.engine.spy.appender.MessageFormattingStrategy
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class CustomSqlFormat: MessageFormattingStrategy {
    companion object {
        private const val CONNECTION_ID: String = "%(connectionId)"
        private const val CURRENT_TIME: String = "%(currentTime)"
        private const val EXECUTION_TIME: String = "%(executionTime)"
        private const val CATEGORY: String = "%(category)"
        private const val EFFECTIVE_SQL: String = "%(effectiveSql)"
        private const val EFFECTIVE_SQL_SINGLELINE: String = "%(effectiveSqlSingleLine)"
        private const val SQL: String = "%(sql)"
        private const val SQL_SINGLE_LINE: String = "%(sqlSingleLine)"
        private const val URL: String = "%(url)"
        private const val ALLOW_FILTER = "com.sushistack.linktree"
    }

    override fun formatMessage(connectionId: Int, now: String, elapsed: Long, category: String, prepared: String, sql: String, url: String): String {
        val customLogMessageFormat = P6SpyOptions.getActiveInstance().customLogMessageFormat

        return (customLogMessageFormat
            ?.replace(Pattern.quote(CONNECTION_ID).toRegex(), connectionId.toString())
            ?.replace(Pattern.quote(CURRENT_TIME).toRegex(), now)
            ?.replace(Pattern.quote(EXECUTION_TIME).toRegex(), elapsed.toString())
            ?.replace(Pattern.quote(CATEGORY).toRegex(), category)?.replace(Pattern.quote(EFFECTIVE_SQL).toRegex(), "\n" + Matcher.quoteReplacement(prepared))
            ?.replace(Pattern.quote(EFFECTIVE_SQL_SINGLELINE).toRegex(), Matcher.quoteReplacement(P6Util.singleLine(prepared)))
            ?.replace(Pattern.quote(SQL).toRegex(), "\n" + Matcher.quoteReplacement(sql))
            ?.replace(Pattern.quote(SQL_SINGLE_LINE).toRegex(), Matcher.quoteReplacement(P6Util.singleLine(sql)))
            ?.replace(Pattern.quote(URL).toRegex(), url)
            ?: (now + "|" + elapsed + "|" + category + "|connection " + connectionId + "|url " + url + "|\n" + P6Util.singleLine(sql)))
    }

    private fun createStack(connectionId: Int, elapsed: Long): String {
        val callStack: Stack<String> = Stack()
        val stackTrace = Throwable().stackTrace

        for (stackTraceElement in stackTrace) {
            val trace = stackTraceElement.toString()

            if (trace.startsWith(ALLOW_FILTER)) {
                callStack.push(trace)
            }
        }

        val sb = StringBuffer()
        var order = 1
        while (!callStack.empty()) {
            sb.append("\n\t\t" + (order++) + "." + callStack.pop())
        }

        return StringBuffer().append("\n\n\tConnection ID:").append(connectionId)
            .append(" | Excution Time:").append(elapsed).append(" ms\n")
            .append("\tExcution Time:").append(elapsed).append(" ms\n")
            .append("\tCall Stack :").append(sb).append("\n")
            .append("\n--------------------------------------")
            .toString()
    }
}