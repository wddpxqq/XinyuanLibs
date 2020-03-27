package com.originstar.access.ble.protocol.mateyou

import com.originstar.access.ble.protocol.BleProtocol
import com.originstar.access.ble.protocol.mateyou.SensorAbility.Companion.KEY_STATUS
import kotlin.experimental.xor

abstract class MateYouProtocol(val cmdCode:Int, private val successCode:Int): BleProtocol() {
    protected var sendLength = 0
    protected var responseLength = 0
    protected var arrayLength =0

    protected var mapData = HashMap<String,Any>()
    var respCode:Int = 0

    //send value
    var value: ByteArray? = null
       get()  {
            field = getSendData()
            return field
        }

    // response value
    var response: ByteArray? = null
        set(value) {
            field = value
            value?.let { resolveResponse(it) }
        }

    var isSuccess = false
        get(){
          return  respCode xor successCode == 0
        }

    private fun getSendData(): ByteArray? {
        var dataByteArray = getData()
        var dataLength = dataByteArray?.size ?: 0
        val arraySize = dataLength+3//cmd length checksum
        var protocolByteArray = ByteArray(arraySize)
        protocolByteArray[0] = cmdCode.toByte()
        protocolByteArray[1] = sendLength.toByte()

        var index = 2
        while ((index < arraySize - 1) && dataLength > 0) {
            protocolByteArray[index] = dataByteArray!![index - 2]
            index++
        }

        var checksum = getCheckSum(dataByteArray)
        protocolByteArray[protocolByteArray.size - 1] = checksum
        return protocolByteArray
    }

    abstract fun getData():ByteArray?

    open fun getCheckSum(dataArray : ByteArray?):Byte {
        return if (dataArray == null) 0x00.toByte()
        else {
            val length = dataArray.size
            var result = dataArray[0]
            for (i in 1 until length) {
                result = result xor dataArray[i]
            }
            result
        }
    }

    open fun resolveResponse(dataArray : ByteArray){

    }

    open fun toMap():Map<String,Any> {
        mapData[KEY_STATUS] = isSuccess
        return mapData
    }
}

