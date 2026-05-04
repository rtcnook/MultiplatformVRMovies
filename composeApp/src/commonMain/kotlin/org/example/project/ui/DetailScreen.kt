package org.example.project.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import myvrmoviesmultiplatform.composeapp.generated.resources.*
import org.example.project.domain.FilmItemModel
import org.jetbrains.compose.resources.painterResource

@Composable
fun DetailScreen(film: FilmItemModel, onBackClick: () -> Unit) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = BlackBackgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // 1. 顶部海报区域
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                // 背景模糊图
                AsyncImage(
                    model = film.Poster,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.1f
                )

                // 返回按钮
                Image(
                    painter = painterResource(Res.drawable.back),
                    contentDescription = "Back",
                    modifier = Modifier
                        .padding(start = 16.dp, top = 48.dp)
                        .size(35.dp)
                        .clickable { onBackClick() }
                )

                // 收藏按钮
                Image(
                    painter = painterResource(Res.drawable.fav),
                    contentDescription = "Favorite",
                    modifier = Modifier
                        .padding(end = 16.dp, top = 48.dp)
                        .size(35.dp)
                        .align(Alignment.TopEnd)
                )

                // 中间主海报
                AsyncImage(
                    model = film.Poster,
                    contentDescription = null,
                    modifier = Modifier
                        .size(210.dp, 300.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .align(Alignment.BottomCenter),
                    contentScale = ContentScale.Crop,
                    error = painterResource(Res.drawable.bg2)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. 电影标题
            Text(
                text = film.Title,
                style = TextStyle(
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. 评分、时长、年份信息
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(Res.drawable.star),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(text = "IMDB: ${film.Imdb}", color = Color.White, fontSize = 12.sp)

                Spacer(modifier = Modifier.width(10.dp))

                Icon(
                    painter = painterResource(Res.drawable.time),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(text = "Runtime: ${film.Time}", color = Color.White, fontSize = 12.sp)

                Spacer(modifier = Modifier.width(10.dp))

                Icon(
                    painter = painterResource(Res.drawable.cal),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(text = "Release: ${film.Year}", color = Color.White, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. 简介部分
            Text(
                text = "Summary",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = film.Description,
                style = TextStyle(color = Color.White, fontSize = 14.sp, lineHeight = 20.sp),
                modifier = Modifier.padding(horizontal = 16.dp),
                textAlign = TextAlign.Justify
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 5. 演员列表部分
            if (film.Casts.isNotEmpty()) {
                Text(
                    text = "Actors",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                val actorsString = film.Casts.joinToString(", ") { it.Actor }
                Text(
                    text = actorsString,
                    style = TextStyle(color = Color.White, fontSize = 14.sp),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(film.Casts) { cast ->
                        AsyncImage(
                            model = cast.PicUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(65.dp)
                                .clip(RoundedCornerShape(50.dp)),
                            contentScale = ContentScale.Crop,
                            error = painterResource(Res.drawable.woman)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
