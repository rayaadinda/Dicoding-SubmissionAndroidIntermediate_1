package com.dicoding.submission1int.ui.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagingData
import com.dicoding.submission1int.data.model.Story
import com.dicoding.submission1int.data.repository.StoryRepository

class StoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getStories(token: String): LiveData<PagingData<Story>> =
        storyRepository.getStories(token)
}

class ViewModelFactory(private val repository: StoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}