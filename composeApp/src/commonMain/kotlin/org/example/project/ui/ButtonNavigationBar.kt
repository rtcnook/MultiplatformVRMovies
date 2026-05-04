package org.example.project.ui

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import myvrmoviesmultiplatform.composeapp.generated.resources.*

@Composable
fun BottomNavigationBar() {
    val bottomMenuItemsList = prepareBottomMenu()
    var selectedItem by remember { mutableStateOf("Home") }

    BottomAppBar(
        containerColor = Black3Color,
        contentColor = Color.White,
        tonalElevation = 3.dp
    ) {
        bottomMenuItemsList.forEachIndexed { index, bottomMenuItem ->
            if (index == 2) {
                // 第3个位置留空，给 FloatingActionButton 留出空间
                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = {},
                    enabled = false
                )
            }

            NavigationBarItem(
                selected = (selectedItem == bottomMenuItem.label),
                onClick = {
                    selectedItem = bottomMenuItem.label
                },
                icon = {
                    Icon(
                        painter = bottomMenuItem.icon,
                        contentDescription = bottomMenuItem.label,
                        modifier = Modifier
                            .height(20.dp)
                            .width(20.dp)
                    )
                },
                label = {
                    Text(text = bottomMenuItem.label)
                },
                alwaysShowLabel = true,
                enabled = true
            )
        }
    }
}

data class BottomMenuItem(
    val label: String,
    val icon: Painter
)

@Composable
fun prepareBottomMenu(): List<BottomMenuItem> {
    return listOf(
        BottomMenuItem("Home", painterResource(Res.drawable.btn_1)),
        BottomMenuItem("Profile", painterResource(Res.drawable.btn_2)),
        BottomMenuItem("Support", painterResource(Res.drawable.btn_3)),
        BottomMenuItem("Settings", painterResource(Res.drawable.btn_4))
    )
}
