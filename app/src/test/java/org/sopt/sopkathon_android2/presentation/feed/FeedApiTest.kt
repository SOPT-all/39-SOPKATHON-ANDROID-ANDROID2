package org.sopt.sopkathon_android2.presentation.feed

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.sopt.sopkathon_android2.data.service.ApiService
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class FeedApiTest {
    @Test
    fun sendsPathUserIdAndOpaqueCursorWithoutDuplicateQuery() = runBlocking {
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            val url = chain.request().url
            assertEquals("/api/v1/users/42/feed", url.encodedPath)
            assertEquals("LATEST", url.queryParameter("sort"))
            assertEquals("a+/= cursor", url.queryParameter("cursor"))
            assertNull(url.queryParameter("period"))
            assertNull(url.queryParameter("userId"))
            Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body("""{"status":200,"code":"FEED_RETRIEVED","message":"ok","data":{"items":[],"nextCursor":null,"hasMore":false}}""".toResponseBody("application/json".toMediaType()))
                .build()
        }.build()
        val api = Retrofit.Builder()
            .baseUrl("https://example.com/")
            .client(client)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ApiService::class.java)
        val response = api.getFeed(userId = 42, cursor = "a+/= cursor")
        assertEquals(false, response.data?.hasMore)
    }
}
