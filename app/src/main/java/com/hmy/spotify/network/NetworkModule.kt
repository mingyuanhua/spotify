package com.hmy.spotify.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


// instead of Jackson we used Gson, they are similar

// Module是我们拿依赖的工厂，可以provide retrofit
// provide singleton代表了整个App里只能有一份retrofit

// InstallIn

// 定义了工厂如何provide retrofit，也可以定义NetworkApi

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient())
            .build()
    }

    // 这里的retrofit已经被上面这个function提供了
    // hilt call的时候发现retrofit可以从上面拿
    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): NetworkApi {
        return retrofit.create(NetworkApi::class.java)
    }
}