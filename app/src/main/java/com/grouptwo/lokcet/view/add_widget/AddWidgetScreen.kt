package com.grouptwo.lokcet.view.add_widget

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grouptwo.lokcet.data.model.widgetItems
import com.grouptwo.lokcet.ui.component.global.pager.HorizontalPagerIndicator
import com.grouptwo.lokcet.ui.theme.BlackSecondary
import com.grouptwo.lokcet.ui.theme.YellowPrimary
import com.grouptwo.lokcet.ui.theme.fontFamily

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AddWidgetScreen(
    clearAndNavigate: (String) -> Unit, viewModel: AddWidgetViewModel = hiltViewModel()
) {
    // Hold pager state
    val pagerState = rememberPagerState(pageCount = { widgetItems.size })
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = 40.dp,
                bottom = 40.dp,
            ),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Text(
            text = "Tạo một Locket mới", style = TextStyle(
                fontSize = 24.sp,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
            )
        )
        Spacer(modifier = Modifier.height(40.dp))
        HorizontalPager(state = pagerState, modifier = Modifier.weight(1.5f)) { page ->
            AddWidgetTutorial(item = widgetItems[page])
        }
        Spacer(modifier = Modifier.height(20.dp))
        HorizontalPagerIndicator(
            pagerState = pagerState,
            pageCount = widgetItems.size,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
            activeColor = Color.White
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                viewModel.onConfirmClick(clearAndNavigate)
            },
            modifier = Modifier
                .width(300.dp)
                .padding(8.dp)
                .clip(
                    shape = RoundedCornerShape(50)
                ),
            colors = ButtonDefaults.buttonColors(YellowPrimary),
        ) {
            Text(
                text = "Tôi đã thêm tiện ích này rồi", style = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    color = BlackSecondary,
                )
            )
        }
    }

}
