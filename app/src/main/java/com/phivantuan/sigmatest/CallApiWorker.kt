package com.phivantuan.sigmatest

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CallApiWorker(context: Context, param: WorkerParameters) : CoroutineWorker(context, param) {
    override suspend fun doWork(): Result {
        val inputData = inputData.getString("data") ?: ""
        withContext(Dispatchers.IO) {
            ApiClient.getApiService().sendData(inputData)
        }
        return Result.success()
    }
}