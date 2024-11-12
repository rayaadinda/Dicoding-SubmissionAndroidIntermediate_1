package com.dicoding.submission1int.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.submission1int.MainActivity
import com.dicoding.submission1int.data.model.LoginResponse
import com.dicoding.submission1int.databinding.ActivityLoginBinding
import com.dicoding.submission1int.remote.NetworkClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("session", MODE_PRIVATE)

        if (isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setupAction()
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            when {
                email.isEmpty() -> {
                    binding.edLoginEmail.error = "Email is required"
                    binding.edLoginEmail.requestFocus()
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    binding.edLoginEmail.error = "Invalid email format"
                    binding.edLoginEmail.requestFocus()
                }
                password.isEmpty() -> {
                    binding.edLoginPassword.error = "Password is required"
                    binding.edLoginPassword.requestFocus()
                }
                password.length < 8 -> {
                    binding.edLoginPassword.error = "Password must be at least 8 characters"
                    binding.edLoginPassword.requestFocus()
                }
                else -> {
                    loginUser(email, password)
                }
            }
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
    private fun isLoggedIn(): Boolean {
        return !sharedPreferences.getString("token", "").isNullOrEmpty()
    }

    private fun loginUser(email: String, password: String) {
        showLoading(true)
        val loginRequest = LoginRequest(email, password)
        val call = NetworkClient.apiInterface.loginUser(loginRequest)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                showLoading(false)
                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        if (!loginResponse.error) {

                            sharedPreferences.edit().apply {
                                putString("token", loginResponse.loginResult.token)
                                putString("userId", loginResponse.loginResult.userId)
                                putString("name", loginResponse.loginResult.name)
                                apply()
                            }


                            startActivity(Intent(this@LoginActivity, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            })
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, loginResponse.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                showLoading(false)
                Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }



    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }


}