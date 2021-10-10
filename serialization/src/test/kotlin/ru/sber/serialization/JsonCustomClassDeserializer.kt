package ru.sber.serialization

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class JsonCustomClassDeserializer {


    @Test
    fun `Нобходимо десериализовать данные в класс`() {
        // given
        val data = """{"client": "Нестеров Никита Викторович"}"""

        val module = SimpleModule("CustomCarDeserializer", Version(1, 0, 0, null, null, null))
        module.addDeserializer(Client7::class.java, CustomClient7Deserializer())

        val objectMapper = ObjectMapper()
            .registerModules(KotlinModule(), JavaTimeModule(), module)

        // when
        val client = objectMapper.readValue<Client7>(data)

        // then
        assertEquals("Никита", client.firstName)
        assertEquals("Нестеров", client.lastName)
        assertEquals("Викторович", client.middleName)
    }
}

class CustomClient7Deserializer @JvmOverloads constructor(vc: Class<*>? = null) : StdDeserializer<Client7?>(vc) {
    override fun deserialize(parser: JsonParser, deserializer: DeserializationContext): Client7 {
        val codec = parser.codec
        val node = codec.readTree<JsonNode>(parser)

        val clientSplit = node["client"].toString().replace("\"", "").trim().split(" ")
        return Client7(clientSplit[1], clientSplit[0], clientSplit[2])
    }
}