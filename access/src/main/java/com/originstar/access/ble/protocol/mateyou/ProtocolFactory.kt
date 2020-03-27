package com.originstar.access.ble.protocol.mateyou

class ProtocolFactory {

    companion object {

        fun create(cmd: Byte): MateYouProtocol {
            return when (cmd) {
                //手表与设备双向寻找
                toByte(0xF3) -> FindProtocol()
                //获取设备时间
                toByte(0x89), toByte(0x29), toByte(0x09) -> TimeGetProtocol()
                //设置设备时间
                toByte(0xc2), toByte(0x22), toByte(0x02) -> TimeSetProtocol()
                //设备设置
                toByte(0x9b),toByte(0xc2B), toByte(0X1B) -> SettingsProtocol()
                //获取当天当前计步数据命令
                toByte(0xc6), toByte(0x26), toByte(0x06) -> SensorCountStepProtocol()
                //开启传感器测量
                toByte(0x93), toByte(0x33), toByte(0x13) -> SensorMeasureProtocol()
                //设备主动上传传感器测试数据
                toByte(0x99) -> RespSensorProtocol()
                else -> ResponseProtocol(0x00)
            }
//            return when (cmd.toInt()) {
//                //手表与设备双向寻找
//                0xF3 -> FindProtocol()
//                //获取设备时间
//                0x89, 0x29, 0x09 -> TimeGetProtocol()
//                //设置设备时间
//                0xc2, 0x22, 0x02 -> TimeSetProtocol()
//                //获取当天当前计步数据命令
//                0xc6, 0x26, 0x06 -> SensorCountStepProtocol()
//                //开启传感器测量
//                0x93, 0x33, 0x13 -> SensorMeasureProtocol()
//                //设备主动上传传感器测试数据
//                0x99 -> RespSensorProtocol()
//                else -> ResponseProtocol(0x00)
//            }
        }

        private fun toByte(value: Int): Byte {
            return value.toByte()
        }
    }
}