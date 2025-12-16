package com.kontranik.easycycle.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.io.Serializable
import java.lang.reflect.Type

data class Phase  (
    var key: Long,
    var from: Int,
    var to: Int? = null,
    var desc: String,
    var color: String? = null,  // color of current cycle
    var colorP: String? = null, // color of followed cycles (future)
    var markwholephase: Boolean = false, // has effect only in tabcalendar
    var notificateStart: Boolean = true,
    var notificateEveryDay: Boolean = false,
) : Serializable {}

data class PhaseUi  (
    var key: Long,
    var from: String,
    var to: String,
    var desc: String,
    var color: String? = null,  // color of current cycle
    var colorP: String? = null, // color of followed cycles (future)
    var markwholephase: Boolean = false, // has effect only in tabcalendar
    var notificateStart: Boolean = true,
    var notificateEveryDay: Boolean = false,
)

fun Phase.toUi(): PhaseUi  {
    return PhaseUi(
        key = key,
        from = from.toString(),
        to = to?.toString() ?: "",
        desc = desc,
        color = color,
        colorP = colorP,
        markwholephase = markwholephase,
        notificateStart = notificateStart,
        notificateEveryDay = notificateEveryDay
    )
}

fun PhaseUi.toPhase(): Phase  {
    return Phase(
        key = key,
        from = from.toInt(),
        to = if (to.isNotEmpty()) to.toInt() else null,
        desc = desc,
        color = color,
        colorP = colorP,
        markwholephase = markwholephase,
        notificateStart = notificateStart,
        notificateEveryDay = notificateEveryDay
    )
}

class PhaseAdapter: JsonDeserializer<Phase>, JsonSerializer<Phase> {
    override fun serialize(
        src: Phase?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement? {
        if (src == null) return null
        val json = JsonObject()
        json.addProperty("key", src.key)
        json.addProperty("from", src.from)
        json.addProperty("to", src.to)
        json.addProperty("desc", src.desc)
        json.addProperty("color", src.color)
        json.addProperty("colorP", src.colorP)
        json.addProperty("markwholephase", src.markwholephase)
        json.addProperty("notificateStart", src.notificateStart)
        json.addProperty("notificateEveryDay", src.notificateEveryDay)
        return json
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Phase? {
        if (json == null) return null
        return Phase(
            key = json.asJsonObject["key"].asLong,
            from = json.asJsonObject["from"].asInt,
            to = json.asJsonObject["to"]?.asInt,
            desc = json.asJsonObject["desc"].asString,
            color = json.asJsonObject["color"]?.asString,
            colorP = json.asJsonObject["colorP"]?.asString,
            markwholephase = json.asJsonObject["markwholephase"]?.asBoolean ?: false,
            notificateStart = json.asJsonObject["notificateStart"]?.asBoolean ?: true,
            notificateEveryDay = json.asJsonObject["notificateEveryDay"]?.asBoolean ?: false,
        )
    }


}