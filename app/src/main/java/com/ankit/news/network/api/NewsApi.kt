package com.ankit.news.network.api

import com.ankit.news.utils.Constants.API_KEY
import com.ankit.news.utils.Constants.CountryCode
import com.ankit.news.utils.Constants.QUERY_PER_PAGE
import com.ankit.news.data.model.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("v2/top-headlines")
    suspend fun getNews(
        @Query("country")
        countryCode: String = CountryCode,
        @Query("page")
        pageNumber: Int = 1,
        @Query("pageSize")
        pageSize: Int = QUERY_PER_PAGE,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>

    @GET("v2/everything")
    suspend fun searchNews(
        @Query("q")
        searchQuery: String,
        @Query("page")
        pageNumber: Int = 1,
        @Query("pageSize")
        pageSize: Int = QUERY_PER_PAGE,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>
}