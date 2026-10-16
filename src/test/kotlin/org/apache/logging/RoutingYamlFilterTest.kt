package org.apache.logging

import org.apache.logging.filter.ContextKeyExistsFilter
import org.apache.logging.filter.ContextMapRegexFilter
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.ThreadContext
import org.apache.logging.log4j.core.Filter
import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.appender.routing.RoutingAppender
import org.apache.logging.log4j.core.util.KeyValuePair
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach

class RoutingYamlFilterTest {
    
    private lateinit var logger: Logger
    
    @BeforeEach
    fun setUp() {
        logger = LogManager.getLogger(RoutingYamlFilterTest::class.java)
    }
    
    @AfterEach
    fun tearDown() {
        ThreadContext.clearAll()
    }
    
    @Test
    fun testRoutingWithYamlConfiguration() {
        // 设置上下文数据，触发路由
        ThreadContext.put("type", "test123")
        
        logger.info("Testing routing with context 'type' set to 'test123'")
        
        println("Log event with type=test123 should be routed accordingly")
        
        // 清除上下文
        ThreadContext.clearAll()
        
        // 再次测试不同类型
        ThreadContext.put("type", "production")
        
        logger.info("Testing routing with context 'type' set to 'production'")
        
        println("Log event with type=production should be routed differently")
        
        ThreadContext.clearAll()
    }
    
    @Test
    fun testContextMapRegexFilterWithYaml() {
        // 设置上下文数据以测试正则表达式过滤器
        ThreadContext.put("type", "testSpecial")
        
        logger.info("Testing ContextMapRegexFilter with matching value")
        
        println("Log event with type=testSpecial should match regex pattern 'test.*'")
        
        ThreadContext.clearAll()
        
        ThreadContext.put("type", "nomatch")
        
        logger.info("Testing ContextMapRegexFilter with non-matching value")
        
        println("Log event with type=nomatch should not match regex pattern 'test.*'")
        
        ThreadContext.clearAll()
    }
    
    @Test
    fun testContextKeyExistsFilterWithYaml() {
        // 设置上下文数据以测试键存在过滤器
        ThreadContext.put("userId", "12345")
        
        logger.info("Testing ContextKeyExistsFilter with userId present")
        
        println("Log event with userId should pass ContextKeyExistsFilter")
        
        ThreadContext.clearAll()
        
        // 测试没有userId键的情况
        ThreadContext.put("otherKey", "someValue")
        
        logger.info("Testing ContextKeyExistsFilter without userId present")
        
        println("Log event without userId should not pass ContextKeyExistsFilter")
        
        ThreadContext.clearAll()
    }
    
    @Test
    fun testMultipleFiltersInRouting() {
        // 同时设置多个上下文键来测试过滤器组合
        ThreadContext.put("type", "testProd")
        ThreadContext.put("userId", "67890")
        
        logger.info("Testing multiple filters with both type and userId present")
        
        println("Log event with both type and userId should pass multiple filters")
        
        ThreadContext.clearAll()
        
        // 测试缺少某些键的情况
        ThreadContext.put("type", "testDev")
        // 注意：这里没有设置userId
        
        logger.info("Testing multiple filters with only type present")
        
        println("Log event with only type present should pass some filters but not others")
        
        ThreadContext.clearAll()
    }
    
    @Test
    fun testComplexRoutingScenario() {
        // 测试复杂的路由场景
        val scenarios = listOf(
            mapOf("type" to "testAdmin", "userId" to "admin123"),
            mapOf("type" to "testUser", "userId" to "user456"),
            mapOf("type" to "normalType", "otherField" to "value"),
            mapOf("type" to "testSpecial")
        )
        
        scenarios.forEachIndexed { index, scenario ->
            println("Testing scenario $index: $scenario")
            
            scenario.forEach { (key, value) ->
                ThreadContext.put(key, value)
            }
            
            logger.info("Complex routing scenario $index")
            
            ThreadContext.clearAll()
        }
    }
    
}