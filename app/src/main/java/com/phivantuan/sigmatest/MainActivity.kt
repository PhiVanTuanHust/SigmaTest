package com.phivantuan.sigmatest

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    companion object {
        const val START_STATUS = 1
        const val STOP_STATUS = 0
        const val LOCATION_TAG = "LOCATION_WORK_TAG"
        const val BATTERY_TAG = "BATTERY_WORK_TAG"
        const val LOCATION_PERIOD = 6L
        const val BATTERY_PERIOD = 9L
    }

    private lateinit var viewModel: MainViewModel
    private var workManager: WorkManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        workManager = WorkManager.getInstance(this)
        btnStart.setOnClickListener { viewModel.status.postValue(START_STATUS) }
        btnStop.setOnClickListener { viewModel.status.postValue(STOP_STATUS) }

        viewModel.status.observe(this) {
            when (it) {
                START_STATUS -> {
                    start()
                    btnStart.isEnabled = false
                    btnStop.isEnabled = true
                }
                STOP_STATUS -> {
                    stop()
                    btnStart.isEnabled = true
                    btnStop.isEnabled = false

                }
            }
        }

        observeLstData()

    }

    private fun stop() {
        viewModel.refreshData()
        workManager?.cancelAllWork()
    }

    private fun start() {
        if (checkPermissions()) {
            val getLocationWork = PeriodicWorkRequestBuilder<GetLocationWorker>(
                LOCATION_PERIOD,
                TimeUnit.MINUTES,
            )
                .addTag(LOCATION_TAG)
                .build()

            val getBatteryWork = PeriodicWorkRequestBuilder<GetBatteryWorker>(
                BATTERY_PERIOD, TimeUnit.MINUTES
            )
                .addTag(BATTERY_TAG)
                .build()

            workManager?.enqueueUniquePeriodicWork(
                LOCATION_TAG,
                ExistingPeriodicWorkPolicy.REPLACE,
                getLocationWork
            )

            workManager?.enqueueUniquePeriodicWork(
                BATTERY_TAG,
                ExistingPeriodicWorkPolicy.REPLACE,
                getBatteryWork
            )

            workManager?.getWorkInfosByTagLiveData(LOCATION_TAG)?.observe(this) { workInfo ->
                if (!workInfo.isNullOrEmpty() && workInfo[0].state == WorkInfo.State.ENQUEUED) {
                    viewModel.updateData(UserData.location)
                }
            }

            workManager?.getWorkInfosByTagLiveData(BATTERY_TAG)?.observe(this) { workInfo ->
                if (!workInfo.isNullOrEmpty() && workInfo[0].state == WorkInfo.State.ENQUEUED) {
                    viewModel.updateData(UserData.battery)
                }
            }
        } else {
            requestPermissions()
        }
    }

    private fun checkPermissions(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            LocationUtility.REQUEST_CHECK_SETTINGS
        )
    }

    private fun observeLstData() {
        viewModel.lstData.observe(this) {
            if (it.size > 5) {
                val data: Data = Data.Builder()
                    .putString("data", it.joinToString())
                    .build()
                val callApi = OneTimeWorkRequestBuilder<CallApiWorker>().setInputData(data).build()
                workManager?.enqueue(callApi)
                workManager?.getWorkInfoByIdLiveData(callApi.id)?.observe(this) {
                    if (it.state.isFinished) {
                        viewModel.status.postValue(STOP_STATUS)
                        viewModel.refreshData()
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LocationUtility.REQUEST_CHECK_SETTINGS) {
            start()
        }
    }
}