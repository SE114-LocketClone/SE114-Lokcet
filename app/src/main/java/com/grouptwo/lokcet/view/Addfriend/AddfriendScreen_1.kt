package com.grouptwo.lokcet.view.Addfriend

import android.annotation.SuppressLint
import android.widget.ImageButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.grouptwo.lokcet.ui.theme.fontFamily

@SuppressLint("ResourceAsColor")
@Composable
fun AddfriendScreen1()
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
            text = "Chọn 10 người bạn thân\n" +
                "Mời một người bạn để tiếp tục",
            style = TextStyle(
                fontSize = 20.sp,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF737070),
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
                        color = Color(0xFF737070),
                        textAlign = TextAlign.Center)) },
            singleLine = true,
            leadingIcon = {Image(painter = painterResource(id = com.grouptwo.lokcet.R.drawable.icon_search),
                contentDescription = "search",
                modifier = Modifier.size(20.dp)) }
        )

        Row(
            modifier = Modifier
                .width(349.dp)
                .height(60.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Image( painter = painterResource(id = com.grouptwo.lokcet.R.drawable.icon_addfriend),
                contentDescription = "iconaddfriend",
                modifier = Modifier.size(25.dp)
            )

            Text(modifier = Modifier
                .padding(top = 20.dp)
                .height(60.dp)
                .width(250.dp),
                text = "Tìm bạn bè từ ứng dụng khác",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF959292),
                    textAlign = TextAlign.Center,
                )
            )
        }



        Box(modifier = Modifier
            .width(349.dp)
            .height(80.dp)
            .background(color = Color(0xFF141414), shape = RoundedCornerShape(size = 15.dp))
        )
        {
            Row(
                modifier = Modifier
                    .fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                ) {
                    Image( painter = painterResource(id = com.grouptwo.lokcet.R.drawable.icon_messenger),
                        contentDescription = "iconmessenger",
                        modifier = Modifier.size(36.dp)
                    )
                    Text(modifier = Modifier
                        .padding(top = 2.dp)
                        .height(20.dp)
                        .width(80.dp),
                        text = "Messenger",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEDEDED),
                            textAlign = TextAlign.Center,
                        )
                    )
                }
                Column (
                    modifier = Modifier
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ){
                    Image( painter = painterResource(id = com.grouptwo.lokcet.R.drawable.icon_insta),
                        contentDescription = "iconinsta",
                        modifier = Modifier.size(36.dp)
                    )
                    Text(modifier = Modifier
                        .padding(top = 2.dp)
                        .height(20.dp)
                        .width(80.dp),
                        text = "Insta",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEDEDED),
                            textAlign = TextAlign.Center,
                        )
                    )
                }
                Column (
                    modifier = Modifier
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    ){
                    Image( painter = painterResource(id = com.grouptwo.lokcet.R.drawable.icon_tinnhan),
                        contentDescription = "icontinnhan",
                        modifier = Modifier.size(36.dp)
                    )
                    Text(modifier = Modifier
                        .padding(top = 2.dp)
                        .height(20.dp)
                        .width(80.dp),
                        text = "Tin nhắn",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEDEDED),
                            textAlign = TextAlign.Center,
                        )
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image( painter = painterResource(id = com.grouptwo.lokcet.R.drawable.icon_khac),
                        contentDescription = "iconkhac",
                        modifier = Modifier.size(36.dp)
                    )
                    Text(modifier = Modifier
                        .padding(top = 2.dp)
                        .height(20.dp)
                        .width(80.dp),
                        text = "Khác",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEDEDED),
                            textAlign = TextAlign.Center,
                        )
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .width(349.dp)
                .height(60.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = com.grouptwo.lokcet.R.drawable.vector),
                contentDescription = "iconvector",
                modifier = Modifier.size(25.dp)
            )

            Text(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .height(60.dp)
                    .width(250.dp),
                text = "Chia sẻ liên kết Locket của bạn",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF959292),
                    textAlign = TextAlign.Center,
                )
            )
        }
        Box(modifier = Modifier
            .width(390.dp)
            .height(300.dp)
            .padding(start = 80.dp)
        )
        {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image( painter = painterResource(id = com.grouptwo.lokcet.R.drawable.icon_messenger),
                        contentDescription = "iconmessenger",
                        modifier = Modifier.size(36.dp)
                    )
                    Text(modifier = Modifier
                        .padding(20.dp)
                        .height(20.dp)
                        .width(150.dp),
                        text = "Messenger",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEDEDED),

                        )
                    )
                    Button(
                        onClick = { /*TODO*/ },
                        colors = ButtonDefaults.buttonColors(Color(color=0xFF000000))
                    ) {
                        Image( painter = painterResource(id = com.grouptwo.lokcet.R.drawable.icon_vector_right),
                            contentDescription = "iconvetorright",
                            modifier = Modifier.size(20.dp),)
                    }
                }
                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Image( painter = painterResource(id = com.grouptwo.lokcet.R.drawable.icon_insta),
                        contentDescription = "iconinsta",
                        modifier = Modifier.size(36.dp)
                    )
                    Text(modifier = Modifier
                        .padding(20.dp)
                        .height(20.dp)
                        .width(150.dp),
                        text = "Insta",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEDEDED),

                        )
                    )
                    Button(
                        onClick = { /*TODO*/ },
                        colors = ButtonDefaults.buttonColors(Color(color=0xFF000000))
                    ) {
                        Image( painter = painterResource(id = com.grouptwo.lokcet.R.drawable.icon_vector_right),
                            contentDescription = "iconvetorright",
                            modifier = Modifier.size(20.dp),)
                    }
                }
                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Image( painter = painterResource(id = com.grouptwo.lokcet.R.drawable.icon_tinnhan),
                        contentDescription = "icontinnhan",
                        modifier = Modifier.size(36.dp)
                    )
                    Text(modifier = Modifier
                        .padding(20.dp)
                        .height(20.dp)
                        .width(150.dp),
                        text = "Tin nhắn",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEDEDED),
                        )
                    )
                    Button(
                        onClick = { /*TODO*/ },
                        colors = ButtonDefaults.buttonColors(Color(color=0xFF000000))
                    ) {
                        Image( painter = painterResource(id = com.grouptwo.lokcet.R.drawable.icon_vector_right),
                            contentDescription = "iconvetorright",
                            modifier = Modifier.size(20.dp),)
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image( painter = painterResource(id = com.grouptwo.lokcet.R.drawable.icon_telegram),
                        contentDescription = "icontelegram",
                        modifier = Modifier.size(36.dp)
                    )
                    Text(modifier = Modifier
                        .padding(20.dp)
                        .height(20.dp)
                        .width(150.dp),
                        text = "Telegram",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEDEDED),
                        )
                    )
                    Button(
                        onClick = { /*TODO*/ },
                        colors = ButtonDefaults.buttonColors(Color(color=0xFF000000))
                    ) {
                        Image( painter = painterResource(id = com.grouptwo.lokcet.R.drawable.icon_vector_right),
                            contentDescription = "iconvetorright",
                            modifier = Modifier.size(20.dp),)
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image( painter = painterResource(id = com.grouptwo.lokcet.R.drawable.icon_khac),
                        contentDescription = "iconkhac",
                        modifier = Modifier.size(36.dp)
                    )
                    Text(modifier = Modifier
                        .padding(20.dp)
                        .height(20.dp)
                        .width(150.dp),
                        text = "Các ứng dụng khác",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEDEDED),
                        )
                    )
                    Button(
                        onClick = { /*TODO*/ },
                        colors = ButtonDefaults.buttonColors(Color(color=0xFF000000))
                        ) {
                        Image( painter = painterResource(id = com.grouptwo.lokcet.R.drawable.icon_vector_right),
                            contentDescription = "iconvetorright",
                            modifier = Modifier.size(20.dp),)
                    }
                }
            }
        }

        Button(
            onClick = {},
            modifier = Modifier
                .width(250.dp)
                .height(46.dp)
                .clip(
                    shape = RoundedCornerShape(50)
                ),
            colors = ButtonDefaults.buttonColors(Color(color=0xFFE5A500)),
        )
        {
            Text ( "Tiếp tục",style=TextStyle(
                fontSize = 26.sp,
                fontFamily = fontFamily,
                color = Color(0xFF000000),
                fontWeight = FontWeight.Bold))
            Image( painter = painterResource(id = com.grouptwo.lokcet.R.drawable.iconarrowright1),
                contentDescription = "arrowright",
                modifier = Modifier.size(50.dp)
            )
        }
    }
}


