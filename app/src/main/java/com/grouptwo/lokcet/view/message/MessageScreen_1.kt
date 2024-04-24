package com.grouptwo.lokcet.view.message

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.FlowColumnScopeInstance.align
//import androidx.compose.foundation.layout.FlowColumnScopeInstance.align
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grouptwo.lokcet.R
import com.grouptwo.lokcet.ui.component.global.composable.BasicIconButton
import com.grouptwo.lokcet.ui.theme.BlackSecondary
import com.grouptwo.lokcet.ui.theme.WhitePrimary
import com.grouptwo.lokcet.ui.theme.fontFamily

@Composable
fun MessageScreen1(viewModel : MessageViewModel = hiltViewModel(),
                   popUp :() -> Unit)
{
    Box(modifier = Modifier.fillMaxSize())
    {
        Column (

            horizontalAlignment = Alignment.CenterHorizontally,
            //verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 16.dp)
                //.verticalScroll(scrollState)

        ) {
            Box (modifier = Modifier
                .width(342.dp)
                .height(79.dp)) {
                Spacer(modifier = Modifier.height(70.dp))
                BasicIconButton(
                    drawableResource = R.drawable.arrow_left,
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.BottomStart),
                    action = { viewModel.onBackClick(popUp) },
                    description = "Back icon",
                )

                Text(
                    text = "Tin nhắn",
                    modifier = Modifier.align(Alignment.Center),
                    style = TextStyle(
                        color = Color.White,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        textAlign = TextAlign.Center
                    )
                )
            }

            LazyColumn (modifier = Modifier.fillMaxSize()) {
                items(15)
                {item ->
                    LazyColumnItem {
                    }
                }
            }
        }
    }
}

@Composable
fun LazyColumnItem(function: () -> Unit)
{
    Column {
        Spacer(modifier = Modifier.height(0.dp))
        Card(modifier = Modifier.wrapContentSize(),
            colors = CardDefaults.cardColors(Color.Transparent),
            elevation = CardDefaults.cardElevation())
        {
            Row (modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(0.dp)) {

                Image(painter = painterResource(id = R.drawable.icon_friend),
                    contentDescription = "icon user",
                    modifier = Modifier.align(Alignment.Top))

                Column (modifier = Modifier.fillMaxWidth()) {
                    Row (modifier = Modifier.height(30.dp)){
                        Text(text = "Thinh...",
                            color = Color.White)


                    Text(
                        text = "ngày 24 thg 4, 2024",
                        color = Color.White,
                        textAlign = TextAlign.End,
                        modifier = Modifier.size(200.dp)
                    )

                    BasicIconButton(
                        drawableResource = R.drawable.arrow_down,
                        modifier = Modifier.size(25.dp)
                            .align(Alignment.Top),
                        action = { /*TODO*/ },
                        description = "detail"
                    )

                    }
                    Text(text = "Bla",
                        style = TextStyle(
                            color = WhitePrimary,
                            fontFamily = fontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewMess()
{
    Box(modifier = Modifier
        .fillMaxSize()
        .background(BlackSecondary))
    {
        LazyColumnItem {

        }
    }
}



