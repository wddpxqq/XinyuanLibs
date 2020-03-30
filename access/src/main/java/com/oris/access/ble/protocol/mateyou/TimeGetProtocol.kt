package com.oris.access.ble.protocol.mateyou

import java.util.*

class TimeGetProtocol : MateYouProtocol(0x89,0x29) {
    //(0~99)
    var year = 0x00
    //(1~12)
    var month = 0x01
    var date = 0x01
    var hour = 0x01
    var minute = 0x01
    var second = 0x01
    var week = 0x01

    init {
        responseLength = 9
    }

    override fun getData(): ByteArray? {
        return null
    }

    override fun toMap(): Map<String,Any> {
        super.toMap()
        mapData.putAll(
            hashMapOf(
                "year" to year + 2000,
                "month" to month,
                "minute" to minute,
                "second" to second,
                "date" to date,
                "week" to week,
                "hour" to hour,
                "calendar" to Calendar.getInstance().apply {
                    set(year+2000,month,date,hour,minute,second)
                }
            )
        )
        return mapData
    }
}