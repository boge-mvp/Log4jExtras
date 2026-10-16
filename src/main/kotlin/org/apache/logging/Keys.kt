package org.apache.logging

import org.apache.logging.log4j.core.config.Node
import org.apache.logging.log4j.core.config.plugins.Plugin
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory

@Plugin(name = "keys", category = Node.CATEGORY, printObject = true)
class Keys(val key: String?) {

    companion object {
        @PluginBuilderFactory
        @JvmStatic
        fun newBuilder(): Builder {
            return Builder()
        }
    }

}

data class Builder(
    @field:PluginBuilderAttribute
    @field:SuppressWarnings("log4j.public.setter")
    var key: String? = null
) : org.apache.logging.log4j.core.util.Builder<Keys> {

    override fun build(): Keys {
        return Keys(key)
    }

}