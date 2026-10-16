package org.apache.logging.config.yaml

import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.config.Configuration
import org.apache.logging.log4j.core.config.ConfigurationFactory
import org.apache.logging.log4j.core.config.ConfigurationSource
import org.apache.logging.log4j.core.config.Order
import org.apache.logging.log4j.core.config.plugins.Plugin
import org.apache.logging.log4j.core.util.Loader

/**
 * @see org.apache.logging.log4j.core.config.yaml.YamlConfigurationFactory
 * @see com.fasterxml.jackson.dataformat.yaml.YAMLAnchorReplayingFactory
 */
@Plugin(name = "YamlAnchorReplayConfigurationFactory", category = ConfigurationFactory.CATEGORY)
@Order(8)
class YamlAnchorReplayConfigurationFactory : ConfigurationFactory() {

    companion object {
        /**
         * The file extensions supported by this factory.
         */
        private val SUFFIXES = arrayOf(".yml", ".yaml")

        private val dependencies = arrayOf(
            "com.fasterxml.jackson.databind.ObjectMapper",
            "com.fasterxml.jackson.databind.JsonNode",
            "com.fasterxml.jackson.core.JsonParser",
            "com.fasterxml.jackson.dataformat.yaml.YAMLAnchorReplayingFactory"
        )

    }

    private var isActive = false

    init {
        isActive = checkDependencies()
    }

    private fun checkDependencies(): Boolean {
        for (dependency in dependencies) {
            if (!Loader.isClassAvailable(dependency)) {
                LOGGER.debug(
                    "Missing dependencies for Yaml support, ConfigurationFactory {} is inactive",
                    this::class.java.name
                )
                return false
            }
        }
        return true
    }

    override fun isActive(): Boolean {
        return isActive
    }

    override fun getConfiguration(loggerContext: LoggerContext?, source: ConfigurationSource): Configuration? {
        if (!isActive) {
            return null
        }
        return YamlAnchorReplayConfiguration(loggerContext, source)
    }

    public override fun getSupportedTypes(): Array<String> {
        return SUFFIXES
    }


}