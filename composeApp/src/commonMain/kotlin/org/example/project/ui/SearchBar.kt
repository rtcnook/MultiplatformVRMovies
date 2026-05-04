package org.example.project.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import myvrmoviesmultiplatform.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun SearchBar(hint: String = "") {
    var text by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(50.dp)
            .background(
                color = Color(0x20FFFFFF),
                shape = RoundedCornerShape(50.dp)
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(Res.drawable.search),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        TextField(
            value = text,
            onValueChange = { newText -> text = newText },
            placeholder = {
                Text(text = hint, color = Color(0xFFbdbdbd))
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                cursorColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedPlaceholderColor = Color(0xFFbdbdbd),
                unfocusedPlaceholderColor = Color(0xFFbdbdbd)
            ),
            modifier = Modifier.weight(1f).fillMaxHeight(),
            textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
            shape = RoundedCornerShape(50.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            painter = painterResource(Res.drawable.microphone),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}
