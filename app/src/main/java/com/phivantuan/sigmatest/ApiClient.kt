package com.phivantuan.sigmatest

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private var service: ApiService? = null
    private var retrofit: Retrofit? = null
    private const val DEFAULT_TIMEOUT = 10L
    private const val BASE_URL = "http://sigmansolutions.eu/"
    /**
     * Get Retrofit Instance
     */
    @Synchronized
    private fun getRetrofitInstance(): Retrofit {
        if (retrofit == null) {

            val buildClient =
                OkHttpClient.Builder()
            buildClient.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            buildClient.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(buildClient.build())
                .build()
        }

        return retrofit!!
    }

    @Synchronized
    fun getApiService(): ApiService {
        if (service == null) {
            service = getRetrofitInstance().create(ApiService::class.java)
        }
        return service!!
    }
}