package com.oris.access.ble.protocol.mateyou

import java.util.*

class HistortySportProtocol : MateYouProtocol(0xB3,0xB3) {
    var year = 0x00
    var month = 0x00
    var date = 0x00
    init {
        responseLength = 0
        sendLength =0x03
    }

    var mCalendar: Calendar? = null
        set(value) {
            field = value
            field?.let {
                year = it.get(Calendar.YEAR) - 2000
                month = it.get(Calendar.MONTH) + 1
                date = it.get(Calendar.DAY_OF_MONTH)
            }
        }

    override fun getData(): ByteArray {
        return byteArrayOf(
            year.toByte(),
            month.toByte(),
            date.toByte()
        )
    }

    override fun resolveResponse(dataArray: ByteArray) {

//        response?.let {
//            when(it[2]){
//                SensorAbility.CMD_HEART_RATE.toByte()->{
//                    mHearRate = it[6].toInt()
//                }
//            }
//        }
    }

    override fun toMap(): Map<String,Any> {
        super.toMap()
        mapData.putAll(hashMapOf())
        return mapData
    }
}