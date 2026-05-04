package org.example.project.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import myvrmoviesmultiplatform.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun IntroScreen(onGetInClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = BlackBackgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            HeaderSection()
            FooterSection(onGetInClick)
        }
    }
}

@Composable
fun HeaderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(650.dp)
    ) {
        Image(
            painter = painterResource(Res.drawable.bg1),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.matchParentSize()
        ) {
            Image(
                painter = painterResource(Res.drawable.woman),
                contentDescription = null,
                modifier = Modifier.size(360.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Watch Videos in \nVirtual Reality",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun FooterSection(onGetInClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Image(
            painter = painterResource(Res.drawable.bg2),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
        Button(
            modifier = Modifier
                .size(200.dp, 50.dp)
                .align(Alignment.Center),
            shape = RoundedCornerShape(50.dp),
            onClick = onGetInClick,
            border = androidx.compose.foundation.BorderStroke(
                width = 4.dp,
                brush = Brush.linearGradient(colors = listOf(PinkColor, GreenColor))
            ),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Get In",
                fontSize = 18.sp,
                color = Color.White
            )
        }
    }
}
