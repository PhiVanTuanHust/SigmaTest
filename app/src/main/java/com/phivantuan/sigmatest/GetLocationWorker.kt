package com.phivantuan.sigmatest

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

class GetLocationWorker(private val context: Context, parameters: WorkerParameters) :
    Worker(context, parameters) {
    private var locationUtility: LocationUtility = LocationUtility(context)
    init {
        if (locationUtility.checkPermissions()) {
            locationUtility.startLocationClient()
        }
    }

    override fun doWork(): Result {
        val location = locationUtility.getLocation(context)
        Log.d("TAG", "Location :${location}")
        UserData.location = location
        return Result.success()
    }
}