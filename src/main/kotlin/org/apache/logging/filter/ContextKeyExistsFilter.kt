package org.apache.logging.filter

import org.apache.logging.Keys
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.core.Filter
import org.apache.logging.log4j.core.Filter.Result
import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.Logger
import org.apache.logging.log4j.core.config.Node
import org.apache.logging.log4j.core.config.plugins.Plugin
import org.apache.logging.log4j.core.config.plugins.PluginAttribute
import org.apache.logging.log4j.core.config.plugins.PluginElement
import org.apache.logging.log4j.core.config.plugins.PluginFactory
import org.apache.logging.log4j.core.filter.AbstractFilter
import org.apache.logging.log4j.core.util.KeyValuePair
import org.apache.logging.log4j.message.Message
import org.apache.logging.log4j.util.PerformanceSensitive
import org.apache.logging.log4j.util.ReadOnlyStringMap
import java.util.*

/**
 * ContextKeyExistsFilter 是一个过滤器，用于检查上下文映射中是否存在指定的键。
 * 该过滤器只关注键是否存在，不关心键对应的值。
 * 
 * @param keys 要检查的键集合
 * @param isAnd 操作符，true 表示 AND 操作（所有键都必须存在），false 表示 OR 操作（任意键存在即可）
 * @param onMatch 当匹配时的处理结果
 * @param onMismatch 当不匹配时的处理结果
 */
@Plugin(
    name = "ContextKeyExistsFilter",
    category = Node.CATEGORY,
    elementType = Filter.ELEMENT_TYPE,
    printObject = true
)
@PerformanceSensitive("allocation")
class ContextKeyExistsFilter private constructor(
    private val keys:  List<String>,
    private val isAnd: Boolean,
    onMatch: Result?,
    onMismatch: Result?
) : AbstractFilter(onMatch, onMismatch) {

    override fun filter(logger: Logger?, level: Level?, marker: Marker?, msg: String?, vararg params: Any?): Result? {
        return filter()
    }

    override fun filter(logger: Logger?, level: Level?, marker: Marker?, msg: Any?, t: Throwable?): Result? {
        return filter()
    }

    override fun filter(logger: Logger?, level: Level?, marker: Marker?, msg: Message?, t: Throwable?): Result? {
        return filter()
    }

    override fun filter(event: LogEvent): Result? {
        return if (filter(event.contextData)) onMatch else onMismatch
    }

    override fun filter(logger: Logger?, level: Level?, marker: Marker?, msg: String?, p0: Any?): Result? {
        return filter()
    }

    override fun filter(logger: Logger?, level: Level?, marker: Marker?, msg: String?, p0: Any?, p1: Any?): Result? {
        return filter()
    }

    override fun filter(logger: Logger?, level: Level?, marker: Marker?, msg: String?, p0: Any?, p1: Any?, p2: Any?): Result? {
        return filter()
    }

    override fun filter(logger: Logger?, level: Level?, marker: Marker?, msg: String?, p0: Any?, p1: Any?, p2: Any?, p3: Any?): Result? {
        return filter()
    }

    override fun filter(logger: Logger?, level: Level?, marker: Marker?, msg: String?, p0: Any?, p1: Any?, p2: Any?, p3: Any?, p4: Any?): Result? {
        return filter()
    }

    override fun filter(logger: Logger?, level: Level?, marker: Marker?, msg: String?, p0: Any?, p1: Any?, p2: Any?, p3: Any?, p4: Any?, p5: Any?): Result? {
        return filter()
    }

    override fun filter(logger: Logger?, level: Level?, marker: Marker?, msg: String?, p0: Any?, p1: Any?, p2: Any?, p3: Any?, p4: Any?, p5: Any?, p6: Any?): Result? {
        return filter()
    }

    override fun filter(logger: Logger?, level: Level?, marker: Marker?, msg: String?, p0: Any?, p1: Any?, p2: Any?, p3: Any?, p4: Any?, p5: Any?, p6: Any?, p7: Any?): Result? {
        return filter()
    }

    override fun filter(logger: Logger?, level: Level?, marker: Marker?, msg: String?, p0: Any?, p1: Any?, p2: Any?, p3: Any?, p4: Any?, p5: Any?, p6: Any?, p7: Any?, p8: Any?): Result? {
        return filter()
    }

    override fun filter(logger: Logger?, level: Level?, marker: Marker?, msg: String?, p0: Any?, p1: Any?, p2: Any?, p3: Any?, p4: Any?, p5: Any?, p6: Any?, p7: Any?, p8: Any?, p9: Any?): Result? {
        return filter()
    }

    /**
     * 检查日志事件的上下文数据是否包含所有或任一键
     */
    private fun filter(): Result? {
        // 对于非事件相关的过滤，假设没有上下文数据，返回onMismatch
        // 或者根据实际需求决定行为
        return onMismatch
    }

    /**
     * 检查给定的ReadOnlyStringMap中是否包含所有或任一键
     */
    fun filter(data: ReadOnlyStringMap?): Boolean {
        if (data == null || keys.isEmpty()) {
            return if (isAnd) keys.isEmpty() else false
        }

        return if (isAnd) {
            // AND操作：所有键都必须存在
            keys.all { data.containsKey(it) }
        } else {
            // OR操作：至少有一个键存在
            keys.any { data.containsKey(it) }
        }
    }

    companion object {
        @PluginFactory
        @JvmStatic
        fun createFilter(
            @PluginElement("keys") keys: Array<Keys>,
            @PluginAttribute("operator") oper: String?,
            @PluginAttribute("onMatch") match: Result?,
            @PluginAttribute("onMismatch") mismatch: Result?
        ): ContextKeyExistsFilter? {
            if (keys.isEmpty()) {
                println("ContextKeyExistsFilter is not configured with any valid keys")
                return null
            }
            val isAnd = oper == null || !oper.equals("or", ignoreCase = true)
            val newKeys = keys.mapNotNull { it.key }
            return ContextKeyExistsFilter(newKeys, isAnd, match, mismatch)
        }
    }


}