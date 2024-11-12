package com.dicoding.submission1int.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _email = MutableLiveData<String>()
    private val _password = MutableLiveData<String>()
    private val _name = MutableLiveData<String>()

    val email: LiveData<String> = _email
    val password: LiveData<String> = _password
    val name: LiveData<String> = _name

    fun setEmail(email: String) {
        viewModelScope.launch {
            _email.value = email
        }
    }

    fun setPassword(password: String) {
        viewModelScope.launch {
            _password.value = password
        }
    }

    fun setName(name: String) {
        viewModelScope.launch {
            _name.value = name
        }
    }
}