package com.mochamadahya.angleres.videostreamer

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiHelperInterface {
    @GET("video.json")
    fun videoData(): Call<ArrayList<VideoModel>>

    companion object {
        fun create(): ApiHelperInterface {
            val retrofit = Retrofit
                    .Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(ApiUrlHelper.videoUrl)
                    .build()
            return retrofit.create(ApiHelperInterface::class.java)
        }
    }
}