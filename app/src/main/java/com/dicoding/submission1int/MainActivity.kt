package com.dicoding.submission1int

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
import com.dicoding.submission1int.data.model.Story
import com.dicoding.submission1int.data.model.StoryResponse
import com.dicoding.submission1int.databinding.ActivityMainBinding
import com.dicoding.submission1int.remote.NetworkClient
import com.dicoding.submission1int.ui.LoginActivity
import com.dicoding.submission1int.ui.maps.MapsActivity
import com.dicoding.submission1int.ui.story.AddStoryActivity
import com.dicoding.submission1int.ui.story.DetailStoryActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("session", MODE_PRIVATE)

        if (!isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setupRecyclerView()
        setupFabAnimation()
        getStories()


        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }
    }


    private fun setupFabAnimation() {
        binding.fabAdd.setOnClickListener {

            it.animate()
                .rotation(360f)
                .setDuration(500)
                .withEndAction {
                    it.rotation = 0f
                    startActivity(Intent(this, AddStoryActivity::class.java))
                }
                .start()
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
            R.id.action_maps -> {
                startActivity(Intent(this, MapsActivity::class.java))
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

    private fun isLoggedIn(): Boolean {
        return !sharedPreferences.getString("token", "").isNullOrEmpty()
    }



    private fun setupRecyclerView() {
        storyAdapter = StoryAdapter()
        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = storyAdapter
            setHasFixedSize(true)
        }

        storyAdapter.setOnItemClickCallback(object : StoryAdapter.OnItemClickCallback {
            override fun onItemClicked(story: Story) {
                val intent = Intent(this@MainActivity, DetailStoryActivity::class.java)
                intent.putExtra(DetailStoryActivity.EXTRA_STORY, story)
                startActivity(intent)
            }
        })
    }

    private fun getStories() {
        val token = sharedPreferences.getString("token", "") ?: ""
        showLoading(true)

        val call = NetworkClient.apiInterface.getStories("Bearer $token")
        call.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                showLoading(false)
                if (response.isSuccessful) {
                    response.body()?.let { storyResponse ->
                        if (!storyResponse.error) {
                            storyAdapter.setStories(storyResponse.listStory)
                        } else {
                            Toast.makeText(this@MainActivity, storyResponse.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Failed to get stories", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                showLoading(false)
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()
        getStories()
    }
}