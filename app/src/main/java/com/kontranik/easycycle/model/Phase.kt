package com.kontranik.easycycle.model

import java.io.Serializable
import kotlin.toString

class Phase  (
    var key: Long,
    var from: Long,
    var to: Long? = null,
    var desc: String,
    var color: String? = null,  // color of current cycle
    var colorP: String? = null, // color of followed cycles (future)
    var markwholephase: Boolean = false // has effect only in tabcalendar
) : Serializable {}

data class PhaseUi  (
    var key: Long,
    var from: String,
    var to: String,
    var desc: String,
    var color: String? = null,  // color of current cycle
    var colorP: String? = null, // color of followed cycles (future)
    var markwholephase: Boolean = false // has effect only in tabcalendar
)

fun Phase.toUi(): PhaseUi  {
    return PhaseUi(
        key = key,
        from = from.toString(),
        to = to?.toString() ?: "",
        desc = desc,
        color = color,
        colorP = colorP,
        markwholephase = markwholephase
    )
}

fun PhaseUi.toPhase(): Phase  {
    return Phase(
        key = key,
        from = from.toLong(),
        to = if (to.isNotEmpty()) to.toLong() else null,
        desc = desc,
        color = color,
        colorP = colorP,
        markwholephase = markwholephase
    )
}