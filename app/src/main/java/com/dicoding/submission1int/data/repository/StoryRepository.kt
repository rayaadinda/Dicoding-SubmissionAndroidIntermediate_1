package com.dicoding.submission1int.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.submission1int.data.model.Story
import com.dicoding.submission1int.data.paging.StoryPagingSource
import com.dicoding.submission1int.remote.ApiInterface

class StoryRepository(private val apiService: ApiInterface) {
    fun getStories(token: String): LiveData<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService, token)
            }
        ).liveData
    }
}