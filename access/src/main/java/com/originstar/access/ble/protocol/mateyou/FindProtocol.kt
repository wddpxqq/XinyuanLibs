package com.originstar.access.ble.protocol.mateyou

class FindProtocol : MateYouProtocol(0xF3,0xF3) {

    companion object{
        var START_SEARCH = 0x01.toByte()
        var STOP_SEARCH = 0x0A.toByte()
    }

    init {
        responseLength = 0
        sendLength =0x01
    }

    var action =
        START_SEARCH
        set(value) {
            field = value
        }

    override fun getData(): ByteArray {
        return byteArrayOf(action)
    }

    override fun getCheckSum(dataArray: ByteArray?): Byte {
        return 0x00.toByte()
    }

    override fun toMap():Map<String,Any> {
        super.toMap()
        return mapData
    }
}