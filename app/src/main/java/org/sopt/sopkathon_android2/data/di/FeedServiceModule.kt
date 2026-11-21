package org.sopt.sopkathon_android2.data.di

import org.sopt.sopkathon_android2.data.service.FeedApiService

object FeedServiceModule {
    val apiService: FeedApiService by lazy {
        NetworkModule.instance.create(FeedApiService::class.java)
    }
}
