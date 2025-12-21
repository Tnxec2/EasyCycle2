package com.kontranik.easycycle.model

import java.io.Serializable

data class Phase  (
    var key: Long,
    var from: Int,
    var to: Int? = null,
    var desc: String,
    var color: String? = null,  // color of current cycle
    var colorP: String? = null, // color of followed cycles (future)
    var markwholephase: Boolean = false, // has effect only in tabcalendar
    var notificateStart: Boolean? = true,
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
        notificateStart = notificateStart ?: true,
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
