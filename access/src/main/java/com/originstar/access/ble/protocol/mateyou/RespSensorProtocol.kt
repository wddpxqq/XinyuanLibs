package com.originstar.access.ble.protocol.mateyou

class RespSensorProtocol : ResponseProtocol(0x99) {

    private var mHearRate: Int? = null

    init {
        responseLength = 0
        sendLength = 17
    }

    override fun resolveResponse(dataArray: ByteArray) {
        response?.let {
            when(it[2]){
                SensorAbility.CMD_HEART_RATE.toByte()->{
                    mHearRate = it[6].toInt()
                }
            }
        }
    }

    override fun toMap(): Map<String,Any> {
        super.toMap()

        mapData.putAll(
            hashMapOf<String, Any>(
                if(mHearRate == null){
                    SensorAbility.KEY_HEAR_RATE to false
                }else{
                    SensorAbility.KEY_HEAR_RATE to mHearRate!!
                }
            )
        )
        return mapData
    }
}