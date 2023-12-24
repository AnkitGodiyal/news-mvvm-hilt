package com.ankit.news.utils

import com.ankit.news.BuildConfig

object Constants {
    const val API_KEY = BuildConfig.API_KEY
    const val BASE_URL = "https://newsapi.org"
    const val searchTimeDelay = 500L
    const val QUERY_PER_PAGE = 10
    const val DEFAULT_PAGE_INDEX = 1
    const val CountryCode = "us"
}