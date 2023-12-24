package com.ankit.news.ui.feed

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ankit.news.R
import com.ankit.news.base.BaseFragment
import com.ankit.news.databinding.FragmentFeedBinding
import com.ankit.news.state.NetworkState
import com.ankit.news.ui.adapter.NewsAdapter
import com.ankit.news.ui.details.DetailsFragment
import com.ankit.news.ui.main.MainViewModel
import com.ankit.news.utils.Constants
import com.ankit.news.utils.Constants.QUERY_PER_PAGE
import com.ankit.news.utils.EndlessRecyclerOnScrollListener
import com.ankit.news.utils.FragmentExtensions.openFragment


class FeedFragment : BaseFragment<FragmentFeedBinding>() {
    override fun setBinding(): FragmentFeedBinding =
        FragmentFeedBinding.inflate(layoutInflater)

    private lateinit var onScrollListener: EndlessRecyclerOnScrollListener
    val mainViewModel: MainViewModel? by lazy {
        activity?.let { ViewModelProvider(it) }?.get(MainViewModel::class.java)
    }
    private lateinit var newsAdapter: NewsAdapter
    val countryCode = Constants.CountryCode
    private lateinit var searchView: SearchView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupRecyclerView()
        setupObservers()
        setHasOptionsMenu(true)
    }

    private fun setupUI() {
        binding.itemErrorMessage.btnRetry.setOnClickListener {
            if (mainViewModel?.searchEnable == true) {
                mainViewModel?.searchNews(mainViewModel?.newQuery ?: "")
            } else {
                mainViewModel?.fetchNews(countryCode)
            }
            hideErrorMessage()
        }

        // scroll listener for recycler view
        onScrollListener = object : EndlessRecyclerOnScrollListener(QUERY_PER_PAGE) {
            override fun onLoadMore() {
                if (mainViewModel?.searchEnable == true) {
                    mainViewModel?.searchNews(mainViewModel?.newQuery ?: "")
                } else {
                    mainViewModel?.fetchNews(countryCode)
                }
            }
        }

        //Swipe refresh listener
        val refreshListener = SwipeRefreshLayout.OnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            mainViewModel?.clearSearch()
            mainViewModel?.fetchNews(countryCode)
        }
        binding.swipeRefreshLayout.setOnRefreshListener(refreshListener)
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.rvNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(onScrollListener)
        }
        newsAdapter.setOnItemClickListener { news ->
            val bundle = Bundle().apply {
                putSerializable("news", news)
            }
            parentFragmentManager.openFragment(
                R.id.flFragment,
                DetailsFragment().apply { arguments = bundle })
        }
    }

    private fun setupObservers() {
        lifecycleScope.launchWhenStarted {
            if (mainViewModel?.searchEnable != true) {
                mainViewModel?.newsResponse?.collect { response ->
                    when (response) {
                        is NetworkState.Success -> {
                            hideProgressBar()
                            hideErrorMessage()
                            response.data?.let { newResponse ->
                                newsAdapter.differ.submitList(newResponse.articles.toList())
                                mainViewModel?.totalPage =
                                    newResponse.totalResults / QUERY_PER_PAGE + 1
                                onScrollListener.isLastPage =
                                    mainViewModel?.feedNewsPage == (mainViewModel?.totalPage
                                        ?: 0) + 1
                                hideBottomPadding()
                            }
                        }

                        is NetworkState.Loading -> {
                            showProgressBar()
                        }

                        is NetworkState.Error -> {
                            hideProgressBar()
                            response.message?.let {
                                showErrorMessage(response.message)
                            }
                        }

                        else -> {}
                    }
                }
            } else {
                collectSearchResponse()
            }
        }

        lifecycleScope.launchWhenStarted {
            mainViewModel?.errorMessage?.collect { value ->
                if (value.isNotEmpty()) {
                    Toast.makeText(activity, value, Toast.LENGTH_LONG).show()
                }
                mainViewModel?.hideErrorToast()
            }
        }
    }

    private fun collectSearchResponse() {
        //Search response
        lifecycleScope.launchWhenStarted {
            if (mainViewModel?.searchEnable == true) {
                mainViewModel?.searchNewsResponse?.collect { response ->
                    when (response) {
                        is NetworkState.Success -> {
                            hideProgressBar()
                            hideErrorMessage()
                            response.data?.let { searchResponse ->
                                newsAdapter.differ.submitList(searchResponse.articles.toList())
                                mainViewModel?.totalPage =
                                    searchResponse.totalResults / QUERY_PER_PAGE + 1
                                onScrollListener.isLastPage =
                                    mainViewModel?.searchNewsPage == (mainViewModel?.totalPage
                                        ?: 0) + 1
                                hideBottomPadding()
                            }
                        }

                        is NetworkState.Loading -> {
                            showProgressBar()
                        }

                        is NetworkState.Error -> {
                            hideProgressBar()
                            response.message?.let {
                                showErrorMessage(response.message)
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showErrorMessage(message: String) {
        binding.itemErrorMessage.errorCard.visibility = View.VISIBLE
        binding.itemErrorMessage.tvErrorMessage.text = message
        onScrollListener.isError = true
    }

    private fun hideErrorMessage() {
        binding.itemErrorMessage.errorCard.visibility = View.GONE
        onScrollListener.isError = false
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView
        //Search button clicked
        searchView.setOnSearchClickListener {
            searchView.maxWidth = android.R.attr.width
        }
        //Close button clicked
        searchView.setOnCloseListener {
            mainViewModel?.clearSearch()
            mainViewModel?.fetchNews(countryCode)
            //Collapse the action view
            searchView.onActionViewCollapsed()
            searchView.maxWidth = 0
            true
        }

        val searchPlate =
            searchView.findViewById(androidx.appcompat.R.id.search_src_text) as EditText
        searchPlate.hint = "Search"
        val searchPlateView: View =
            searchView.findViewById(androidx.appcompat.R.id.search_plate)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    mainViewModel?.searchNews(query)
                    mainViewModel?.enableSearch()
                    collectSearchResponse()
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        activity?.let {
            searchPlateView.setBackgroundColor(
                ContextCompat.getColor(
                    it,
                    android.R.color.transparent
                )
            )
            val searchManager =
                it.getSystemService(Context.SEARCH_SERVICE) as SearchManager
            searchView.setSearchableInfo(searchManager.getSearchableInfo(it.componentName))
        }
        //check if search is activated
        if (mainViewModel?.searchEnable == true) {
            searchView.isIconified = false
            searchItem.expandActionView()
            searchView.setQuery(mainViewModel?.newQuery ?: "", false)
        }
        return super.onCreateOptionsMenu(menu, inflater)
    }

    private fun hideBottomPadding() {
        if (onScrollListener.isLastPage) {
            binding.rvNews.setPadding(0, 0, 0, 0)
        }
    }
}