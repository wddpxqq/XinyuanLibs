package com.originstar.access.ble

import android.bluetooth.*
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.originstar.orislog.orisLog
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

private const val STATUS_DISCONNECTED = 0x0001
private const val STATUS_DESTORYED = 0x0002
private const val STATUS_CONNECT_FAILED = 0x0003
private const val MASK_INVALID_STATUS = 0xFF00

private const val STATUS_CONNECTING = 0x0100
private const val STATUS_CONNECTED = 0x0200
private const val MASK_VALID_STATUS = 0x00FF

private const val TAG = "BleDevice"

abstract class BleDevice<T>(val device: BluetoothDevice) : BluetoothGattCallback(){

    private var mGatt:BluetoothGatt?=null
    private var SHOW_CHARAC_DEBUG = true
    private var mGattServices :HashMap<UUID,BluetoothGattService>  = HashMap<UUID,BluetoothGattService>()
    private var mNotifyCallback :ArrayList<(data:T)->Unit>   = ArrayList<(data:T)->Unit>()
    private var mDeviceStatus = STATUS_DISCONNECTED
    private val mWritCharacteristicPool = LinkedBlockingQueue<BluetoothGattCharacteristic>()
    abstract fun convert(data:ByteArray):T

    fun connect(context: Context): BluetoothGatt? {
        if(mDeviceStatus and MASK_INVALID_STATUS == 0) {
            orisLog.d(TAG, "devices start connect")
            release()
            mGatt = device.connectGatt(context, true, BleDevice@this)
            mDeviceStatus = STATUS_CONNECTING
        }else{
            orisLog.d(TAG, "devices connect state is  $mDeviceStatus")
        }
        return mGatt
    }

    private fun disconnect(){
        if(mDeviceStatus and MASK_VALID_STATUS == 0) {
            orisLog.d(TAG, "devices disconnect")
            mGatt?.disconnect()
            mDeviceStatus = STATUS_DISCONNECTED
        }
    }

    fun release() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            mGatt?.close()
            mGatt = null
            mDeviceStatus = STATUS_DISCONNECTED
        } else {
            Handler(Looper.getMainLooper()).post {
                mGatt?.close()
                mGatt = null
                mDeviceStatus = STATUS_DISCONNECTED
            }
        }
    }

    open fun reConnect(mContext: Context) {
        connect(mContext)
    }

    fun destroy() {
        orisLog.d(TAG, "destroy the connected $mDeviceStatus")
        if (mDeviceStatus and MASK_VALID_STATUS == 0) {
            orisLog.d(TAG, "destroy the connected")
            mGatt?.close()
            mGatt = null
            mDeviceStatus = STATUS_DESTORYED
        } else {
            orisLog.d(TAG, "destroy the connected status incorrect")
        }
    }

    abstract fun onDevicesConnected(it: BluetoothGatt)

    abstract fun onDevicesDisconnected()

    abstract fun onServicesDiscovered()

    protected fun registerOnCharacteristicChangedCallback(callback:(data:T)->Unit){
        mNotifyCallback.add (callback)
    }

    fun write(characteristic: BluetoothGattCharacteristic):Boolean {
        orisLog.d(TAG, "write-chara ${StringUtils.toHexString(characteristic.value)}")
        return mGatt?.writeCharacteristic(characteristic)?: false
    }

    fun read(characteristic: BluetoothGattCharacteristic):Boolean{
       return mGatt?.readCharacteristic(characteristic)?: false
    }

    fun setNotify(characteristic: BluetoothGattCharacteristic, enable:Boolean):Boolean{
        var isEnableNotification = mGatt?.setCharacteristicNotification(characteristic,enable)?:false
        if (isEnableNotification) {
            characteristic.descriptors?.forEach {
                it.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                mGatt?.writeDescriptor(it)
            }
        }
        return isEnableNotification
    }

    fun getServices(uuid: UUID):BluetoothGattService?{
        return mGattServices[uuid]
    }

    override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
        super.onConnectionStateChange(gatt, status, newState)
        orisLog.d(TAG, "onConnectionStateChange status:$status newState:$newState")
        if (BluetoothGatt.GATT_SUCCESS == status) {
            // CQ :status 表示相应的连接或断开操作是否完成，而不是指连接状态
            if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                mDeviceStatus = STATUS_DISCONNECTED
                orisLog.d(TAG, "onConnectionStateChange disconnected")
                onDevicesDisconnected()
            } else if (newState == BluetoothGatt.STATE_CONNECTED) {
                orisLog.d(TAG, "onConnectionStateChange connected")
                mDeviceStatus = STATUS_CONNECTED
                gatt?.let {
                    onDevicesConnected(it)
                }
            }
        } else {
            orisLog.d(TAG, "onConnectionStateChange status failed")
            release()
            onDevicesDisconnected()
        }
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
        orisLog.d(TAG, "onServicesDiscovered status $status discovered services")
        if (status == BluetoothGatt.GATT_SUCCESS) {
            gatt?.services?.let { its ->
                for (item in its.iterator()){
                    orisLog.d(TAG, "services status ${item.uuid}")
                    mGattServices[item.uuid] = item
                    mDeviceStatus = STATUS_CONNECTED
                }
            }
            onServicesDiscovered()
        }else{
            mDeviceStatus = STATUS_CONNECT_FAILED
        }
    }

    override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
        if(BluetoothGatt.GATT_SUCCESS == status){
            orisLog.d(TAG,  "on-chara-read value=${StringUtils.toHexString(characteristic?.value)} success")
        }else{
            orisLog.d(TAG,  "on-chara-read value=${StringUtils.toHexString(characteristic?.value)} failed")
        }
    }

    override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
        if(BluetoothGatt.GATT_SUCCESS == status){
            orisLog.d(TAG,  "on-chara-write value=${StringUtils.toHexString(characteristic?.value)} success")
        }else{
            orisLog.d(TAG,  "on-chara-write value=${StringUtils.toHexString(characteristic?.value)} failed")
        }
    }

    @Synchronized
    override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
        orisLog.d(TAG, "onCharacteristicChanged value=${StringUtils.toHexString(characteristic?.value)}")
        characteristic?.value?.let {
            mNotifyCallback.forEach { callback ->
                callback?.let {
                    it.invoke(convert(characteristic.value))
                }
            }
        }
    }

    fun close() {
        orisLog.d(TAG, "device close connection")
        mGatt?.close()
        mDeviceStatus = STATUS_DISCONNECTED
    }
}