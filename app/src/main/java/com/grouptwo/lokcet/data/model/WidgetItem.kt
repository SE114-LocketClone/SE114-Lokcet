package com.grouptwo.lokcet.data.model

import androidx.annotation.DrawableRes
import com.grouptwo.lokcet.R.drawable as Image

data class WidgetItem(
    val id: Int,
    val title: String,
    val description: String,
    @DrawableRes val image: Int
)

// Mock data for the add_widget items list
val widgetItems = listOf(
    WidgetItem(
        id = 1,
        title = "Bước 1",
        description = "Nhấn giữ trên bất kì ứng dụng nào\nđể sửa Màn hình chính của bạn",
        image = Image.widget_1
    ),
    WidgetItem(
        id = 2,
        title = "Bước 2",
        description = "Chạm vào nút Tiện ích và\ntìm Lokcet",
        image = Image.widget_2
    ),
    WidgetItem(
        id = 3,
        title = "Bước 3",
        description = "Chạm và giữ Lokcet và kéo nó\nđến màn hình chính",
        image = Image.widget_3
    ),
    WidgetItem(
        id = 4,
        title = "Bước 4",
        description = "Chạm vào tiện ích và\nchọn một người bạn",
        image = Image.widget_4
    ),
)