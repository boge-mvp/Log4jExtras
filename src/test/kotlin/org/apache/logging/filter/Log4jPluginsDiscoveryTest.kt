package org.apache.logging.filter

import org.apache.logging.log4j.core.config.plugins.util.PluginManager
import org.apache.logging.log4j.core.config.plugins.util.PluginRegistry
import org.apache.logging.log4j.core.config.plugins.util.PluginBuilder
import org.junit.jupiter.api.Test
import java.lang.reflect.Field

class Log4jPluginsDiscoveryTest {

    @Test
    fun discoverLog4jPlugins() {
        println("Discovering Log4j plugins...")

        try {
            // 使用 Log4j 的内置工具来发现插件
            println("Using PluginManager to collect plugins...")
            // 注意：PluginManager 可能不会直接暴露已收集的插件列表，
            // 所以我们尝试使用 PluginRegistry
            val pluginRegistry = PluginRegistry.getInstance()
            val registry = pluginRegistry.loadFromMainClassLoader()

            if (registry != null) {
                println("Found ${registry.size} plugin categories:")
                registry.forEach { (category, plugins) ->
                    println("Category: $category")
                    plugins.forEach {
                        println("  Plugin Name: ${it.key}")
                    }
                }
            } else {
                println("Could not access plugin registry via reflection")
            }
        } catch (e: Exception) {
            println("Error using PluginManager/PluginRegistry: ${e.message}")
            e.printStackTrace()
        }

    }

}