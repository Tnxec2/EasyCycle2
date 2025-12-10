package com.kontranik.easycycle.model

import java.io.Serializable

class Phase  (
    var key: Long,
    var from: Long,
    var to: Long? = null,
    var desc: String,
    var color: String? = null,  // color of current cycle
    var colorP: String? = null, // color of followed cycles (future)
    var markwholephase: Boolean? = false // has effect only in tabcalendar
) : Serializable {}