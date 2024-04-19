package com.grouptwo.lokcet.view.login

import android.accounts.Account
import com.grouptwo.lokcet.di.service.AccountService
import com.grouptwo.lokcet.view.LokcetViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(val accountService: AccountService) : LokcetViewModel(){

}