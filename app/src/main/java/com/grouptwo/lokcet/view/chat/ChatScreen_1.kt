package com.grouptwo.lokcet.view.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grouptwo.lokcet.R
import com.grouptwo.lokcet.ui.component.global.composable.BasicIconButton

@Composable
fun ChatScreen1(
    viewModel: ChatViewModel = hiltViewModel(),
    popUp: () -> Unit
) {
    val scrollState = rememberScrollState()
    Box(modifier = Modifier.fillMaxSize()) {
        // Chat screen
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            BasicIconButton(
                drawableResource = R.drawable.arrow_left,
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.Start),
                action = { viewModel.onBackClick(popUp) },
                description = "Back icon",
                colors = Color(0xFF948F8F),
                tint = Color.White
            )

            // Chat screen content
            // Tittle

        }
    }
}