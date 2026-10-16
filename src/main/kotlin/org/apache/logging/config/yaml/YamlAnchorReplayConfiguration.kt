package org.apache.logging.config.yaml

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLAnchorReplayingFactory
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.config.Configuration
import org.apache.logging.log4j.core.config.ConfigurationSource
import org.apache.logging.log4j.core.config.yaml.YamlConfiguration
import java.io.IOException

/**
 * @see YamlConfiguration
 */
class YamlAnchorReplayConfiguration(loggerContext: LoggerContext?, configSource: ConfigurationSource) : YamlConfiguration(loggerContext, configSource) {

    override fun getObjectMapper(): ObjectMapper? {
        return ObjectMapper(YAMLAnchorReplayingFactory())
            .configure(JsonParser.Feature.ALLOW_COMMENTS, true)
    }

    override fun reconfigure(): Configuration? {
        try {
            val source = configurationSource.resetInputStream() ?: return null
            return YamlAnchorReplayConfiguration(loggerContext, source)
        } catch (ex: IOException) {
            LOGGER.error("Cannot locate file {}", configurationSource, ex)
        }
        return null
    }

}