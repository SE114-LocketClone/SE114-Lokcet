package com.grouptwo.lokcet.view.Addfriend

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grouptwo.lokcet.R
import com.grouptwo.lokcet.ui.component.global.composable.BasicTextButton
import com.grouptwo.lokcet.ui.theme.fontFamily

@Composable
fun AddfriendScreen2()
{
    var name by remember {
        mutableStateOf("")
    }
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF000000)),
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        Text(modifier = Modifier
            .padding(top = 46.dp, bottom = 20.dp),
            text = "10/20 người bạn\n" +
                    "Mời một người bạn để tiếp tục",
            style = TextStyle(
                fontSize = 20.sp,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8C8888),
                textAlign = TextAlign.Center,)
        )

        TextField(modifier = Modifier
            .width(349.dp)
            .height(49.dp)
            .background(color = Color(0xFF272626), shape = RoundedCornerShape(size = 18.dp))
            .clip(shape = RoundedCornerShape(50)),
            value = name,
            onValueChange = { newname -> name=newname },
            placeholder = {
                Text(text = "Tìm trong các liên hệ của bạn",
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF737070),)
                ) },
            singleLine = true,
            leadingIcon = {
                Image(painter = painterResource(id = R.drawable.icon_search),
                contentDescription = "search",
                modifier = Modifier.size(20.dp)) }
        )


        Text(modifier = Modifier
            .padding(top = 200.dp)
            .width(250.dp),
            text = "Nhập các liên hệ của bạn",
            style = TextStyle(
                fontSize = 22.sp,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFFFFF),
                textAlign = TextAlign.Center,
            )
        )

        Text(modifier = Modifier
            .padding(top=20.dp,bottom=60.dp),
            text = "Locket không bao giờ thay bạn nhắn tin cho bạn bè",
            style = TextStyle(
                fontSize = 15.sp,
                fontFamily = fontFamily,
                color = Color(0xFF737070),
                textAlign = TextAlign.Center,)
        )

        BasicTextButton(
            stringResource = R.string.Continues,
            modifier = Modifier
                .width(156.dp)
                .height(96.dp)
                .clip(
                    shape = RoundedCornerShape(50)
                ),
            action = { /*TODO*/ },
            textStyle = TextStyle(
                fontSize = 20.sp,
                fontFamily = fontFamily,
                color = Color(0xFF000000),
                fontWeight = FontWeight.Bold
            ),
        )
        Box(modifier = Modifier
            .padding(70.dp)
        )
        {

        }
        Button(
            onClick = {},
            modifier = Modifier
                .width(90.dp)
                .height(40.dp)
                .clip(
                    shape = RoundedCornerShape(50)
                ),
            colors = ButtonDefaults.buttonColors(Color(color=0xFF565551)),
        )
        {
            Text ( "Bỏ qua",style= TextStyle(
                fontSize = 14.sp,
                fontFamily = fontFamily,
                color = Color(0xFFFFFFFF),
                fontWeight = FontWeight.Bold)
            )
        }
    }
}