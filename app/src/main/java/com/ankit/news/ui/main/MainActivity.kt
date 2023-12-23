package com.ankit.news.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.ankit.news.R
import com.ankit.news.base.BaseActivity
import com.ankit.news.databinding.ActivityMainBinding
import com.ankit.news.ui.favorite.FavoriteFragment
import com.ankit.news.ui.feed.FeedFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    val mainViewModel: MainViewModel by viewModels()

    override fun onViewReady(savedInstanceState: Bundle?) {
        super.onViewReady(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Today's News"
        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        }

        savedInstanceState?.let {
            mainViewModel.hideErrorToast()
        }
    }

    override fun setBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    private fun setupBottomNavigationBar() {

        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.feedFragment -> replaceFragment(FeedFragment())
                R.id.favoriteFragment -> replaceFragment(FavoriteFragment())
            }
            true
        }

        // Set the initial fragment
        replaceFragment(FeedFragment())
    }


    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.flFragment.id, fragment)
            .commit()
    }
}