package com.prince.wally.model.remote

import com.prince.wally.BuildConfig.PIXABAY_KEY
import com.prince.wally.model.remote.response.pixabay.Pic
import com.prince.wally.model.remote.response.unsplash.UnsplashResponse
import com.prince.wally.model.remote.response.videos.VideoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {

    @GET("?key=$PIXABAY_KEY")
    suspend fun getPixabayPictures(
        @Query("q") query: String,
        @Query("page") index: Int
    ): Response<Pic>

    @GET
    suspend fun getUnsplashPictures(
        @Url url: String,
        @Query("query") query: String,
        @Query("page") page: Int
    ): Response<UnsplashResponse>

    @GET
    suspend fun getVideos(
        @Url url: String,
        @Query("playlistId") playlistId: String
    ): Response<VideoResponse>