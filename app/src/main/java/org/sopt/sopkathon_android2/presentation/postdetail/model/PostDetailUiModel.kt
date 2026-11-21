package org.sopt.sopkathon_android2.presentation.postdetail.model

import org.sopt.sopkathon_android2.core.designsystem.component.post.OutfitItemType
import org.sopt.sopkathon_android2.data.dto.response.CommentResponseDto
import org.sopt.sopkathon_android2.data.dto.response.FeedPostResponseDto
import org.sopt.sopkathon_android2.data.dto.response.OutfitItemResponseDto
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
    items = outfitItems.distinctBy { it.clothesId }.map { it.toUiModel() },
    purchaseUrls = purchaseLinks.mapNotNull { it.url.toWebUrlOrNull() }.distinct(),
)

// 서버 API에 브랜드, 가격이 없어 고정 값
private const val OUTFIT_ITEM_BRAND = "UNIQLO"

private fun OutfitItemResponseDto.toUiModel(): OutfitItemUiModel {
    val type = when (category.trim().uppercase(Locale.ROOT)) {
        "TOP" -> OutfitItemType.TOP
        "BOTTOM" -> OutfitItemType.BOTTOM
        else -> null
    }
    return OutfitItemUiModel(
        clothesId = clothesId,
        name = name,
        category = when (type) {
            OutfitItemType.TOP -> "상의"
            OutfitItemType.BOTTOM -> "하의"
            null -> category
        },
        imageUrl = imageUrl.orEmpty(),
        purchaseUrl = purchaseUrl.toWebUrlOrNull(),
        type = type,
        brand = OUTFIT_ITEM_BRAND,
        priceLabel = when (type) {
            OutfitItemType.TOP -> "49,900원"
            OutfitItemType.BOTTOM -> "39,900원"
            null -> ""
        },
    )
}

fun CommentResponseDto.toUiModel() = CommentUiModel(
    commentId, author.userId, author.name.trim(), author.position.trim(), content, createdAt.normalizePostTimestamp(),
)

fun String?.toWebUrlOrNull(): String? = this?.trim()?.takeIf { value ->
    runCatching { URI(value) }.getOrNull()?.let {
        it.scheme?.lowercase(Locale.ROOT) in setOf("http", "https") && !it.host.isNullOrBlank()
    } == true
}
