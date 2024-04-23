package com.grouptwo.lokcet.view.message

import com.grouptwo.lokcet.view.LokcetViewModel

class MessageViewModel : LokcetViewModel() {

    fun onBackClick(popUp: () -> Unit) {
        popUp()
    }
}