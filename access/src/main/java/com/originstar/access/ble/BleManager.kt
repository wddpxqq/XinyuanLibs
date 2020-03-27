package com.originstar.access.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.core.content.ContextCompat
import com.originstar.orislog.orisLog


private const val NONE = 0x0000
private const val DISCOVERING = 0x0001
private const val START_DISCOVERY = 0x0002
private const val DEVICES_DISCOVERD = 0x0003
private const val STOP = 0x0004

private const val TAG = "BleManager"
class BleManager {

    private lateinit var supportDevices:List<String>
    private var mState:Int = NONE
    private var mContext: Context? = null
    private var mDiscoveredListener: OnDeviceDiscoveredListener?=null
    private var mBluetoothLeScanner: BluetoothLeScanner? = null
    private var mBluetoothManager: BluetoothManager? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null

    private object SingletonHolder {
        val holder = BleManager()
    }

    companion object {
        val instance = SingletonHolder.holder
        fun init(context: Context, deviceName:List<String>) {
            instance.supportDevices = deviceName
            instance.mContext = context
            instance.mBluetoothManager = ContextCompat.getSystemService(context,BluetoothManager::class.java)
            instance.mBluetoothAdapter = instance.mBluetoothManager?.adapter
            instance.registerReceiver()
        }

        fun startDiscovery() {
            orisLog.d(TAG, "startDiscovery")
            instance.mState = START_DISCOVERY
            if(instance.checkEnvironment()){
                orisLog.d(TAG, "Environment ok start discovery")
                instance.discovery()
            }else{
                orisLog.d(TAG, "Environment is not enable start discovery")
            }
        }

        fun registerOnDeviceDiscovery(listener: OnDeviceDiscoveredListener) {
            instance.mDiscoveredListener = listener
        }

        fun onDestroy() {
            instance.onDestroy()
        }

        fun onResume() {
            instance.onResume()
        }

        fun onStop() {
            instance.stop()
        }
    }

    private fun stop() {
        orisLog.d(TAG, "ble stop")
        mBluetoothAdapter?.cancelDiscovery()
        mBluetoothLeScanner?.stopScan(mBluetoothLeScannerCallback)
        mState = STOP
    }

    fun onDestroy() {
        orisLog.i(TAG,"OnLifecycleEvent onDestroy")
        instance.destroy()
        mState = NONE
    }

    fun onResume() {
        orisLog.i(TAG, "OnLifecycleEvent ON_RESUME")
        if (mState == START_DISCOVERY && checkEnvironment()) {
            discovery()
        }
    }

    fun checkEnvironment(): Boolean {
        if (mContext == null) return false
        mBluetoothAdapter?.let {
            if (!it.isEnabled) {
                mContext!!.startActivity(Intent().apply {
                    action = BluetoothAdapter.ACTION_REQUEST_ENABLE
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
                return false
            }else{
                mBluetoothLeScanner = instance.mBluetoothAdapter?.bluetoothLeScanner
            }
        }
        return true
    }

    interface OnDeviceDiscoveredListener{
        fun deviceDiscovered(device:BluetoothDevice)
        fun onScanFailed(errorCode: Int)
    }

    private fun discovery() {
        orisLog.i(TAG, "BleManager start discovery adapter = ${mBluetoothAdapter!=null}")
        mBluetoothAdapter?.let {
            if (it.isDiscovering) {
                mBluetoothLeScanner?.stopScan(mBluetoothLeScannerCallback)
            }
            var mScannerSetting = ScanSettings.Builder() //退到后台时设置扫描模式为低功耗
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .build()
            //过滤扫描蓝牙设备的主服务
            var mFilters = ArrayList<ScanFilter>()
            supportDevices.forEach { deviceName ->
                mFilters.add(ScanFilter.Builder().setDeviceName(deviceName).build())
            }
            mBluetoothLeScanner?.startScan(mFilters, mScannerSetting, mBluetoothLeScannerCallback)
            mState = DISCOVERING
        }
    }
    private fun destroy() {
        stop()
        mDiscoveredListener = null
        mContext?.let {
            orisLog.i(TAG,"unregisterReceiver")
            it.unregisterReceiver(mScanBluetoothReceiver)
        }
    }

    fun registerReceiver() {
        mContext?.registerReceiver(mScanBluetoothReceiver, IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        })
    }

    private var mBluetoothLeScannerCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.let {
                it.device?.let { itc ->
                    instance.mState = DEVICES_DISCOVERD
                    mDiscoveredListener?.deviceDiscovered(itc)
                    Log.d(TAG,"onScanResult  device:${itc.name} mac:${itc.address} uuid:device:${itc.uuids}}")
                }
            }
        }
        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            results?.let {
                for (result in it) {
                    result.device?.let { itc ->
                        Log.d(TAG,"onBatchScanResults  device:${itc.name} mac:${itc.address} uuid:device:${itc.uuids}}")
                        mDiscoveredListener?.deviceDiscovered(itc)
                    }
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.d(TAG, "onScanFailed  errorCode:$errorCode")
            mBluetoothAdapter?.cancelDiscovery()
            mBluetoothLeScanner?.stopScan(this)
            instance.mState = STOP
            mDiscoveredListener?.onScanFailed(errorCode)
        }
    }

    private var mScanBluetoothReceiver= object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE) as BluetoothDevice
                    device?.let {
                        Log.d(TAG ,"Receiver find device:${it.name} mac:${it.address} uuid:device:${it.uuids}}")
                        mDiscoveredListener?.deviceDiscovered(it)
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    Log.i(TAG, "ACTION_DISCOVERY_STARTED $intent}")
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    Log.i(TAG, "ACTION_DISCOVERY_FINISHED $intent")
                }

                BluetoothDevice.ACTION_UUID -> {
                    Log.i(TAG, "ACTION_UUID ${intent.getStringExtra(BluetoothDevice.EXTRA_UUID)}")
                }

                else -> {
                    Log.i(TAG, "Receiver Other Action ${intent.action}")
                }
            }
        }
    }
}
