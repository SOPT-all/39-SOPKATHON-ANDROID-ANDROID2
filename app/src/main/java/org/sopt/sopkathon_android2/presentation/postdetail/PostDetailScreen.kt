package org.sopt.sopkathon_android2.presentation.postdetail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import org.sopt.sopkathon_android2.R
import org.sopt.sopkathon_android2.core.common.extensions.noRippleClickable
import org.sopt.sopkathon_android2.core.designsystem.component.SopkathonSnackbarHost
import org.sopt.sopkathon_android2.core.designsystem.component.LocalSopkathonSnackbarState
import org.sopt.sopkathon_android2.core.designsystem.component.SopkathonSnackbarVisuals
import org.sopt.sopkathon_android2.core.designsystem.component.post.*
import org.sopt.sopkathon_android2.core.designsystem.preview.ScreenPreview
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme
import org.sopt.sopkathon_android2.presentation.feed.FeedUiState
import org.sopt.sopkathon_android2.presentation.postdetail.component.PostCommentsSheet
import org.sopt.sopkathon_android2.presentation.postdetail.component.PostItemSheet
import org.sopt.sopkathon_android2.presentation.postdetail.model.OutfitItemUiModel
import org.sopt.sopkathon_android2.presentation.postdetail.model.PostDetailUiModel
import org.sopt.sopkathon_android2.presentation.postdetail.model.toWebUrlOrNull

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailRoute(
    postId: Long,
    userId: Long,
    navigateUp: () -> Unit,
    onColleagueClick: (Long) -> Unit,
    onDressUpClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PostDetailViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbar = LocalSopkathonSnackbarState.current
    LaunchedEffect(userId, postId) { viewModel.load(userId, postId) }
    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is PostDetailUiEffect.ShowError -> snackbar.show(SopkathonSnackbarVisuals(message = effect.message))
            }
        }
    }
    val openShop: (String) -> Unit = { url ->
        val validUrl = url.toWebUrlOrNull()
        if (validUrl == null || runCatching {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(validUrl)))
            }.isFailure) {
            snackbar.show(SopkathonSnackbarVisuals(message = "쇼핑몰 링크를 열 수 없어요"))
        }
    }
    val share: () -> Unit = {
        uiState.detail?.post?.let { post ->
            runCatching {
                context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, listOf(post.title, post.content, post.imageUrl).filter { it.isNotBlank() }.joinToString("\n"))
                }, "게시글 공유"))
            }.onFailure { snackbar.show(SopkathonSnackbarVisuals(message = "공유 화면을 열 수 없어요")) }
        }
    }
    Box(modifier) {
        PostDetailScreen(
            uiState, navigateUp, onColleagueClick, viewModel::toggleLike, viewModel::openComments,
            viewModel::selectTag, viewModel::openItem, share, viewModel::retry,
        )
        if (uiState.sheet == null) SopkathonSnackbarHost(Modifier.align(Alignment.TopCenter).padding(top = 56.dp))
    }
    when (val sheet = uiState.sheet) {
        PostDetailSheet.Comments -> ModalBottomSheet(
            onDismissRequest = viewModel::dismissSheet,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = SopkathonTheme.colors.white,
            dragHandle = { BottomSheetDefaults.DragHandle(color = SopkathonTheme.colors.coolGray200) },
        ) {
            PostCommentsSheet(
                uiState, viewModel::updateDraft, viewModel::postComment,
                { viewModel.loadComments() }, viewModel::retryComments,
                { id -> viewModel.dismissSheet(); onColleagueClick(id) },
            )
        }
        is PostDetailSheet.Item -> {
            val item = uiState.detail?.items?.find { it.clothesId == sheet.clothesId }
            if (item != null) ModalBottomSheet(
                onDismissRequest = viewModel::dismissSheet,
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = SopkathonTheme.colors.white,
                dragHandle = { BottomSheetDefaults.DragHandle(color = SopkathonTheme.colors.coolGray200) },
            ) {
                PostItemSheet(
                    item,
                    onDressUpClick = {
                        uiState.detail?.targetUserId?.let { id -> viewModel.dismissSheet(); onDressUpClick(id) }
                    },
                    onShopClick = { item.purchaseUrl?.let(openShop) },
                )
            }
        }
        null -> Unit
    }
}

@Composable
private fun PostDetailScreen(
    uiState: PostDetailUiState,
    navigateUp: () -> Unit,
    onColleagueClick: (Long) -> Unit,
    onLikeClick: () -> Unit,
    onCommentsClick: () -> Unit,
    onTagClick: (OutfitItemType) -> Unit,
    onItemClick: (Long) -> Unit,
    onShareClick: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showMore by remember { mutableStateOf(false) }
    Column(modifier.fillMaxSize().background(SopkathonTheme.colors.white)) {
        Row(
            Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = navigateUp) {
                Icon(painterResource(R.drawable.ic_profilecreate_back_24), "뒤로", tint = SopkathonTheme.colors.coolGray950)
            }
            Text("게시물", Modifier.weight(1f), style = SopkathonTheme.typography.subtitle, color = SopkathonTheme.colors.coolGray950)
            Box {
                IconButton(onClick = { showMore = true }) {
                    Icon(painterResource(R.drawable.ic_feed_more_24), "더보기", tint = SopkathonTheme.colors.coolGray950)
                }
                DropdownMenu(expanded = showMore, onDismissRequest = { showMore = false }) {
                    DropdownMenuItem(text = { Text("공유하기") }, onClick = { showMore = false; onShareClick() }, enabled = uiState.detail != null)
                }
            }
        }
        when {
            uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = SopkathonTheme.colors.coolGray950)
            }
            uiState.error != null -> Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Text(uiState.error, style = SopkathonTheme.typography.bodySmall)
                TextButton(onClick = onRetry) { Text("다시 시도", color = SopkathonTheme.colors.coolGray950) }
            }
            uiState.detail != null -> {
                val detail = uiState.detail
                val post = detail.post
                Column(
                    Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp).padding(bottom = 24.dp),
                ) {
                    PostAuthorRow(post.author, post.targetLabel, post.scopeLabel, post.createdAt,
                        { onColleagueClick(post.author.userId) }, { showMore = true })
                    Spacer(Modifier.height(12.dp))
                    DetailImage(detail, uiState.selectedTagType, onTagClick, onItemClick)
                    PostReactionBar(post.likeCount, post.commentCount, post.isLiked,
                        onLikeClick, onCommentsClick, onShareClick)
                    PostContent(post.title, post.content)
                    Spacer(Modifier.height(12.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(detail.items, key = { it.clothesId }) { item ->
                            Text(
                                "${item.category}  ${item.name} ↗",
                                style = SopkathonTheme.typography.caption,
                                color = SopkathonTheme.colors.coolGray950,
                                modifier = Modifier.border(1.dp, SopkathonTheme.colors.coolGray50, RoundedCornerShape(12.dp))
                                    .noRippleClickable { onItemClick(item.clothesId) }.padding(12.dp),
                            )
                        }
                    }
                    Text(
                        "댓글 ${post.commentCount}개 모두 보기",
                        style = SopkathonTheme.typography.bodySmall,
                        color = SopkathonTheme.colors.coolGray500,
                        modifier = Modifier.fillMaxWidth().noRippleClickable(onClick = onCommentsClick).padding(vertical = 12.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailImage(
    detail: PostDetailUiModel,
    selectedTagType: OutfitItemType?,
    onTagClick: (OutfitItemType) -> Unit,
    onItemClick: (Long) -> Unit,
) {
    BoxWithConstraints(Modifier.fillMaxWidth().aspectRatio(328f / 340f).clip(RoundedCornerShape(20.dp))) {
        PostImageSection(
            detail.post.imageUrl, detail.post.hashtag,
            showItemMarkers = true, onItemClick = onTagClick,
            hasTopItem = detail.items.any { it.type == OutfitItemType.TOP },
            hasBottomItem = detail.items.any { it.type == OutfitItemType.BOTTOM },
        )
        if (selectedTagType != null) {
            val candidates = detail.items.filter { it.type == selectedTagType }
            Column(
                modifier = Modifier
                    .offset(x = if (selectedTagType == OutfitItemType.TOP) 12.dp else maxWidth * 0.55f,
                        y = maxHeight * if (selectedTagType == OutfitItemType.TOP) 0.34f else 0.70f)
                    .width(maxWidth * 0.42f).heightIn(max = maxHeight * 0.28f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                candidates.forEach { item ->
                    Row(
                        Modifier.fillMaxWidth().background(SopkathonTheme.colors.white, RoundedCornerShape(12.dp))
                            .noRippleClickable { onItemClick(item.clothesId) }.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AsyncImage(
                            model = item.imageUrl,
                            contentDescription = item.name,
                            modifier = Modifier.size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SopkathonTheme.colors.coolGray50),
                            contentScale = ContentScale.Fit,
                        )
                        Spacer(Modifier.width(6.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${item.brand} ↗",
                                style = SopkathonTheme.typography.caption,
                                color = SopkathonTheme.colors.coolGray500,
                            )
                            Text(
                                text = item.name,
                                style = SopkathonTheme.typography.caption,
                                color = SopkathonTheme.colors.coolGray950,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        }
    }
}

@ScreenPreview
@Composable
private fun PostDetailScreenPreview() {
    SopkathonTheme {
        PostDetailScreen(
            PostDetailUiState(detail = PostDetailUiModel(FeedUiState.dummy.posts.first(), 101, listOf(
                OutfitItemUiModel(1, "네이비 크루넥 니트", "상의", "", "https://example.com", OutfitItemType.TOP),
                OutfitItemUiModel(2, "아이보리 데님", "하의", "", null, OutfitItemType.BOTTOM),
            ), emptyList())),
            {}, {}, {}, {}, {}, {}, {}, {},
        )
    }
}
