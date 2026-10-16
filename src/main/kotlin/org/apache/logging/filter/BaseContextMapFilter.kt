package org.apache.logging.filter

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.core.ContextDataInjector
import org.apache.logging.log4j.core.Filter.Result
import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.Logger
import org.apache.logging.log4j.core.filter.MapFilter
import org.apache.logging.log4j.core.impl.ContextDataFactory
import org.apache.logging.log4j.core.impl.ContextDataInjectorFactory
import org.apache.logging.log4j.core.util.KeyValuePair
import org.apache.logging.log4j.message.Message
import org.apache.logging.log4j.util.ReadOnlyStringMap
import java.util.*

/**
 * BaseContextMapFilter 是一个抽象基类，为上下文映射过滤器提供基础功能。
 * 它提供了处理键值对映射的基础逻辑，支持单个键值对或多个键值对的过滤操作。
 *
 * @param pairs 要检查的键值对映射
 * @param oper 操作符，true 表示 AND 操作，false 表示 OR 操作
 * @param onMatch 当匹配时的处理结果
 * @param onMismatch 当不匹配时的处理结果
 * @see org.apache.logging.log4j.core.filter.ThreadContextMapFilter
 */
abstract class BaseContextMapFilter(
    pairs: Map<String, List<String>>,
    oper: Boolean,
    onMatch: Result?,
    onMismatch: Result?
) : MapFilter(pairs, oper, onMatch, onMismatch) {

    protected val injector: ContextDataInjector = ContextDataInjectorFactory.createInjector()
    protected var key: String? = null
    protected var value: String? = null

    protected var useMap = false

    init {
        // ContextDataFactory looks up a property. The Spring PropertySource may log which will cause recursion.
        // By initializing the ContextDataFactory here recursion will be prevented.
        val map = ContextDataFactory.createContextData()
        LOGGER.debug(
            "Successfully initialized ContextDataFactory by retrieving the context data with {} entries",
            map.size()
        )
        if (pairs.size == 1) {
            val iter = pairs.entries.iterator()
            val entry = iter.next()
            if (entry.value.size == 1) {
                this.key = entry.key
                this.value = entry.value[0]
                this.useMap = false
            } else {
                this.key = null
                this.value = null
                this.useMap = true
            }
        } else {
            this.key = null
            this.value = null
            this.useMap = true
        }
    }

    override fun filter(
        logger: Logger?, level: Level?, marker: Marker?, msg: String?, vararg params: Any?
    ): Result? {
        return filter()
    }

    override fun filter(
        logger: Logger?, level: Level?, marker: Marker?, msg: Any?, t: Throwable?
    ): Result? {
        return filter()
    }

    override fun filter(
        logger: Logger?, level: Level?, marker: Marker?, msg: Message?, t: Throwable?
    ): Result? {
        return filter()
    }

    abstract fun filter(): Result?

    protected fun getContextValue(key: String?): String? {
        return Objects.toString(injector.getValue(key), null)
    }

    override fun filter(event: LogEvent): Result? {
        return if (filter(event.contextData)) onMatch else onMismatch
    }

    abstract override fun filter(data: ReadOnlyStringMap?): Boolean

    override fun filter(
        logger: Logger?, level: Level?, marker: Marker?, msg: String?, p0: Any?
    ): Result? {
        return filter()
    }

    override fun filter(
        logger: Logger?,
        level: Level?,
        marker: Marker?,
        msg: String?,
        p0: Any?,
        p1: Any?
    ): Result? {
        return filter()
    }

    override fun filter(
        logger: Logger?,
        level: Level?,
        marker: Marker?,
        msg: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?
    ): Result? {
        return filter()
    }

    override fun filter(
        logger: Logger?,
        level: Level?,
        marker: Marker?,
        msg: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?
    ): Result? {
        return filter()
    }

    override fun filter(
        logger: Logger?,
        level: Level?,
        marker: Marker?,
        msg: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?
    ): Result? {
        return filter()
    }

    override fun filter(
        logger: Logger?,
        level: Level?,
        marker: Marker?,
        msg: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?
    ): Result? {
        return filter()
    }

    override fun filter(
        logger: Logger?,
        level: Level?,
        marker: Marker?,
        msg: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?
    ): Result? {
        return filter()
    }

    override fun filter(
        logger: Logger?,
        level: Level?,
        marker: Marker?,
        msg: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?
    ): Result? {
        return filter()
    }

    override fun filter(
        logger: Logger?,
        level: Level?,
        marker: Marker?,
        msg: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?
    ): Result? {
        return filter()
    }

    override fun filter(
        logger: Logger?,
        level: Level?,
        marker: Marker?,
        msg: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?,
        p9: Any?
    ): Result? {
        return filter()
    }

    companion object {

        @JvmStatic
        protected inline fun <reified T : BaseContextMapFilter> createFilterBase(
            pairs: Array<KeyValuePair>?,
            oper: String?,
            match: Result?,
            mismatch: Result?
        ): T? {
            if (pairs.isNullOrEmpty()) {
                LOGGER.error("key and value pairs must be specified for the ContextMapRegexFilter")
                return null
            }
            val map: MutableMap<String, MutableList<String>> = mutableMapOf()
            for (pair in pairs) {
                val key = pair.key
                if (key == null) {
                    LOGGER.error("A null key is not valid in MapFilter")
                    continue
                }
                val value = pair.value
                if (value == null) {
                    LOGGER.error("A null value for key $key is not allowed in MapFilter")
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
                LOGGER.error("ContextMapRegexFilter is not configured with any valid key value pairs")
                return null
            }
            val isAnd = oper == null || !oper.equals("or", ignoreCase = true)
            val klass = T::class.java
            return klass.getDeclaredConstructor(
                MutableMap::class.java,
                Boolean::class.java,
                Result::class.java,
                Result::class.java
            ).newInstance(map, isAnd, match, mismatch)
        }
    }

}