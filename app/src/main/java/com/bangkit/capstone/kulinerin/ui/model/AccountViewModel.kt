package com.bangkit.capstone.kulinerin.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AccountViewModel : ViewModel() {

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> get() = _name

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> get() = _password

    private val _otp = MutableLiveData<String>()
    val otp: LiveData<String> get() = _otp

    fun setName(name: String) {
        _name.value = name
    }

    fun setEmail(email: String) {
        _email.value = email
    }

    fun setPassword(password: String) {
        _password.value = password
    }

    fun setOtp(otp: String) {
        _otp.value = otp
    }
}
