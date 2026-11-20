package org.sopt.sopkathon_android2.data.di

import org.sopt.sopkathon_android2.data.di.NetworkModule.instance
import org.sopt.sopkathon_android2.data.service.ApiService

object ServiceModule {
    val apiService: ApiService by lazy { instance.create(ApiService::class.java) }
}
