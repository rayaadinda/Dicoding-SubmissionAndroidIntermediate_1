package com.dicoding.submission1int.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.submission1int.data.model.Story
import com.dicoding.submission1int.remote.ApiInterface

class StoryPagingSource(
    private val apiService: ApiInterface,
    private val token: String
) : PagingSource<Int, Story>() {

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val response = apiService.getStoriesPaging("Bearer $token", position, params.loadSize)

            if (!response.error) {
                LoadResult.Page(
                    data = response.listStory,
                    prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                    nextKey = if (response.listStory.isEmpty()) null else position + 1
                )
            } else {
                LoadResult.Error(Exception(response.message))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    companion object {
        private const val INITIAL_PAGE_INDEX = 1
    }
}