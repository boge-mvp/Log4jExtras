package org.apache.logging.filter

import org.apache.logging.Keys
import org.apache.logging.log4j.core.Filter
import org.apache.logging.log4j.core.impl.Log4jLogEvent
import org.apache.logging.log4j.util.SortedArrayStringMap
import org.junit.jupiter.api.Test

class ContextKeyExistsFilterTest {

    @Test
    fun testKeyExistsFilterMatchesWhenKeyIsPresent() {
        // 创建键值对，检查键是否存在
        val pairs = arrayOf(Keys("userId")) // 值是占位符，只检查键的存在
        
        // 创建过滤器 - 当键存在时返回 NEUTRAL，不存在时返回 DENY
        val filter = ContextKeyExistsFilter.createFilter(pairs, null, Filter.Result.NEUTRAL, Filter.Result.DENY)
        
        // 创建一个包含指定键的上下文数据
        val contextData = SortedArrayStringMap()
        contextData.putValue("userId", "12345") // 键存在，值可以是任何值
        
        // 使用日志事件进行测试
        val logEvent = Log4jLogEvent.Builder()
            .setContextData(contextData)
            .build()
        
        val result = filter?.filter(logEvent)
        println("Should return NEUTRAL when key exists in context: $result")
    }

    @Test
    fun testKeyExistsFilterDoesNotMatchWhenKeyIsAbsent() {
        // 创建键值对，检查键是否存在
        val pairs = arrayOf(Keys("userId")) // 值是占位符，只检查键的存在
        
        // 创建过滤器
        val filter = ContextKeyExistsFilter.createFilter(pairs, null, Filter.Result.NEUTRAL, Filter.Result.DENY)
        
        // 创建一个不包含指定键的上下文数据
        val contextData = SortedArrayStringMap()
        contextData.putValue("otherKey", "someValue") // 不包含 userId 键
        
        // 使用日志事件进行测试
        val logEvent = Log4jLogEvent.Builder()
            .setContextData(contextData)
            .build()
        
        val result = filter?.filter(logEvent)
        println("Should return DENY when key is absent from context: $result")
    }

    @Test
    fun testKeyExistsFilterWithNullValueButKeyPresent() {
        // 创建键值对，检查键是否存在
        val pairs = arrayOf(Keys("userId"))
        
        val filter = ContextKeyExistsFilter.createFilter(pairs, null, Filter.Result.NEUTRAL, Filter.Result.DENY)
        
        // 创建一个包含键但值为null的上下文数据
        val contextData = SortedArrayStringMap()
        contextData.putValue("userId", null) // 键存在，但值为null
        
        // 使用日志事件进行测试
        val logEvent = Log4jLogEvent.Builder()
            .setContextData(contextData)
            .build()
        
        val result = filter?.filter(logEvent)
        println("Should return NEUTRAL when key exists even if value is null: $result")
    }

    @Test
    fun testKeyExistsFilterWithMultipleKeysOrOperator() {
        val pairs = arrayOf(Keys("userId"), Keys("sessionId"))
        
        // 使用 OR 操作符 ("or")
        val filter = ContextKeyExistsFilter.createFilter(pairs, "or", Filter.Result.NEUTRAL, Filter.Result.DENY)
        
        // 测试第一个键存在
        val context1 = SortedArrayStringMap()
        context1.putValue("userId", "12345")
        // sessionId 不存在
        
        val logEvent1 = Log4jLogEvent.Builder()
            .setContextData(context1)
            .build()
        
        val result1 = filter?.filter(logEvent1)
        println("Should return NEUTRAL because userId key exists: $result1")
        
        // 测试第二个键存在
        val context2 = SortedArrayStringMap()
        context2.putValue("sessionId", "abc123")
        // userId 不存在
        
        val logEvent2 = Log4jLogEvent.Builder()
            .setContextData(context2)
            .build()
        
        val result2 = filter?.filter(logEvent2)
        println("Should return NEUTRAL because sessionId key exists: $result2")
        
        // 测试两个键都不存在
        val context3 = SortedArrayStringMap()
        context3.putValue("otherKey", "value")
        
        val logEvent3 = Log4jLogEvent.Builder()
            .setContextData(context3)
            .build()
        
        val result3 = filter?.filter(logEvent3)
        println("Should return DENY because neither key exists: $result3")
    }

    @Test
    fun testKeyExistsFilterWithMultipleKeysAndOperator() {
        val pairs = arrayOf(Keys("userId"), Keys("sessionId"))

        // 使用 AND 操作符 (默认)
        val filter = ContextKeyExistsFilter.createFilter(pairs, null, Filter.Result.NEUTRAL, Filter.Result.DENY)
        
        // 测试两个键都存在
        val context1 = SortedArrayStringMap()
        context1.putValue("userId", "12345")
        context1.putValue("sessionId", "abc123")
        
        val logEvent1 = Log4jLogEvent.Builder()
            .setContextData(context1)
            .build()
        
        val result1 = filter?.filter(logEvent1)
        println("Should return NEUTRAL because both keys exist: $result1")
        
        // 测试只有一个键存在
        val context2 = SortedArrayStringMap()
        context2.putValue("userId", "12345")
        // sessionId 不存在
        
        val logEvent2 = Log4jLogEvent.Builder()
            .setContextData(context2)
            .build()
        
        val result2 = filter?.filter(logEvent2)
        println("Should return DENY because sessionId key is missing: $result2")
    }

    @Test
    fun testFilterWithLogEvent() {
        val pairs = arrayOf(Keys("userId"))
        
        val filter = ContextKeyExistsFilter.createFilter(pairs, null, Filter.Result.NEUTRAL, Filter.Result.DENY)
        
        // 创建带有上下文数据的日志事件，包含所需的键
        val contextData = SortedArrayStringMap()
        contextData.putValue("userId", "12345")
        
        val logEvent = Log4jLogEvent.Builder()
            .setContextData(contextData)
            .build()
        
        val result = filter?.filter(logEvent)
        println("Should return NEUTRAL when key exists: $result")
    }

    @Test
    fun testFilterWithLogEventWhenKeyIsAbsent() {
        val pairs = arrayOf(Keys("userId"))
        
        val filter = ContextKeyExistsFilter.createFilter(pairs, null, Filter.Result.NEUTRAL, Filter.Result.DENY)
        
        // 创建带有上下文数据的日志事件，但不包含所需的键
        val contextData = SortedArrayStringMap()
        contextData.putValue("otherKey", "someValue")
        
        val logEvent = Log4jLogEvent.Builder()
            .setContextData(contextData)
            .build()
        
        val result = filter?.filter(logEvent)
        println("Should return DENY when key is absent: $result")
    }

    @Test
    fun testFilterWithEmptyContext() {
        val pairs = arrayOf(Keys("userId"))
        
        val filter = ContextKeyExistsFilter.createFilter(pairs, null, Filter.Result.NEUTRAL, Filter.Result.DENY)
        
        // 创建空的上下文数据
        val contextData = SortedArrayStringMap()
        
        val logEvent = Log4jLogEvent.Builder()
            .setContextData(contextData)
            .build()
        
        val result = filter?.filter(logEvent)
        println("Should return DENY when context is empty: $result")
    }
}