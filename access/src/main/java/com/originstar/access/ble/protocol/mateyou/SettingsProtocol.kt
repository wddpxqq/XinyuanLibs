package com.originstar.access.ble.protocol.mateyou

class SettingsProtocol:MateYouProtocol(0x9B,0x2B) {

    init {
        responseLength = 0
        sendLength =0x0F
    }

    override fun getData(): ByteArray? {
        return byteArrayOf(
            1,//#3 12小时制00，24小时制01
            0,//#4 摄氏度00,华氏度01
            0,////#5 灭屏时间设置，单位秒。最小0，最大255
            0,//#6 正反屏，正屏00，反屏01
            1,//#7 计量单位 1：公制 2：英制
            0,//#8 震动 0:马达震动关闭 1:打开 2:防丢震动关闭
            0,//#9 抬手亮屏开关 0:关闭 1:打开
            1,//#10 语言选择 0：英文 1：中文 2: 俄罗斯语 3: 乌克兰语 默认：0
            1,//#11 勿扰模式 0：关闭 1：打开
            0,//#12 勿扰时段开始小时
            0,//#13 勿扰时段开始分钟
            0,//#14 勿扰时段结束小时
            0,//#15 勿扰时段结束分钟
            0,//#16 横竖屏
            1//#17 日期显示方式 1：公制 2：英制
        )
    }
}