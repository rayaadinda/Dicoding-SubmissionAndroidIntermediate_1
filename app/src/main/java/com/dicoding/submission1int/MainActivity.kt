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
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.submission1int.data.adapter.LoadingStateAdapter
import com.dicoding.submission1int.data.model.Story
import com.dicoding.submission1int.data.repository.StoryRepository
import com.dicoding.submission1int.databinding.ActivityMainBinding
import com.dicoding.submission1int.remote.NetworkClient
import com.dicoding.submission1int.ui.LoginActivity
import com.dicoding.submission1int.ui.maps.MapsActivity
import com.dicoding.submission1int.ui.story.AddStoryActivity
import com.dicoding.submission1int.ui.story.DetailStoryActivity
import com.dicoding.submission1int.ui.story.StoryViewModel
import com.dicoding.submission1int.ui.story.ViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var viewModel: StoryViewModel
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



        setupViewModel()
        setupRecyclerView()
        getStories()
        setupFabAnimation()


        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupViewModel() {
        val repository = StoryRepository(NetworkClient.apiInterface)
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[StoryViewModel::class.java]
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
            adapter = storyAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter { storyAdapter.retry() }
            )
        }


        storyAdapter.addLoadStateListener { loadState ->

            showLoading(loadState.source.refresh is LoadState.Loading)


            val errorState = loadState.source.refresh as? LoadState.Error
            errorState?.let {
                Toast.makeText(
                    this,
                    "Error: ${it.error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
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
        viewModel.getStories(token).observe(this) {
            storyAdapter.submitData(lifecycle, it)
        }
    }

        private fun showLoading(isLoading: Boolean) {
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

    override fun onResume() {
        super.onResume()
        getStories()
    }
}