package com.hmy.spotify.network

import com.hmy.spotify.datamodel.Section
import retrofit2.Call
import retrofit2.http.GET

// retrofit帮忙做了Implementation，省去了实现把JSON变成API call of List of Section
// List of Section是response，包裹住Call这个Wrapper里面
// Call相当于一个指令，本质上是一个还没有执行的任务，这个任务执行成功后会拿到一个List of Section
// 执行这个任务会在MainActivity里面execute他
// get restful call
interface NetworkApi {
    @GET("feed")
    fun getHomeFeed(): Call<List<Section>>
}