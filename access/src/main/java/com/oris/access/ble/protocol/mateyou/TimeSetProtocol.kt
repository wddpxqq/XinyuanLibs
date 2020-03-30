package com.oris.access.ble.protocol.mateyou

import java.util.*

class TimeSetProtocol : MateYouProtocol(0xC2,0x22) {
    //(0~99)
    var year = 0x00
    //(1~12)
    var month = 0x01
    var date = 0x01
    var hour = 0x01
    var minute = 0x01
    var second = 0x01
    var week = 0x01

    var mCalendar: Calendar? = null
        set(value) {
            field = value
            field?.let {
                year = it.get(Calendar.YEAR) - 2000
                month = it.get(Calendar.MONTH) + 1
                date = it.get(Calendar.DAY_OF_MONTH)
                hour = it.get(Calendar.HOUR_OF_DAY)
                minute = it.get(Calendar.MINUTE)
                second = it.get(Calendar.SECOND)
                week = it.get(Calendar.DAY_OF_WEEK)
            }
        }

    init {
        sendLength = 7
        responseLength = 4
        arrayLength = 10
    }

    override fun getData(): ByteArray {
        return byteArrayOf(
            year.toByte(),
            month.toByte(),
            date.toByte(),
            hour.toByte(),
            minute.toByte(),
            second.toByte(),
            week.toByte()
        )
    }

    override fun toMap(): Map<String, Any> {
        super.toMap()
        return mapData
    }
}