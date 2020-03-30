package com.oris.access.ble.protocol.mateyou

class SensorMeasureProtocol : MateYouProtocol(0x93,0x33) {
    var status: Int = 0x00
    var sync: Int = 0x00
    var checkCount: Int = 0x00
    //#3-#6	XX/XX/XX/XX
    var type: Int = 0x00

    init {
        responseLength = 0
        sendLength =0x11
    }

    override fun getData(): ByteArray {
        return byteArrayOf(
            type.toByte(),//#3
            0,//#4
            0,////#5
            0,//#6
            status.toByte(),//#7
            sync.toByte(),//#8
            checkCount.toByte(),//#9
            0,//#10
            0,//#11
            0,//#12
            0,//#13
            0,//#14
            0,//#15
            0,//#16
            0,//#17
            0,//#18
            0//#19
        )
    }

    override fun toMap(): Map<String,Any> {
        super.toMap()
        mapData.putAll(
            hashMapOf(
            )
        )
        return mapData
    }
}
