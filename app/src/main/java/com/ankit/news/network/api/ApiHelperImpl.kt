package com.ankit.news.network.api

import com.ankit.news.data.model.NewsResponse
import retrofit2.Response
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(private val newsApi: NewsApi) : ApiHelper {
    override suspend fun searchNews(query: String, pageNumber: Int): Response<NewsResponse> {
        return newsApi.searchNews(query, pageNumber)
    }

    override suspend fun getNews(countryCode: String, pageNumber: Int): Response<NewsResponse> {
        return newsApi.getNews(countryCode, pageNumber)
    }
}