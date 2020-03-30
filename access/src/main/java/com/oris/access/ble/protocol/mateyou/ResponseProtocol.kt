package com.oris.access.ble.protocol.mateyou

open class ResponseProtocol(sendCmd:Int) : MateYouProtocol(sendCmd,sendCmd) {

    init {
        responseLength = 0
        sendLength =0x01
    }

    override fun getData(): ByteArray {
        return byteArrayOf(0x00.toByte())
    }

    override fun resolveResponse(dataArray: ByteArray) {
    }

    override fun toMap(): Map<String,Any> {
        super.toMap()
        return mapData
    }
}