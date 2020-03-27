package com.originstar.access.ble

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.SparseArray
import androidx.core.util.set
import com.originstar.access.ble.protocol.mateyou.*
import com.originstar.orislog.orisLog
import java.util.*
import kotlin.collections.HashMap


class MateYouBleDevice(device: BluetoothDevice):BleDevice<MateYouProtocol>(device) {
    companion object{
        val BLUETOOTH_DEVICE_NAME =  arrayListOf("ActiveFit 2.0","V2","Q08","MST89")
    }
    private val TAG = "MateYouBleDevice"
    private var mOnDeviceReady: ((isConnected:Boolean) -> Unit)? = null
    private val PRIMARY_SERVICES_UUID = UUID.fromString("00000001-0000-1000-8000-00805f9b34fb")
    private val SEND_UUID = UUID.fromString("00000002-0000-1000-8000-00805f9b34fb")
    private val NOTIFY_UUID = UUID.fromString("00000003-0000-1000-8000-00805f9b34fb")
    private val mNotifyCharacteristic = HashMap<UUID, Boolean>()
    private val mCallbackDispatcher = SparseArray<(version: Map<String,Any>) -> Unit>()
    private val mUiHandler  = Handler(Looper.getMainLooper())
    private var mPrimaryService: BluetoothGattService? = null
        get() {
            if (field == null) {
                field = getServices(this.PRIMARY_SERVICES_UUID)
            }
            return field
        }

    init {
        registerOnCharacteristicChangedCallback { data -> dispatch(data) }
    }

    fun registerSensorDataArrived(callback: (version: Map<String,Any>) -> Unit) {
        addCallback(RespSensorProtocol(),callback)
    }

    fun registerOnDeviceReady(listener: (isConnected:Boolean) -> Unit) {
        mOnDeviceReady = listener
    }

    override fun onDevicesDisconnected() {
        mNotifyCharacteristic.clear()
        mOnDeviceReady?.invoke(false)
    }

    override fun reConnect(mContext: Context){
        mNotifyCharacteristic.clear()
        super.reConnect(mContext)
    }

    fun unRegisterAllListener() {
        mOnDeviceReady = null
        mCallbackDispatcher.clear()
    }

    override fun onDevicesConnected(gatt: BluetoothGatt) {
        orisLog.d(TAG, "start discoverServices")
        gatt.discoverServices()
    }

    override fun onServicesDiscovered() {
        orisLog.d(TAG, "setNotified")
        setNotified(NOTIFY_UUID, true)
        mUiHandler.postDelayed({
            mOnDeviceReady?.invoke(true)
        }, 3000)
    }

    @Synchronized
    private fun addCallback(protocol: MateYouProtocol, callback: (version: Map<String,Any>) -> Unit) {
        mCallbackDispatcher.put(protocol.cmdCode,callback)
    }

    @Synchronized
    private fun removeCallback(key: Int) {
        mCallbackDispatcher.remove(key)
    }

    override fun convert(data: ByteArray): MateYouProtocol {
        val cmd = data[0]
        val protocol = ProtocolFactory.create(cmd)
        protocol.response = data
        protocol.respCode = cmd.toInt() and 0xff
        orisLog.d(TAG, "resp-convert:${cmd.toInt()} protocol:$protocol status:${protocol.isSuccess}")
        return protocol
    }

    private fun dispatch(protocol: MateYouProtocol) {
        var callback: ((version: Map<String,Any>) -> Unit)? = null
        if (protocol is ResponseProtocol) {
            callback = mCallbackDispatcher.get(protocol.cmdCode)
            callback?.let {
                orisLog.d(TAG, "resp-dispatch:client:${protocol.respCode}")
                mUiHandler.post { it.invoke(protocol.toMap()) }
            }
        } else {
            callback = mCallbackDispatcher.get(protocol.cmdCode)
            callback?.let {
                orisLog.d(TAG, "resp-dispatch:server:${protocol.respCode}")
                mUiHandler.post { it.invoke(protocol.toMap()) }
                removeCallback(protocol.cmdCode)
            }
        }

        if (callback == null) {
            orisLog.d(TAG, "resp-dispatch:callback is null protocol-s[${protocol.cmdCode}]-r[${protocol.respCode}]")
        }
    }

    private fun setNotified(uuid: UUID, enable: Boolean): Boolean {
        val enabled = mNotifyCharacteristic[uuid]
        if (enable == enabled) {
            return true
        } else {
            orisLog.d(TAG, "notify status is changed $enable $enabled")
            mPrimaryService?.let {
                mNotifyCharacteristic[uuid] = enable
                return setNotify(it.getCharacteristic(NOTIFY_UUID), enable)
            }
        }
        return false
    }

    fun find() {
        orisLog.d(TAG, "find mate you device ")
        mPrimaryService?.getCharacteristic(SEND_UUID)?.let {
            var protocol = FindProtocol()
                .apply {
                action = FindProtocol.START_SEARCH
            }
            it.value = protocol.value
            write(it)
        }
    }

    fun getDeviceMAC(callback: (version: Map<String,Any>) -> Unit) {
        orisLog.d(TAG, "get mate you device version ")
        mPrimaryService?.getCharacteristic(SEND_UUID)?.let {
            it.value = StringUtils.hexToByteArray("FA0000")
            addCallback(ResponseProtocol(0xFA),callback)
            write(it)
        }
    }

    fun getDeviceOSVersion(callback: (version: Map<String,Any>) -> Unit) {
        orisLog.d(TAG, "get mate you device version ")
        mPrimaryService?.getCharacteristic(SEND_UUID)?.let {
            it.value = StringUtils.hexToByteArray("FB0000")
            addCallback(ResponseProtocol(0xFB),callback)
            write(it)
        }
    }

    fun setDeviceTime(cal: Calendar,callback: (version: Map<String,Any>) -> Unit) {
        orisLog.d(TAG, "set mate you device time ")
        mPrimaryService?.getCharacteristic(SEND_UUID)?.let {
            var protocol = TimeSetProtocol().apply {
                mCalendar = cal
            }
            it.value = protocol.value
            addCallback(protocol,callback)
            write(it)
        }
    }

    fun setDeviceSettings(callback: (version: Map<String,Any>) -> Unit){
        mPrimaryService?.getCharacteristic(SEND_UUID)?.let {
            var protocol = SettingsProtocol()
            it.value = protocol.value
            addCallback(protocol,callback)
            write(it)
        }
    }

    fun getDeviceTime(callback: (version: Map<String,Any>) -> Unit) {
        orisLog.d(TAG, "get mate you device time ")
        mPrimaryService?.getCharacteristic(SEND_UUID)?.let {
            var protocol =
                TimeGetProtocol()
            it.value = protocol.value
            mCallbackDispatcher[protocol.respCode] = callback
            write(it)
        }
    }

    fun startMeasureHeartRate(checkCount:Int,callback: (data: Map<String,Any>) -> Unit) {
        mPrimaryService?.getCharacteristic(SEND_UUID)?.let {
            orisLog.d(TAG, "start Sensor measure HeartRate")
            var protocol = SensorMeasureProtocol()
            protocol.type = SensorAbility.CMD_HEART_RATE
            protocol.status = SensorAbility.CMD_START
            protocol.sync = SensorAbility.CMD_SYNC
            protocol.checkCount = checkCount
            it.value = protocol.value
            addCallback(protocol, callback)
            write(it)
        }
    }

    fun stopMeasureHeartRate() {
        mPrimaryService?.getCharacteristic(SEND_UUID)?.let {
            orisLog.d(TAG, "start Sensor measure HeartRate")
            var protocol = SensorMeasureProtocol()
            protocol.type = SensorAbility.CMD_HEART_RATE
            protocol.status = SensorAbility.CMD_STOP
            protocol.sync = SensorAbility.CMD_STOP_SYNC
            protocol.checkCount = 1
            it.value = protocol.value
            removeCallback(protocol.respCode)
            write(it)
        }
    }

    fun startMeasureBloodPressuresSync(count: Int, callback: (data: Map<String,Any>) -> Unit) {
        orisLog.d(TAG, "start Sensor measure blood pressures")
        mPrimaryService?.getCharacteristic(SEND_UUID)?.let {
            var protocol =
                SensorMeasureProtocol()
            protocol.type = SensorAbility.CMD_BLOOD_PRESSURE
            protocol.status = SensorAbility.CMD_START
            protocol.sync = SensorAbility.CMD_SYNC
            protocol.checkCount = count
            it.value = protocol.value
            addCallback(protocol,callback)
            write(it)
        }
    }

    fun syncDeviceSportHistory(cal: Calendar,callback: (data: Map<String,Any>) -> Unit){
        orisLog.d(TAG, "start sync device sport history data")
        mPrimaryService?.getCharacteristic(SEND_UUID)?.let {
            var protocol = HistortySportProtocol().apply {
                mCalendar = cal
            }
            it.value = protocol.value
            addCallback(protocol,callback)
            write(it)
        }

    }

    fun getStepCount(callback: (version: Map<String,Any>) -> Unit) {
        orisLog.d(TAG, "get sensor total step ")
        mPrimaryService?.getCharacteristic(SEND_UUID)?.let {
            var protocol = SensorCountStepProtocol()
            it.value = protocol.value
            addCallback(protocol,callback)
            write(it)
        }
    }
}

