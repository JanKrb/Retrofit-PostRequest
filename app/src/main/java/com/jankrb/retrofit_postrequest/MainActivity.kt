package com.jankrb.retrofit_postrequest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.*
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.POST
import retrofit2.http.Query


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val service = RetrofitFactory.makeRetrofitService() // Retrofit service
        CoroutineScope(Dispatchers.IO).launch { // Coroutine to load sync
            service.savePost("Param 1", "Param 2").enqueue(object: Callback<PostRequest> {

                override fun onResponse(call:Call<PostRequest>, response:Response<PostRequest>) {
                    Log.i("Success", response.body().toString())
                }

                override fun onFailure(call:Call<PostRequest>, t:Throwable) {
                    Log.d("Failure", "Failed")
                }
            })
        }
    }

    class PostRequest {
        @SerializedName("param1")
        @Expose
        private val param1: String? = null

        @SerializedName("param2")
        @Expose
        private val param2: String? = null
    }

    /**
     * Service to register Response name and type
     */
    interface RetrofitService {
        @POST("/post") // Type and route of api call
        fun savePost(@Query("param1") param1: String,
                     @Query("param2") param2: String): Call<PostRequest>
    }

    /**
     * Factory to create requests
     */
    object RetrofitFactory {
        private const val BASE_URL = "http://127.0.0.1:5000" // URL of the api (Without route or tail slash)

        fun makeRetrofitService(): RetrofitService { // Function to create Retrofit
            return Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build().create(RetrofitService::class.java)
        }
    }
}