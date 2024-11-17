package com.dicoding.submission1int.ui.story

import com.dicoding.submission1int.data.adapter.StoryAdapter
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.submission1int.R
import com.dicoding.submission1int.data.model.Story
import com.dicoding.submission1int.data.model.StoryResponse
import com.dicoding.submission1int.databinding.ActivityListStoryBinding
import com.dicoding.submission1int.remote.NetworkClient
import com.dicoding.submission1int.ui.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListStoryBinding
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("session", MODE_PRIVATE)
        setupRecyclerView()


        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        sharedPreferences.edit().clear().apply()

        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }


    private fun setupRecyclerView() {
        storyAdapter = StoryAdapter()
        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(this@ListStoryActivity)
            adapter = storyAdapter
            setHasFixedSize(true)
        }

        storyAdapter.setOnItemClickCallback(object : StoryAdapter.OnItemClickCallback {
            override fun onItemClicked(story: Story) {
                val intent = Intent(this@ListStoryActivity, DetailStoryActivity::class.java)
                intent.putExtra(DetailStoryActivity.EXTRA_STORY, story)
                startActivity(intent)
            }
        })
    }

//    private fun getStories() {
//        val token = sharedPreferences.getString("token", "") ?: ""
//        showLoading(true)
//
//        val call = NetworkClient.apiInterface.getStories("Bearer $token")
//        call.enqueue(object : Callback<StoryResponse> {
//            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
//                showLoading(false)
//                if (response.isSuccessful) {
//                    response.body()?.let { storyResponse ->
//                        if (!storyResponse.error) {
//                            storyAdapter.setStories(storyResponse.listStory)
//                        } else {
//                            Toast.makeText(this@ListStoryActivity, storyResponse.message, Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                } else {
//                    Toast.makeText(this@ListStoryActivity, "Failed to get stories", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
//                showLoading(false)
//                Toast.makeText(this@ListStoryActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }



    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()

    }
}