package org.sopt.sopkathon_android2.data.di

import org.sopt.sopkathon_android2.data.service.PostDetailApiService

object PostDetailServiceModule {
    val apiService: PostDetailApiService by lazy {
        NetworkModule.instance.create(PostDetailApiService::class.java)
    }
}
