package com.phivantuan.sigmatest

import android.content.Context
import android.content.Context.BATTERY_SERVICE
import android.os.BatteryManager
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

class GetBatteryWorker(private val context: Context, parameters: WorkerParameters) :
    Worker(context, parameters) {
    override fun doWork(): Result {
        UserData.battery = getBatteryPercentage(context)
        return Result.success()
    }

    private fun getBatteryPercentage(context: Context): String {
        val bm = context.getSystemService(BATTERY_SERVICE) as BatteryManager
        val battery = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        Log.d("TAG", "Battery : $battery")
        return "$battery"
    }
}