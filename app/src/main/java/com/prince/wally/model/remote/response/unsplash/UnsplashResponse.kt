package com.prince.wally.model.remote.response.unsplash

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UnsplashResponse {
    @SerializedName("results")
    @Expose
    var results: List<Result>? = null
}