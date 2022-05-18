package com.phivantuan.sigmatest


import retrofit2.http.*

interface ApiService {
    @POST("/test")
    suspend fun sendData(@Part("test") data: String): Any
}