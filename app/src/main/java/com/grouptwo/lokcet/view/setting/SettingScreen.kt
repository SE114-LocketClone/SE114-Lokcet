//package com.grouptwo.lokcet.view.setting
//
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.imePadding
//import androidx.compose.foundation.layout.navigationBarsPadding
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.statusBarsPadding
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.grouptwo.lokcet.R
//import com.grouptwo.lokcet.ui.component.global.ime.rememberImeState
//import com.grouptwo.lokcet.ui.theme.fontFamily
//
//@Composable
//fun SettingScreen() {
//    val scrollState = rememberScrollState()
//    val imeState = rememberImeState()
//
//    LaunchedEffect(key1 = imeState.value) {
//        if (imeState.value) {
//            scrollState.animateScrollTo(scrollState.maxValue, tween(300))
//        }
//    }
//
//    Box(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .statusBarsPadding()
//                .navigationBarsPadding()
//                .imePadding()
//                .verticalScroll(scrollState)
//                .background(color = Color(0xFF000000))
//        ) {
//            // Avatar
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(color = Color(0xFF000000)),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(top = 20.dp),
//                    horizontalArrangement = Arrangement.Center,
//                    verticalAlignment = Alignment.Top
//                ) {
//                    Spacer(modifier = Modifier.size(120.dp))
//                    Image(
//                        modifier = Modifier
//                            .size(110.dp)
//                            .border(
//                                BorderStroke(3.dp, Color(color = 0xFFE5A500)), CircleShape
//                            )
//                            .clip(CircleShape),
//                        painter = painterResource(id = R.drawable.icon_profile),
//                        contentDescription = "iconprofile",
//                    )
//                    Spacer(modifier = Modifier.size(50.dp))
//                    Button(
//                        onClick = { /*TODO*/ },
//                        colors = ButtonDefaults.buttonColors(Color(color = 0xFF000000))
//                    ) {
//                        Image(
//                            painter = painterResource(id = R.drawable.icon_vector_right),
//                            contentDescription = "iconvectorright",
//                            modifier = Modifier.size(25.dp)
//                        )
//                    }
//                }
//                Text(
//                    text = "Kelvin", style = androidx.compose.ui.text.TextStyle(
//                        color = Color.White,
//                        fontSize = 24.sp,
//                        fontWeight = FontWeight.Bold,
//                        fontFamily = fontFamily,
//                        textAlign = TextAlign.Center
//                    ), modifier = Modifier
//                        .width(152.dp)
//                        .height(43.dp)
//                        .padding(2.dp)
//                )
//            }
//
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(start = 30.dp)
//                    .background(color = Color(0xFF000000))
//            ) {
//
//
//                Spacer(modifier = Modifier.size(15.dp))
//
//                Row(
//                    modifier = Modifier
//                        .width(152.dp)
//                        .height(43.dp)
//                ) {
//                    Image(
//                        painter = painterResource(id = R.drawable.icon_heart),
//                        contentDescription = "iconheart",
//                        modifier = Modifier.size(18.dp)
//
//                    )
//                    Text(
//                        text = " Tiện ích",
//                        style = androidx.compose.ui.text.TextStyle(
//                            color = Color(0xFF959292),
//                            fontSize = 17.sp,
//                            fontWeight = FontWeight.Bold,
//                            fontFamily = fontFamily
//                        ),
//                    )
//                }
//
//                Row(
//                    modifier = Modifier
//                        .width(349.dp)
//                        .height(140.dp)
//                        .background(
//                            color = Color(0xFF272626), shape = RoundedCornerShape(size = 26.dp)
//                        ),
//                    verticalAlignment = Alignment.CenterVertically,
//                ) {
//                    Spacer(modifier = Modifier.size(15.dp))
////                    Column(
////                        modifier = Modifier
////                            .border(
////                                width = 1.dp,
////                                color = Color(0xFF6D6A6A),
////                                shape = RoundedCornerShape(size = 19.dp)
////                            )
////                            .width(94.dp)
////                            .height(94.dp)
////                            .background(
////                                color = Color(0xFF211E1E), shape = RoundedCornerShape(size = 19.dp)
////                            ), horizontalAlignment = Alignment.CenterHorizontally
////                    ) {
////                        Button(
////                            onClick = { /*TODO*/ },
////                            colors = ButtonDefaults.buttonColors(Color(0xFF211E1E))
////                        ) {
////                            Column(
////                                modifier = Modifier
////                                    .size(40.dp)
////                                    .background(
////                                        color = Color(0xFF272626),
////                                        shape = RoundedCornerShape(size = 26.dp)
////                                    )
////                                    .border(
////                                        BorderStroke(3.dp, Color(color = 0xFFE5A500)), CircleShape
////                                    )
////                                    .clip(CircleShape),
////                                horizontalAlignment = Alignment.CenterHorizontally,
////                                verticalArrangement = Arrangement.Center
////                            ) {
////                                Image(
////                                    modifier = Modifier.size(15.dp),
////                                    painter = painterResource(id = R.drawable.icon_plus),
////                                    contentDescription = "iconplus",
////                                )
////                            }
////                        }
////                        Text(
////                            text = "Tiện ích mới", style = androidx.compose.ui.text.TextStyle(
////                                color = Color(0xFFFFFFFF),
////                                fontSize = 11.sp,
////                                fontWeight = FontWeight.Bold,
////                                fontFamily = fontFamily
////                            ), textAlign = TextAlign.Center
////                        )
////                        Text(
////                            text = "Cho một người bạn", style = androidx.compose.ui.text.TextStyle(
////                                color = Color(0xFF959292),
////                                fontSize = 8.sp,
////                                fontWeight = FontWeight.Bold,
////                                fontFamily = fontFamily
////                            ), textAlign = TextAlign.Center
////                        )
////                    }
//                }
//
//                Spacer(modifier = Modifier.size(15.dp))
//
//                Row(
//                    modifier = Modifier
//                        .width(152.dp)
//                        .height(43.dp)
//                ) {
//                    Image(
//                        painter = painterResource(id = R.drawable.icon_man),
//                        contentDescription = "iconman",
//                        modifier = Modifier.size(18.dp)
//                    )
//                    Text(
//                        text = " Tổng quát",
//                        style = androidx.compose.ui.text.TextStyle(
//                            color = Color(0xFF959292),
//                            fontSize = 17.sp,
//                            fontWeight = FontWeight.Bold,
//                            fontFamily = fontFamily
//                        ),
//                    )
//                }
//
//                Column(
//                    modifier = Modifier
//                        .width(349.dp)
//                        .height(450.dp)
//                        .background(
//                            color = Color(0xFF272626), shape = RoundedCornerShape(size = 26.dp)
//                        )
//                ) {
//                    Row(
//                        modifier = Modifier
//                            .width(349.dp)
//                            .height(62.dp)
//                            .background(
//                                color = Color(0xFF272626), shape = RoundedCornerShape(size = 26.dp)
//                            ), verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Spacer(
//                            modifier = Modifier.size(20.dp)
//                        )
//                        Image(
//                            painter = painterResource(id = R.drawable.icon_change_image),
//                            contentDescription = "iconchangeimage",
//                            modifier = Modifier
//                                .size(25.dp)
//                                .clip(RoundedCornerShape(size = 10.dp))
//                                .background(color = Color(0xFF545252))
//                        )
//                        Text(
//                            text = "   Sửa ảnh hồ sơ", style = androidx.compose.ui.text.TextStyle(
//                                color = Color(0xFFFFFFFF),
//                                fontSize = 15.sp,
//                                fontWeight = FontWeight.Bold,
//                                fontFamily = fontFamily
//                            ), modifier = Modifier.width(240.dp)
//                        )
//                        Button(
//                            onClick = { /*TODO*/ },
//                            colors = ButtonDefaults.buttonColors(Color(color = 0xFF272626))
//                        ) {
//                            Image(
//                                painter = painterResource(id = R.drawable.icon_vector_right),
//                                contentDescription = "iconvectorright",
//                                modifier = Modifier.size(15.dp)
//                            )
//                        }
//                    }
//
//
//
//                    Row( Row(
//                        modifier = Modifier
//                            .width(349.dp)
//                            .height(62.dp)
//                            .background(
//                                color = Color(0xFF272626), shape = RoundedCornerShape(size = 26.dp)
//                            ), verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Spacer(
//                            modifier = Modifier.size(20.dp)
//                        )
//                        Image(
//                            painter = painterResource(id = R.drawable.icon_change_name),
//                            contentDescription = "iconchangename",
//                            modifier = Modifier
//                                .size(25.dp)
//                                .clip(RoundedCornerShape(size = 10.dp))
//                                .background(color = Color(0xFF545252))
//                        )
//                        Text(
//                            text = "   Sửa tên", style = androidx.compose.ui.text.TextStyle(
//                                color = Color(0xFFFFFFFF),
//                                fontSize = 15.sp,
//                                fontWeight = FontWeight.Bold,
//                                fontFamily = fontFamily
//                            ), modifier = Modifier.width(240.dp)
//                        )
//                        Button(
//                            onClick = { /*TODO*/ },
//                            colors = ButtonDefaults.buttonColors(Color(color = 0xFF272626))
//                        ) {
//                            Image(
//                                painter = painterResource(id = R.drawable.icon_vector_right),
//                                contentDescription = "iconvectorright",
//                                modifier = Modifier.size(15.dp)
//                            )
//                        }
//                    }
//                        modifier = Modifier
//                            .width(349.dp)
//                            .height(62.dp)
//                            .background(
//                                color = Color(0xFF272626), shape = RoundedCornerShape(size = 26.dp)
//                            ), verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Spacer(
//                            modifier = Modifier.size(20.dp)
//                        )
//                        Image(
//                            painter = painterResource(id = R.drawable.icon_change_date),
//                            contentDescription = "iconchangdate",
//                            modifier = Modifier
//                                .size(25.dp)
//                                .clip(RoundedCornerShape(size = 10.dp))
//                                .background(color = Color(0xFF545252))
//                        )
//                        Text(
//                            text = "   Sửa ngày sinh", style = androidx.compose.ui.text.TextStyle(
//                                color = Color(0xFFFFFFFF),
//                                fontSize = 15.sp,
//                                fontWeight = FontWeight.Bold,
//                                fontFamily = fontFamily
//                            ), modifier = Modifier.width(240.dp)
//                        )
//                        Button(
//                            onClick = { /*TODO*/ },
//                            colors = ButtonDefaults.buttonColors(Color(color = 0xFF272626))
//                        ) {
//                            Image(
//                                painter = painterResource(id = R.drawable.icon_vector_right),
//                                contentDescription = "iconvectorright",
//                                modifier = Modifier.size(15.dp)
//                            )
//                        }
//                    }
//
//                    Row(
//                        modifier = Modifier
//                            .width(349.dp)
//                            .height(62.dp)
//                            .background(
//                                color = Color(0xFF272626), shape = RoundedCornerShape(size = 26.dp)
//                            ), verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Spacer(
//                            modifier = Modifier.size(20.dp)
//                        )
//                        Image(
//                            painter = painterResource(id = R.drawable.icon_change_phone),
//                            contentDescription = "iconchangephone",
//                            modifier = Modifier
//                                .size(25.dp)
//                                .clip(RoundedCornerShape(size = 10.dp))
//                                .background(color = Color(0xFF545252))
//                        )
//                        Text(
//                            text = "   Thay đổi số điện thoại",
//                            style = androidx.compose.ui.text.TextStyle(
//                                color = Color(0xFFFFFFFF),
//                                fontSize = 15.sp,
//                                fontWeight = FontWeight.Bold,
//                                fontFamily = fontFamily
//                            ),
//                            modifier = Modifier.width(240.dp)
//                        )
//                        Button(
//                            onClick = { /*TODO*/ },
//                            colors = ButtonDefaults.buttonColors(Color(color = 0xFF272626))
//                        ) {
//                            Image(
//                                painter = painterResource(id = R.drawable.icon_vector_right),
//                                contentDescription = "iconvectorright",
//                                modifier = Modifier.size(15.dp)
//                            )
//                        }
//                    }
//
//                    Row(
//                        modifier = Modifier
//                            .width(349.dp)
//                            .height(62.dp)
//                            .background(
//                                color = Color(0xFF272626), shape = RoundedCornerShape(size = 26.dp)
//                            ), verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Spacer(
//                            modifier = Modifier.size(20.dp)
//                        )
//                        Image(
//                            painter = painterResource(id = R.drawable.icon_add),
//                            contentDescription = "iconadd",
//                            modifier = Modifier
//                                .size(25.dp)
//                                .clip(RoundedCornerShape(size = 10.dp))
//                                .background(color = Color(0xFF545252))
//                        )
//                        Text(
//                            text = "   Cách thêm tiện ích",
//                            style = androidx.compose.ui.text.TextStyle(
//                                color = Color(0xFFFFFFFF),
//                                fontSize = 15.sp,
//                                fontWeight = FontWeight.Bold,
//                                fontFamily = fontFamily
//                            ),
//                            modifier = Modifier.width(240.dp)
//                        )
//                        Button(
//                            onClick = { /*TODO*/ },
//                            colors = ButtonDefaults.buttonColors(Color(color = 0xFF272626))
//                        ) {
//                            Image(
//                                painter = painterResource(id = R.drawable.icon_vector_right),
//                                contentDescription = "iconvectorright",
//                                modifier = Modifier.size(15.dp)
//                            )
//                        }
//                    }
//
//                    Row(
//                        modifier = Modifier
//                            .width(349.dp)
//                            .height(62.dp)
//                            .background(
//                                color = Color(0xFF272626), shape = RoundedCornerShape(size = 26.dp)
//                            ), verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Spacer(
//                            modifier = Modifier.size(20.dp)
//                        )
//                        Image(
//                            painter = painterResource(id = R.drawable.icon_none),
//                            contentDescription = "iconnone",
//                            modifier = Modifier
//                                .size(25.dp)
//                                .clip(RoundedCornerShape(size = 10.dp))
//                                .background(color = Color(0xFF545252))
//                        )
//                        Text(
//                            text = "   Đã chặn", style = androidx.compose.ui.text.TextStyle(
//                                color = Color(0xFFFFFFFF),
//                                fontSize = 15.sp,
//                                fontWeight = FontWeight.Bold,
//                                fontFamily = fontFamily
//                            ), modifier = Modifier.width(240.dp)
//                        )
//                        Button(
//                            onClick = { /*TODO*/ },
//                            colors = ButtonDefaults.buttonColors(Color(color = 0xFF272626))
//                        ) {
//                            Image(
//                                painter = painterResource(id = R.drawable.icon_vector_right),
//                                contentDescription = "iconvectorright",
//                                modifier = Modifier.size(15.dp)
//                            )
//                        }
//                    }
//
//                    Row(
//                        modifier = Modifier
//                            .width(349.dp)
//                            .height(62.dp)
//                            .background(
//                                color = Color(0xFF272626), shape = RoundedCornerShape(size = 26.dp)
//                            ), verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Spacer(
//                            modifier = Modifier.size(20.dp)
//                        )
//                        Image(
//                            painter = painterResource(id = R.drawable.icon_return),
//                            contentDescription = "iconreturn",
//                            modifier = Modifier
//                                .size(25.dp)
//                                .clip(RoundedCornerShape(size = 10.dp))
//                                .background(color = Color(0xFF545252))
//                        )
//                        Text(
//                            text = "   Khôi phục đơn hàng",
//                            style = androidx.compose.ui.text.TextStyle(
//                                color = Color(0xFFFFFFFF),
//                                fontSize = 15.sp,
//                                fontWeight = FontWeight.Bold,
//                                fontFamily = fontFamily
//                            ),
//                            modifier = Modifier.width(240.dp)
//                        )
//                        Button(
//                            onClick = { /*TODO*/ },
//                            colors = ButtonDefaults.buttonColors(Color(color = 0xFF272626))
//                        ) {
//                            Image(
//                                painter = painterResource(id = R.drawable.icon_vector_right),
//                                contentDescription = "iconvectorright",
//                                modifier = Modifier.size(15.dp)
//                            )
//                        }
//                    }
//                }
//
//
//                Spacer(modifier = Modifier.size(20.dp))
//
//                Row(
//                    modifier = Modifier
//                        .width(152.dp)
//                        .height(43.dp)
//                ) {
//                    Image(
//                        painter = painterResource(id = R.drawable.icon_hoi),
//                        contentDescription = "iconsupport",
//                        modifier = Modifier.size(18.dp)
//                    )
//                    Text(
//                        text = " Support",
//                        style = androidx.compose.ui.text.TextStyle(
//                            color = Color(0xFF959292),
//                            fontSize = 17.sp,
//                            fontWeight = FontWeight.Bold,
//                            fontFamily = fontFamily
//                        ),
//                    )
//                }
//
//                Column(
//                    modifier = Modifier
//                        .width(349.dp)
//                        .height(120.dp)
//                        .background(
//                            color = Color(0xFF272626), shape = RoundedCornerShape(size = 26.dp)
//                        )
//                ) {
//                    Row(
//                        modifier = Modifier
//                            .width(349.dp)
//                            .height(62.dp)
//                            .background(
//                                color = Color(0xFF272626), shape = RoundedCornerShape(size = 26.dp)
//                            ), verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Spacer(
//                            modifier = Modifier.size(20.dp)
//                        )
//                        Image(
//                            painter = painterResource(id = R.drawable.icon_report),
//                            contentDescription = "iconreport",
//                            modifier = Modifier
//                                .size(25.dp)
//                                .clip(RoundedCornerShape(size = 10.dp))
//                                .background(color = Color(0xFF545252))
//                        )
//                        Text(
//                            text = "   Report a problem",
//                            style = androidx.compose.ui.text.TextStyle(
//                                color = Color(0xFFFFFFFF),
//                                fontSize = 15.sp,
//                                fontWeight = FontWeight.Bold,
//                                fontFamily = fontFamily
//                            ),
//                            modifier = Modifier.width(240.dp)
//                        )
//                        Button(
//                            onClick = { /*TODO*/ },
//                            colors = ButtonDefaults.buttonColors(Color(color = 0xFF272626))
//                        ) {
//                            Image(
//                                painter = painterResource(id = R.drawable.icon_vector_right),
//                                contentDescription = "iconvectorright",
//                                modifier = Modifier.size(15.dp)
//                            )
//                        }
//                    }
//
//                    Row(
//                        modifier = Modifier
//                            .width(349.dp)
//                            .height(62.dp)
//                            .background(
//                                color = Color(0xFF272626), shape = RoundedCornerShape(size = 26.dp)
//                            ), verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Spacer(
//                            modifier = Modifier.size(20.dp)
//                        )
//                        Image(
//                            painter = painterResource(id = R.drawable.icon_addchat),
//                            contentDescription = "iconaddchat",
//                            modifier = Modifier
//                                .size(25.dp)
//                                .clip(RoundedCornerShape(size = 10.dp))
//                                .background(color = Color(0xFF545252))
//                        )
//                        Text(
//                            text = "   Make a suggestion",
//                            style = androidx.compose.ui.text.TextStyle(
//                                color = Color(0xFFFFFFFF),
//                                fontSize = 15.sp,
//                                fontWeight = FontWeight.Bold,
//                                fontFamily = fontFamily
//                            ),
//                            modifier = Modifier.width(240.dp)
//                        )
//                        Button(
//                            onClick = { /*TODO*/ },
//                            colors = ButtonDefaults.buttonColors(Color(color = 0xFF272626))
//                        ) {
//                            Image(
//                                painter = painterResource(id = R.drawable.icon_vector_right),
//                                contentDescription = "iconvectorright",
//                                modifier = Modifier.size(15.dp)
//                            )
//                        }
//                    }
//                }
//                Spacer(modifier = Modifier.size(15.dp))
//
////                Row(
////                    modifier = Modifier
////                        .width(152.dp)
////                        .height(43.dp)
////                ) {
////                    Image(
////                        painter = painterResource(id = R.drawable.icon_heart),
////                        contentDescription = "iconheart",
////                        modifier = Modifier.size(18.dp)
////                    )
////                    Text(
////                        text = " Giới thiệu",
////                        style = androidx.compose.ui.text.TextStyle(
////                            color = Color(0xFF959292),
////                            fontSize = 17.sp,
////                            fontWeight = FontWeight.Bold,
////                            fontFamily = fontFamily
////                        ),
////                    )
////                }
//
////                Column(
////                    modifier = Modifier
////                        .width(349.dp)
////                        .height(450.dp)
////                        .background(
////                            color = Color(0xFF272626), shape = RoundedCornerShape(size = 26.dp)
////                        )
////                ) {
////                    Row(
////                        modifier = Modifier
////                            .width(349.dp)
////                            .height(62.dp)
////                            .background(
////                                color = Color(0xFF272626), shape = RoundedCornerShape(size = 26.dp)
////                            ), verticalAlignment = Alignment.CenterVertically
////                    ) {
////                        Spacer(
////                            modifier = Modifier.size(20.dp)
////                        )
////                        Image(
////                            painter = painterResource(id = R.drawable.icon_tiktok_small),
////                            contentDescription = "icontiktoksmall",
////                            modifier = Modifier
////                                .size(25.dp)
////                                .clip(RoundedCornerShape(size = 10.dp))
////                                .background(color = Color(0xFF545252))
////                        )
////                        Text(
////                            text = "   Tiktok", style = androidx.compose.ui.text.TextStyle(
////                                color = Color(0xFFFFFFFF),
////                                fontSize = 15.sp,
////                                fontWeight = FontWeight.Bold,
////                                fontFamily = fontFamily
////                            ), modifier = Modifier.width(240.dp)
////                        )
////                        Button(
////                            onClick = { /*TODO*/ },
////                            colors = ButtonDefaults.buttonColors(Color(color = 0xFF272626))
////                        ) {
////                            Image(
////                                painter = painterResource(id = R.drawable.icon_vector_right),
////                                contentDescription = "iconvectorright",
////                                modifier = Modifier.size(15.dp)
////                            )
////                        }
////                    }
////
////                    Row(
////                        modifier = Modifier
////                            .width(349.dp)
////                            .height(62.dp)
////                            .background(
////                                color = Color(0xFF272626), shape = RoundedCornerShape(size = 26.dp)
////                            ), verticalAlignment = Alignment.CenterVertically
////                    ) {
////                        Spacer(
////                            modifier = Modifier.size(20.dp)
////                        )
////                        Image(
////                            painter = painterResource(id = R.drawable.icon_istagram_small),
////                            contentDescription = "iconinstagram",
////                            modifier = Modifier
////                                .size(25.dp)
////                                .clip(RoundedCornerShape(size = 10.dp))
////                                .background(color = Color(0xFF545252))
////                        )
////                        Text(
////                            text = "   Instagram", style = androidx.compose.ui.text.TextStyle(
////                                color = Color(0xFFFFFFFF),
////                                fontSize = 15.sp,
////                                fontWeight = FontWeight.Bold,
////                                fontFamily = fontFamily
////                            ), modifier = Modifier.width(240.dp)
////                        )
////                        Button(
////                            onClick = { /*TODO*/ },
////                            colors = ButtonDefaults.buttonColors(Color(color = 0xFF272626))
////                        ) {
////                            Image(
////                                painter = painterResource(id = R.drawable.icon_vector_right),
////                                contentDescription = "iconvectorright",
////                                modifier = Modifier.size(15.dp)
////                            )
////                        }
////                    }
////
////                    Row(
////                        modifier = Modifier
////                            .width(349.dp)
////                            .height(62.dp)
////                            .background(
////                                color = Color(0xFF272626), shape = RoundedCornerShape(size = 26.dp)
////                            ), verticalAlignment = Alignment.CenterVertically
////                    ) {
////                        Spacer(
////                            modifier = Modifier.size(20.dp)
////                        )
////                        Image(
////                            painter = painterResource(id = R.drawable.icon_twitter_small),
////                            contentDescription = "icontwittersmall",
////                            modifier = Modifier
////                                .size(25.dp)
////                                .clip(RoundedCornerShape(size = 10.dp))
////                                .background(color = Color(0xFF545252))
////                        )
////                        Text(
////                            text = "   Twitter", style = androidx.compose.ui.text.TextStyle(
////                                color = Color(0xFFFFFFFF),
////                                fontSize = 15.sp,
////                                fontWeight = FontWeight.Bold,
////                                fontFamily = fontFamily
////                            ), modifier = Modifier.width(240.dp)
////                        )
////                        Button(
////                            onClick = { /*TODO*/ },
////                            colors = ButtonDefaults.buttonColors(Color(color = 0xFF272626))
////                        ) {
////                            Image(
////                                painter = painterResource(id = R.drawable.icon_vector_right),
////                                contentDescription = "iconvectorright",
////                                modifier = Modifier.size(15.dp)
////                            )
////                        }
////                    }
////
////                    Row(
////                        modifier = Modifier
////                            .width(349.dp)
////                            .height(62.dp)
////                            .background(
////                                color = Color(0xFF272626), shape = RoundedCornerShape(size = 26.dp)
////                            ), verticalAlignment = Alignment.CenterVertically
////                    ) {
////                        Spacer(
////                            modifier = Modifier.size(20.dp)
////                        )
////                        Image(
////                            painter = painterResource(id = R.drawable.icon_share_locket),
////                            contentDescription = "iconsharelocket",
////                            modifier = Modifier
////                                .size(25.dp)
////                                .clip(RoundedCornerShape(size = 10.dp))
////                                .background(color = Color(0xFF545252))
////                        )
////                        Text(
////                            text = "   Chia sẻ Locket", style = androidx.compose.ui.text.TextStyle(
////                                color = Color(0xFFFFFFFF),
////                                fontSize = 15.sp,
////                                fontWeight = FontWeight.Bold,
////                                fontFamily = fontFamily
////                            ), modifier = Modifier.width(240.dp)
////                        )
////                        Button(
////                            onClick = { /*TODO*/ },
////                            colors = ButtonDefaults.buttonColors(Color(color = 0xFF272626))
////                        ) {
////                            Image(
////                                painter = painterResource(id = R.drawable.icon_vector_right),
////                                contentDescription = "iconvectorright",
////                                modifier = Modifier.size(15.dp)
////                            )
////                        }
////                    }
////
////                    Row(
////                        modifier = Modifier
////                            .width(349.dp)
////                            .height(62.dp)
////                            .background(
////                                color = Color(0xFF272626), shape = RoundedCornerShape(size = 26.dp)
////                            ), verticalAlignment = Alignment.CenterVertically
////                    ) {
////                        Spacer(
////                            modifier = Modifier.size(20.dp)
////                        )
////                        Image(
////                            painter = painterResource(id = R.drawable.icon_star),
////                            contentDescription = "iconstar",
////                            modifier = Modifier
////                                .size(25.dp)
////                                .clip(RoundedCornerShape(size = 10.dp))
////                                .background(color = Color(0xFF545252))
////                        )
////                        Text(
////                            text = "   Đánh giá Locket", style = androidx.compose.ui.text.TextStyle(
////                                color = Color(0xFFFFFFFF),
////                                fontSize = 15.sp,
////                                fontWeight = FontWeight.Bold,
////                                fontFamily = fontFamily
////                            ), modifier = Modifier.width(240.dp)
////                        )
////                        Button(
////                            onClick = { /*TODO*/ },
////                            colors = ButtonDefaults.buttonColors(Color(color = 0xFF272626))
////                        ) {
////                            Image(
////                                painter = painterResource(id = R.drawable.icon_vector_right),
////                                contentDescription = "iconvectorright",
////                                modifier = Modifier.size(15.dp)
////                            )
////                        }
////                    }
////
////                    Row(
////                        modifier = Modifier
////                            .width(349.dp)
////                            .height(62.dp)
////                            .background(
////                                color = Color(0xFF272626), shape = RoundedCornerShape(size = 26.dp)
////                            ), verticalAlignment = Alignment.CenterVertically
////                    ) {
////                        Spacer(
////                            modifier = Modifier.size(20.dp)
////                        )
////                        Image(
////                            painter = painterResource(id = R.drawable.icon_term_of_service),
////                            contentDescription = "icontermofservice",
////                            modifier = Modifier
////                                .size(25.dp)
////                                .clip(RoundedCornerShape(size = 10.dp))
////                                .background(color = Color(0xFF545252))
////                        )
////                        Text(
////                            text = "   Điều khoản dịch vụ",
////                            style = androidx.compose.ui.text.TextStyle(
////                                color = Color(0xFFFFFFFF),
////                                fontSize = 15.sp,
////                                fontWeight = FontWeight.Bold,
////                                fontFamily = fontFamily
////                            ),
////                            modifier = Modifier.width(240.dp)
////                        )
////                        Button(
////                            onClick = { /*TODO*/ },
////                            colors = ButtonDefaults.buttonColors(Color(color = 0xFF272626))
////                        ) {
////                            Image(
////                                painter = painterResource(id = R.drawable.icon_vector_right),
////                                contentDescription = "iconvectorright",
////                                modifier = Modifier.size(15.dp)
////                            )
////                        }
////                    }
////
////                    Row(
////                        modifier = Modifier
////                            .width(349.dp)
////                            .height(62.dp)
////                            .background(
////                                color = Color(0xFF272626), shape = RoundedCornerShape(size = 26.dp)
////                            ), verticalAlignment = Alignment.CenterVertically
////                    ) {
////                        Spacer(
////                            modifier = Modifier.size(20.dp)
////                        )
////                        Image(
////                            painter = painterResource(id = R.drawable.icon_lock),
////                            contentDescription = "iconlock",
////                            modifier = Modifier
////                                .size(25.dp)
////                                .clip(RoundedCornerShape(size = 10.dp))
////                                .background(color = Color(0xFF545252))
////                        )
////                        Text(
////                            text = "   Chính sách quyền riêng tư",
////                            style = androidx.compose.ui.text.TextStyle(
////                                color = Color(0xFFFFFFFF),
////                                fontSize = 15.sp,
////                                fontWeight = FontWeight.Bold,
////                                fontFamily = fontFamily
////                            ),
////                            modifier = Modifier.width(240.dp)
////                        )
////                        Button(
////                            onClick = { /*TODO*/ },
////                            colors = ButtonDefaults.buttonColors(Color(color = 0xFF272626))
////                        ) {
////                            Image(
////                                painter = painterResource(id = R.drawable.icon_vector_right),
////                                contentDescription = "iconvectorright",
////                                modifier = Modifier.size(15.dp)
////                            )
////                        }
////                    }
////                }
//
//                Spacer(modifier = Modifier.size(20.dp))
//
//                Row(
//                    modifier = Modifier
//                        .width(152.dp)
//                        .height(43.dp)
//                ) {
//                    Image(
//                        painter = painterResource(id = R.drawable.icon_danger_area),
//                        contentDescription = "icondangerarea",
//                        modifier = Modifier.size(18.dp)
//                    )
//                    Text(
//                        text = " Vùng nguy hiểm",
//                        style = androidx.compose.ui.text.TextStyle(
//                            color = Color(0xFF959292),
//                            fontSize = 17.sp,
//                            fontWeight = FontWeight.Bold,
//                            fontFamily = fontFamily
//                        ),
//                    )
//                }
//
//                Column(
//                    modifier = Modifier
//                        .width(349.dp)
//                        .height(120.dp)
//                        .background(
//                            color = Color(0xFF272626), shape = RoundedCornerShape(size = 26.dp)
//                        )
//                ) {
//                    Row(
//                        modifier = Modifier
//                            .width(349.dp)
//                            .height(62.dp)
//                            .background(
//                                color = Color(0xFF272626), shape = RoundedCornerShape(size = 26.dp)
//                            ), verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Spacer(
//                            modifier = Modifier.size(20.dp)
//                        )
//                        Image(
//                            painter = painterResource(id = R.drawable.icon_delete_account),
//                            contentDescription = "icondeleteaccount",
//                            modifier = Modifier
//                                .size(25.dp)
//                                .clip(RoundedCornerShape(size = 10.dp))
//                                .background(color = Color(0xFF545252))
//                        )
//                        Text(
//                            text = "   Xóa tài khoản", style = androidx.compose.ui.text.TextStyle(
//                                color = Color(0xFFFFFFFF),
//                                fontSize = 15.sp,
//                                fontWeight = FontWeight.Bold,
//                                fontFamily = fontFamily
//                            ), modifier = Modifier.width(240.dp)
//                        )
//                        Button(
//                            onClick = { /*TODO*/ },
//                            colors = ButtonDefaults.buttonColors(Color(color = 0xFF272626))
//                        ) {
//                            Image(
//                                painter = painterResource(id = R.drawable.icon_vector_right),
//                                contentDescription = "iconvectorright",
//                                modifier = Modifier.size(15.dp)
//                            )
//                        }
//                    }
//
//                    Row(
//                        modifier = Modifier
//                            .width(349.dp)
//                            .height(62.dp)
//                            .background(
//                                color = Color(0xFF272626), shape = RoundedCornerShape(size = 26.dp)
//                            ), verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Spacer(
//                            modifier = Modifier.size(20.dp)
//                        )
//                        Image(
//                            painter = painterResource(id = R.drawable.icon_logout),
//                            contentDescription = "iconlogout",
//                            modifier = Modifier
//                                .size(25.dp)
//                                .clip(RoundedCornerShape(size = 10.dp))
//                                .background(color = Color(0xFF545252))
//                        )
//                        Text(
//                            text = "   Đăng xuất", style = androidx.compose.ui.text.TextStyle(
//                                color = Color(0xFFFFFFFF),
//                                fontSize = 15.sp,
//                                fontWeight = FontWeight.Bold,
//                                fontFamily = fontFamily
//                            ), modifier = Modifier.width(240.dp)
//                        )
//                        Button(
//                            onClick = { /*TODO*/ },
//                            colors = ButtonDefaults.buttonColors(Color(color = 0xFF272626))
//                        ) {
//                            Image(
//                                painter = painterResource(id = R.drawable.icon_vector_right),
//                                contentDescription = "iconvectorright",
//                                modifier = Modifier.size(15.dp)
//                            )
//                        }
//                    }
//                }
//                Spacer(modifier = Modifier.size(20.dp))
//            }
//        }
//    }
//}