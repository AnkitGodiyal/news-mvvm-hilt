package com.ankit.news.network.api

import com.ankit.news.data.model.NewsResponse
import retrofit2.Response

interface ApiHelper {
    suspend fun searchNews(query: String, pageNumber: Int): Response<NewsResponse>
    suspend fun getNews(countryCode: String, pageNumber: Int): Response<NewsResponse>
}