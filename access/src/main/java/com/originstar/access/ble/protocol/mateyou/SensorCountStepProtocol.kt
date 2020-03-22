package com.originstar.access.ble.protocol.mateyou

class SensorCountStepProtocol : MateYouProtocol(0xc6,0x26) {

    var step = 0
    init {
        responseLength = 0
        sendLength =0x01
    }

    override fun getData(): ByteArray {
        return byteArrayOf(0x08.toByte())
    }

    override fun resolveResponse(dataArray: ByteArray) {
        super.resolveResponse(dataArray)
        var dataLength = dataArray[1].toInt()
        if(dataLength>4) {
            step = (dataArray[2].toInt() shl 16) or
                    (dataArray[3].toInt() shl 8) or
                    (dataArray[4].toInt() and 0xFF)
        }
    }

    override fun toMap(): Map<String,Any> {
        super.toMap()
        mapData.putAll(
            hashMapOf(
                SensorAbility.KEY_STEP_TOATL to step
            )
        )
        return mapData
    }
}