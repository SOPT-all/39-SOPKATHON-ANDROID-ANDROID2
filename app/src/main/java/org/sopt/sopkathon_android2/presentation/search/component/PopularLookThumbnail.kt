package org.sopt.sopkathon_android2.presentation.search.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import org.sopt.sopkathon_android2.R

@Composable
internal fun PopularLookThumbnail(index: Int) {
    val look = popularLooks.getOrNull(index) ?: return
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val unit = maxWidth / 104
        Image(
            painter = painterResource(look.room),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop,
        )
        Image(
            painter = painterResource(R.drawable.img_search_floor_shadow),
            contentDescription = null,
            modifier = Modifier.offset(unit * 30.88f, unit * 135.08f)
                .size(unit * 42.24f, unit * 5.28f),
        )
        listOf(look.bottom, look.top, look.head).forEach { layer ->
            Image(
                painter = painterResource(layer.asset),
                contentDescription = null,
                modifier = Modifier.offset(unit * layer.x, unit * layer.y)
                    .size(unit * layer.width, unit * layer.height),
            )
        }
    }
}

private data class LookLayer(
    @param:DrawableRes val asset: Int,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
)

private data class PopularLook(
    @param:DrawableRes val room: Int,
    val head: LookLayer,
    val top: LookLayer,
    val bottom: LookLayer,
)

private fun head(@DrawableRes asset: Int) = LookLayer(asset, 33.52f, 8.36f, 36.96f, 36.96f)
private fun top(@DrawableRes asset: Int) = LookLayer(asset, 23.84f, 36.52f, 56.32f, 56.32f)
private fun bottom(@DrawableRes asset: Int) = LookLayer(asset, 35.72f, 84.04f, 32.56f, 57.64f)

private val popularLooks = listOf(
    PopularLook(
        R.drawable.img_search_room_lavender_heart,
        LookLayer(R.drawable.img_search_head_park, 39.24f, 11f, 25.52f, 30.8f),
        LookLayer(R.drawable.img_search_top_navyknit, 26.48f, 38.28f, 51.04f, 57.2f),
        LookLayer(R.drawable.img_search_bottom_ivorydenim, 37.92f, 84.04f, 28.16f, 61.16f),
    ),
    PopularLook(
        R.drawable.img_search_bg_miniroom,
        head(R.drawable.img_search_head_lee),
        top(R.drawable.img_search_top_skyoxford),
        bottom(R.drawable.img_search_bottom_beigechino),
    ),
    PopularLook(
        R.drawable.img_search_bg_miniroom1,
        head(R.drawable.img_search_head_han),
        top(R.drawable.img_search_top_creamcable),
        bottom(R.drawable.img_search_bottom_lightwidedenim),
    ),
    PopularLook(
        R.drawable.img_search_bg_miniroom2,
        head(R.drawable.img_search_head_lee),
        top(R.drawable.img_search_top_skyoxford),
        bottom(R.drawable.img_search_bottom_beigechino),
    ),
    PopularLook(
        R.drawable.img_search_bg_miniroom3,
        head(R.drawable.img_search_head_choi),
        top(R.drawable.img_search_top_camelblazer),
        bottom(R.drawable.img_search_bottom_blackslacks),
    ),
    PopularLook(
        R.drawable.img_search_bg_miniroom4,
        head(R.drawable.img_search_head_kim),
        top(R.drawable.img_search_top_navycardigan),
        bottom(R.drawable.img_search_bottom_beigestraight),
    ),
)
