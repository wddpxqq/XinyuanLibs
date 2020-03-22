package com.originstar.access.ble.protocol.mateyou

class SensorAbility {
    companion object {

        const val KEY_STATUS = "success"
        const val KEY_HEAR_RATE = "heart_Rate"
        const val KEY_STEP_TOATL = "step_total"

        const val CMD_HEART_RATE = 0x80
        const val CMD_BLOOD_PRESSURE = 0x40
        const val CMD_SPO2H = 0x20

        const val CMD_START = 0x01
        const val CMD_STOP = 0x00
        const val SETTING = 0x02

        const val CMD_SYNC = 0x01
        const val CMD_STOP_SYNC = 0x00
    }
}