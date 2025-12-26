package com.kontranik.easycycle.model

import java.io.Serializable

data class Phase  (
    var key: Int? = null,
    var from: Int = 1,
    var to: Int? = null,
    var desc: String = "",
    var color: String? = null,  // color of current cycle
    var colorP: String? = null, // color of followed cycles (future)
    var markwholephase: Boolean = false, // has effect only in tabcalendar
    var notificateStart: Boolean? = true,
) : Serializable {}

data class PhaseUi  (
    var key: Int?,
    var from: String,
    var to: String,
    var desc: String,
    var color: String? = null,  // color of current cycle
    var colorP: String? = null, // color of followed cycles (future)
    var markwholephase: Boolean = false, // has effect only in tabcalendar
    var notificateStart: Boolean = true,
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
        notificateStart = notificateStart ?: true,
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
    )
}
