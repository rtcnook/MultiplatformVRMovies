package org.example.project.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import myvrmoviesmultiplatform.composeapp.generated.resources.*
import org.example.project.domain.FilmItemModel
import org.jetbrains.compose.resources.painterResource

@Composable
fun FilmItem(
    film: FilmItemModel,
    onClick: (FilmItemModel) -> Unit
) {
    // 固定宽度的竖向海报卡片，与原 Android 项目一致
    Column(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick(film) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = film.Poster,
            contentDescription = film.Title,
            modifier = Modifier
                .width(140.dp)
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop,
            error = painterResource(Res.drawable.bg2)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = film.Title,
            style = MaterialTheme.typography.bodyMedium,
            color = androidx.compose.ui.graphics.Color.White,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}
