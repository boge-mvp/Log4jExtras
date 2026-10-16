package org.apache.logging.filter

import org.apache.logging.log4j.core.Filter
import org.apache.logging.log4j.core.Filter.Result
import org.apache.logging.log4j.core.config.Node
import org.apache.logging.log4j.core.config.plugins.Plugin
import org.apache.logging.log4j.core.config.plugins.PluginAttribute
import org.apache.logging.log4j.core.config.plugins.PluginElement
import org.apache.logging.log4j.core.config.plugins.PluginFactory
import org.apache.logging.log4j.core.util.KeyValuePair
import org.apache.logging.log4j.util.PerformanceSensitive
import org.apache.logging.log4j.util.ReadOnlyStringMap
import java.util.regex.Pattern

/**
 * ContextMapRegexFilter 是一个基于正则表达式的上下文映射过滤器。
 * 它允许使用正则表达式模式匹配上下文映射中的值，提供了比简单字符串匹配更强大的过滤能力。
 * 
 * @param pairs 键值对映射，其中值是正则表达式模式列表
 * @param oper 布尔值，指示多个条件之间的操作关系（AND/OR）
 * @param onMatch 当条件匹配时的过滤结果
 * @param onMismatch 当条件不匹配时的过滤结果
 * 
 * @see org.apache.logging.log4j.core.filter.ThreadContextMapFilter
 */
@Plugin(
    name = "ContextMapRegexFilter",
    category = Node.CATEGORY,
    elementType = Filter.ELEMENT_TYPE,
    printObject = true
)
@PerformanceSensitive("allocation")
class ContextMapRegexFilter(
    pairs: Map<String, List<String>>,
    oper: Boolean,
    onMatch: Result?,
    onMismatch: Result?
) : BaseContextMapFilter(pairs, oper, onMatch, onMismatch) {

    private val compiledPatterns: Map<String, List<Pattern>>

    init {
        // 预编译正则表达式模式以提高性能
        val patternMap = mutableMapOf<String, MutableList<Pattern>>()
        for ((key, values) in pairs) {
            val patterns = values.map { Pattern.compile(it) }.toMutableList()
            patternMap[key] = patterns
        }
        this.compiledPatterns = patternMap
    }

    override fun filter(): Result? {
        var match = false
        if (useMap) {
            val map = stringMap
            for (i in 0..<map.size()) {
                val key = map.getKeyAt(i)
                val toMatch: String? = getContextValue(key)
                val patterns = compiledPatterns[key]
                match = toMatch != null && patterns?.any { it.matcher(toMatch).find() } == true
                if ((!isAnd && match) || (isAnd && !match)) {
                    break
                }
            }
        } else {
            val toMatch: String? = getContextValue(key)
            val patterns = compiledPatterns[key]
            match = toMatch != null && patterns?.any { it.matcher(toMatch).find() } == true
        }
        return if (match) onMatch else onMismatch
    }

    override fun filter(data: ReadOnlyStringMap?): Boolean {
        var match = false
        for (i in 0..<stringMap.size()) {
            val key = stringMap.getKeyAt(i)
            val toMatch = data!!.getValue<String?>(key)
            val patterns = compiledPatterns[key]
            match = toMatch != null && patterns?.any { it.matcher(toMatch).find() } == true
            if ((!isAnd && match) || (isAnd && !match)) {
                break
            }
        }
        return match
    }

    companion object {
        @JvmStatic
        @PluginFactory
        fun createFilter(
            @PluginElement("Pairs") pairs: Array<KeyValuePair>?,
            @PluginAttribute("operator") oper: String?,
            @PluginAttribute("onMatch") match: Result?,
            @PluginAttribute("onMismatch") mismatch: Result?
        ): ContextMapRegexFilter? = createFilterBase(pairs, oper, match, mismatch)
    }

}