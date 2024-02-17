package com.example.epsonkotlin

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.epson.epos2.discovery.Discovery
import com.epson.epos2.discovery.DiscoveryListener
import com.epson.epos2.discovery.FilterOption
import com.example.epsonkotlin.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private val REQUEST_PERMISSION = 100
    private lateinit var binding: ActivityMainBinding
    var printer : com.epson.epos2.printer.Printer? = null
    private val TAG = "myTag"
    private lateinit var filerOption : FilterOption
    private lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mContext = this

        requestRuntimePermission()

        binding.btn.setOnClickListener {
            searchForPrinters()
        }

    }

    private fun searchForPrinters() {
        filerOption = FilterOption()

        filerOption.portType = Discovery.PORTTYPE_ALL
        filerOption.broadcast = "255.255.255.255"
        filerOption.deviceModel = Discovery.MODEL_ALL
        filerOption.deviceType = Discovery.TYPE_ALL

        try {
            Discovery.start(mContext, filerOption, mDiscoveryListener)
            Log.d(TAG, "Discovery started")
            Log.d(TAG, filerOption.bondedDevices.toString())
        } catch (e : Exception) {
            Log.d(TAG, e.message+" ")
        }
    }

    private val mDiscoveryListener = DiscoveryListener { deviceInfo ->
        runOnUiThread {
            Log.d(TAG, deviceInfo.deviceName+" "
                    + deviceInfo.macAddress)
        }
    }


    private fun requestRuntimePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }
        val permissionStorage =
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permissionLocation =
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
        val requestPermissions: MutableList<String> = ArrayList()
        if (permissionStorage == PackageManager.PERMISSION_DENIED) {
            requestPermissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (permissionLocation == PackageManager.PERMISSION_DENIED) {
            requestPermissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (!requestPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                requestPermissions.toTypedArray<String>(),
                REQUEST_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != REQUEST_PERMISSION || grantResults.isEmpty()) {
            return
        }
        val requestPermissions: MutableList<String> = ArrayList()
        for (i in permissions.indices) {
            if (permissions[i] == android.Manifest.permission.WRITE_EXTERNAL_STORAGE && grantResults[i] == PackageManager.PERMISSION_DENIED) {
                requestPermissions.add(permissions[i])
            }
            if (permissions[i] == android.Manifest.permission.ACCESS_COARSE_LOCATION && grantResults[i] == PackageManager.PERMISSION_DENIED) {
                requestPermissions.add(permissions[i])
            }
        }
        if (!requestPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                requestPermissions.toTypedArray<String>(),
                REQUEST_PERMISSION
            )
        }
    }

}