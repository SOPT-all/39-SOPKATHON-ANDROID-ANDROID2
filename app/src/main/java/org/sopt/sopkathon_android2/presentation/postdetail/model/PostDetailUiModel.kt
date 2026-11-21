package org.sopt.sopkathon_android2.presentation.postdetail.model

import org.sopt.sopkathon_android2.core.designsystem.component.post.OutfitItemType
import org.sopt.sopkathon_android2.data.dto.response.CommentResponseDto
import org.sopt.sopkathon_android2.data.dto.response.FeedPostResponseDto
import org.sopt.sopkathon_android2.data.dto.response.PostDetailResponseDto
import org.sopt.sopkathon_android2.presentation.feed.model.FeedPostUiModel
import org.sopt.sopkathon_android2.presentation.feed.model.toUiModel
import org.sopt.sopkathon_android2.data.mapper.normalizePostTimestamp
import java.net.URI
import java.util.Locale

data class OutfitItemUiModel(
    val clothesId: Long,
    val name: String,
    val category: String,
    val imageUrl: String,
    val purchaseUrl: String?,
    val type: OutfitItemType?,
    val brand: String = "",
    val priceLabel: String = "",
    val attributes: List<Pair<String, String>> = emptyList(),
)

data class CommentUiModel(
    val commentId: Long,
    val userId: Long,
    val name: String,
    val position: String,
    val content: String,
    val createdAt: String,
)

data class PostDetailUiModel(
    val post: FeedPostUiModel,
    val targetUserId: Long,
    val items: List<OutfitItemUiModel>,
    val purchaseUrls: List<String>,
)

fun PostDetailResponseDto.toUiModel() = PostDetailUiModel(
    post = FeedPostResponseDto(
        postId, author, targetUser, imageUrl, title, description.orEmpty(),
        likeCount, liked, commentCount, createdAt,
    ).toUiModel(),
    targetUserId = targetUser.userId,
    items = DummyPostOutfitItems.items,
    purchaseUrls = purchaseLinks.mapNotNull { it.url.toWebUrlOrNull() }.distinct(),
)

fun CommentResponseDto.toUiModel() = CommentUiModel(
    commentId, author.userId, author.name.trim(), author.position.trim(), content, createdAt.normalizePostTimestamp(),
)

fun String?.toWebUrlOrNull(): String? = this?.trim()?.takeIf { value ->
    runCatching { URI(value) }.getOrNull()?.let {
        it.scheme?.lowercase(Locale.ROOT) in setOf("http", "https") && !it.host.isNullOrBlank()
    } == true
}
