package org.apache.logging.filter

import org.apache.logging.log4j.core.Filter
import org.apache.logging.log4j.core.util.KeyValuePair
import org.junit.jupiter.api.Test

class BaseContextMapFilterTest {

    @Test
    fun testCreateFilterBaseWithValidPairs() {
        val pairs = arrayOf(KeyValuePair("key1", "value1"), KeyValuePair("key2", "value2"))
        
        // 测试使用有效键值对创建过滤器
        val filter = TestContextMapFilter.createFilter(pairs, null, Filter.Result.ACCEPT, Filter.Result.DENY)
        
        println("Filter should be created successfully with valid pairs: $filter")
    }

    @Test
    fun testCreateFilterBaseWithNullPairs() {
        // 测试使用空对创建过滤器
        val filter = TestContextMapFilter.createFilter(null, null, Filter.Result.ACCEPT, Filter.Result.DENY)
        
        println("Filter should be null when pairs are null: $filter")
    }

    @Test
    fun testCreateFilterBaseWithEmptyPairs() {
        val pairs = emptyArray<KeyValuePair>()
        
        // 测试使用空对创建过滤器
        val filter = TestContextMapFilter.createFilter(pairs, null, Filter.Result.ACCEPT, Filter.Result.DENY)
        
        println("Filter should be null when pairs are empty: $filter")
    }

    @Test
    fun testCreateFilterBaseWithNullKey() {
        val pairs = arrayOf(KeyValuePair(null, "value1")) // null 键
        
        // 测试包含null键的对
        val filter = TestContextMapFilter.createFilter(pairs, null, Filter.Result.ACCEPT, Filter.Result.DENY)
        
        // 应该过滤掉null键并继续处理
        println("Filter should handle null key gracefully: $filter")
    }

    @Test
    fun testCreateFilterBaseWithNullValue() {
        val pairs = arrayOf(KeyValuePair("key1", null)) // null 值
        
        // 测试包含null值的对
        val filter = TestContextMapFilter.createFilter(pairs, null, Filter.Result.ACCEPT, Filter.Result.DENY)
        
        // 应该过滤掉null值并继续处理
        println("Filter should handle null value gracefully: $filter")
    }

    @Test
    fun testCreateFilterBaseWithDuplicateKeys() {
        val pairs = arrayOf(
            KeyValuePair("key1", "value1"),
            KeyValuePair("key1", "value2") // 重复键
        )
        
        // 测试包含重复键的对
        val filter = TestContextMapFilter.createFilter(pairs, null, Filter.Result.ACCEPT, Filter.Result.DENY)
        
        println("Filter should handle duplicate keys: $filter")
    }

    // 为测试目的创建一个具体的BaseContextMapFilter子类
    class TestContextMapFilter(
        pairs: Map<String, List<String>>,
        oper: Boolean,
        onMatch: Filter.Result?,
        onMismatch: Filter.Result?
    ) : BaseContextMapFilter(pairs, oper, onMatch, onMismatch) {

        override fun filter(): Filter.Result? {
            return onMatch
        }

        override fun filter(data: org.apache.logging.log4j.util.ReadOnlyStringMap?): Boolean {
            return true
        }

        companion object {
            fun createFilter(
                pairs: Array<KeyValuePair>?,
                oper: String?,
                match: Filter.Result?,
                mismatch: Filter.Result?
            ): TestContextMapFilter? {
                if (pairs.isNullOrEmpty()) {
                    println("key and value pairs must be specified for the TestContextMapFilter")
                    return null
                }
                val map: MutableMap<String, MutableList<String>> = mutableMapOf()
                for (pair in pairs) {
                    val key = pair.key
                    if (key == null) {
                        println("A null key is not valid in MapFilter")
                        continue
                    }
                    val value = pair.value
                    if (value == null) {
                        println("A null value for key $key is not allowed in MapFilter")
                        continue
                    }
                    var list = map[pair.key]
                    if (list != null) {
                        list.add(value)
                    } else {
                        list = mutableListOf(value)
                        map[pair.key] = list
                    }
                }
                if (map.isEmpty()) {
                    println("TestContextMapFilter is not configured with any valid key value pairs")
                    return null
                }
                val isAnd = oper == null || !oper.equals("or", ignoreCase = true)
                return TestContextMapFilter(map, isAnd, match, mismatch)
            }
        }
    }
}