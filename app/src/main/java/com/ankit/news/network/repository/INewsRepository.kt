package com.ankit.news.network.repository

import androidx.lifecycle.LiveData
import com.ankit.news.state.NetworkState
import com.ankit.news.data.model.NewsArticle
import com.ankit.news.data.model.NewsResponse

interface INewsRepository {
    suspend fun getNews(countryCode: String, pageNumber: Int): NetworkState<NewsResponse>

    suspend fun searchNews(searchQuery: String, pageNumber: Int): NetworkState<NewsResponse>

    suspend fun saveNews(news: NewsArticle): Long

    fun getSavedNews(): LiveData<List<NewsArticle>>

    suspend fun deleteNews(news: NewsArticle)

    suspend fun deleteAllNews()
}
