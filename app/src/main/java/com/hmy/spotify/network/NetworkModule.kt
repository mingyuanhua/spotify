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

// 只要App run了那他永远就是这个object 那么他就是singleton
// InstallIn

// 定义了工厂如何provide retrofit，也可以定义NetworkApi


// 如果我希望 API是跟着Activity走的话 可以InstallIn ActivityComponent
// 现在就不是Singleton了 而是ActivityScope
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