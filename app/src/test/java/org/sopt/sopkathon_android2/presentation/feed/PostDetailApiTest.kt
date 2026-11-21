package org.sopt.sopkathon_android2.presentation.feed

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Test
import org.sopt.sopkathon_android2.data.dto.request.CommentCreateRequestDto
import org.sopt.sopkathon_android2.data.dto.response.PostDetailResponseDto
import org.sopt.sopkathon_android2.data.service.PostDetailApiService
import org.sopt.sopkathon_android2.presentation.postdetail.model.toUiModel
import org.sopt.sopkathon_android2.presentation.postdetail.model.toWebUrlOrNull
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class PostDetailApiTest {
    @Test
    fun mapsKnownItemCategoriesWithoutInventingUnknownMarker() {
        val dto = Json { ignoreUnknownKeys = true }.decodeFromString<PostDetailResponseDto>("""{
            "postId":501,"author":{"userId":2,"name":"작성자","position":"대리"},
            "targetUser":{"userId":1,"name":"대상자","position":"과장"},
            "imageUrl":"image.png","title":"코디","description":null,"liked":true,
            "likeCount":18,"commentCount":3,"createdAt":"2026-11-22T10:00:00Z",
            "outfitItems":[{"clothesId":1,"name":"상의","category":"TOP","imageUrl":null,"purchaseUrl":"https://example.com"},
                {"clothesId":2,"name":"신발","category":"SHOES","imageUrl":null,"purchaseUrl":"javascript:alert(1)"}],
            "purchaseLinks":[{"url":"https://example.com"},{"url":"file:///private/file"}]
        }""")
        val model = dto.toUiModel()
        assertTrue(model.post.isLiked)
        assertEquals("", model.post.content)
        assertNotNull(model.items.first().type)
        assertNull(model.items.last().type)
        assertNull(model.items.last().purchaseUrl)
        assertEquals(listOf("https://example.com"), model.purchaseUrls)
        assertNull("https:///missing-host".toWebUrlOrNull())
    }

    @Test
    fun commentsUseCursorHeaderAndJsonBody() = runBlocking {
        val methods = mutableListOf<String>()
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            val request = chain.request()
            methods += request.method
            assertEquals("/api/v1/users/1/posts/501/comments", request.url.encodedPath)
            assertEquals("1", request.header("X-User-Id"))
            val data = if (request.method == "GET") {
                assertEquals("cursor+/=", request.url.queryParameter("cursor"))
                """{"items":[],"nextCursor":null,"hasMore":false}"""
            } else {
                val buffer = okio.Buffer()
                request.body!!.writeTo(buffer)
                assertEquals("{\"content\":\"댓글\"}", buffer.readUtf8())
                """{"commentId":7,"postId":501,"content":"댓글","createdAt":"2026-11-22T10:00:00Z","commentCount":4}"""
            }
            Response.Builder().request(request).protocol(Protocol.HTTP_1_1).code(200).message("OK")
                .body("""{"status":200,"code":"OK","message":"ok","data":$data}""".toResponseBody("application/json".toMediaType())).build()
        }.build()
        val api = Retrofit.Builder().baseUrl("https://example.com/").client(client)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType())).build().create(PostDetailApiService::class.java)
        assertFalse(api.getPostComments(1, 501, "cursor+/=").data!!.hasMore)
        assertEquals(4, api.postPostComment(1, 501, CommentCreateRequestDto("댓글")).data!!.commentCount)
        assertEquals(listOf("GET", "POST"), methods)
    }
}
