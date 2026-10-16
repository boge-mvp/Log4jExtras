package org.apache.logging.filter

import org.apache.logging.log4j.core.Filter
import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.impl.Log4jLogEvent
import org.apache.logging.log4j.core.util.KeyValuePair
import org.apache.logging.log4j.util.SortedArrayStringMap
import org.junit.jupiter.api.Test

class ContextMapRegexFilterTest {

    @Test
    fun testRegexFilterMatchesPattern() {
        // 创建包含正则表达式模式的键值对
        val pairs = arrayOf(KeyValuePair("userId", "[0-9]+")) // 匹配数字
        
        // 创建过滤器 - 当匹配时返回 NEUTRAL，不匹配时返回 DENY
        val filter = ContextMapRegexFilter.createFilter(pairs, null, Filter.Result.NEUTRAL, Filter.Result.DENY)
        
        // 创建一个带有匹配值的上下文数据
        val contextData = SortedArrayStringMap()
        contextData.putValue("userId", "12345") // 符合数字模式
        
        // 使用日志事件进行测试
        val logEvent = Log4jLogEvent.Builder()
            .setContextData(contextData)
            .build()
        
        val result = filter?.filter(logEvent)
        println("Should return NEUTRAL when pattern matches: $result")
    }

    @Test
    fun testRegexFilterDoesNotMatchPattern() {
        // 创建包含正则表达式模式的键值对
        val pairs = arrayOf(KeyValuePair("userId", "[0-9]+")) // 匹配数字
        
        // 创建过滤器
        val filter = ContextMapRegexFilter.createFilter(pairs, null, Filter.Result.NEUTRAL, Filter.Result.DENY)
        
        // 创建一个带有不匹配值的上下文数据
        val contextData = SortedArrayStringMap()
        contextData.putValue("userId", "abcde") // 不符合数字模式
        
        // 使用日志事件进行测试
        val logEvent = Log4jLogEvent.Builder()
            .setContextData(contextData)
            .build()
        
        val result = filter?.filter(logEvent)
        println("Should return DENY when pattern does not match: $result")
    }

    @Test
    fun testRegexFilterWithEmailPattern() {
        val pairs = arrayOf(KeyValuePair("email", "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
        
        val filter = ContextMapRegexFilter.createFilter(pairs, null, Filter.Result.NEUTRAL, Filter.Result.DENY)
        
        // 测试有效的电子邮件
        val validEmailContext = SortedArrayStringMap()
        validEmailContext.putValue("email", "test@example.com")
        
        val validLogEvent = Log4jLogEvent.Builder()
            .setContextData(validEmailContext)
            .build()
        
        val result1 = filter?.filter(validLogEvent)
        println("Should match valid email pattern: $result1")
        
        // 测试无效的电子邮件
        val invalidEmailContext = SortedArrayStringMap()
        invalidEmailContext.putValue("email", "invalid-email")
        
        val invalidLogEvent = Log4jLogEvent.Builder()
            .setContextData(invalidEmailContext)
            .build()
        
        val result2 = filter?.filter(invalidLogEvent)
        println("Should not match invalid email pattern: $result2")
    }

    @Test
    fun testRegexFilterWithMultiplePatternsOrOperator() {
        val pairs = arrayOf(
            KeyValuePair("userId", "[0-9]+"),
            KeyValuePair("username", "[a-zA-Z]+")
        )
        
        // 使用 OR 操作符 ("or")
        val filter = ContextMapRegexFilter.createFilter(pairs, "or", Filter.Result.NEUTRAL, Filter.Result.DENY)
        
        // 测试第一个模式匹配
        val context1 = SortedArrayStringMap()
        context1.putValue("userId", "12345")
        context1.putValue("username", "invalid123") // 不匹配字母模式
        
        val logEvent1 = Log4jLogEvent.Builder()
            .setContextData(context1)
            .build()
        
        val result1 = filter?.filter(logEvent1)
        println("Should match because userId matches: $result1")
        
        // 测试第二个模式匹配
        val context2 = SortedArrayStringMap()
        context2.putValue("userId", "abcde") // 不匹配数字模式
        context2.putValue("username", "validuser") // 匹配字母模式
        
        val logEvent2 = Log4jLogEvent.Builder()
            .setContextData(context2)
            .build()
        
        val result2 = filter?.filter(logEvent2)
        println("Should match because username matches: $result2")
        
        // 测试都不匹配
        val context3 = SortedArrayStringMap()
        context3.putValue("userId", "abcde") // 不匹配数字模式
        context3.putValue("username", "123456") // 不匹配字母模式
        
        val logEvent3 = Log4jLogEvent.Builder()
            .setContextData(context3)
            .build()
        
        val result3 = filter?.filter(logEvent3)
        println("Should not match because neither pattern matches: $result3")
    }

    @Test
    fun testRegexFilterWithMultiplePatternsAndOperator() {
        val pairs = arrayOf(
            KeyValuePair("userId", "[0-9]+"),
            KeyValuePair("username", "[a-zA-Z]+")
        )
        
        // 使用 AND 操作符 (默认)
        val filter = ContextMapRegexFilter.createFilter(pairs, null, Filter.Result.NEUTRAL, Filter.Result.DENY)
        
        // 测试两个模式都匹配
        val context1 = SortedArrayStringMap()
        context1.putValue("userId", "12345")
        context1.putValue("username", "validuser")
        
        val logEvent1 = Log4jLogEvent.Builder()
            .setContextData(context1)
            .build()
        
        val result1 = filter?.filter(logEvent1)
        println("Should match because both patterns match: $result1")
        
        // 测试只有一个模式匹配
        val context2 = SortedArrayStringMap()
        context2.putValue("userId", "12345")
        context2.putValue("username", "invalid123") // 不匹配字母模式
        
        val logEvent2 = Log4jLogEvent.Builder()
            .setContextData(context2)
            .build()
        
        val result2 = filter?.filter(logEvent2)
        println("Should not match because username doesn't match: $result2")
    }

    @Test
    fun testFilterWithLogEvent() {
        val pairs = arrayOf(KeyValuePair("userId", "[0-9]+"))
        
        val filter = ContextMapRegexFilter.createFilter(pairs, null, Filter.Result.NEUTRAL, Filter.Result.DENY)
        
        // 创建带有上下文数据的日志事件
        val contextData = SortedArrayStringMap()
        contextData.putValue("userId", "12345")
        
        val logEvent = Log4jLogEvent.Builder()
            .setContextData(contextData)
            .build()
        
        val result = filter?.filter(logEvent)
        println("Should return NEUTRAL when pattern matches: $result")
    }

    @Test
    fun testFilterWithNullValue() {
        val pairs = arrayOf(KeyValuePair("userId", "[0-9]+"))
        
        val filter = ContextMapRegexFilter.createFilter(pairs, null, Filter.Result.NEUTRAL, Filter.Result.DENY)
        
        val contextData = SortedArrayStringMap()
        contextData.putValue("userId", null) // null 值
        
        val logEvent = Log4jLogEvent.Builder()
            .setContextData(contextData)
            .build()
        
        val result = filter?.filter(logEvent)
        println("Should not match when value is null: $result")
    }
}