package com.ankit.news.ui.details

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.core.view.isGone
import com.ankit.news.base.BaseFragment
import com.ankit.news.data.model.NewsArticle
import com.ankit.news.databinding.FragmentDetailsBinding
import com.ankit.news.ui.main.MainActivity
import com.ankit.news.ui.main.MainViewModel
import com.ankit.news.utils.FragmentExtensions.getSerializableObject
import com.google.android.material.snackbar.Snackbar

class DetailsFragment : BaseFragment<FragmentDetailsBinding>() {

    override fun setBinding(): FragmentDetailsBinding =
        FragmentDetailsBinding.inflate(layoutInflater)

    private lateinit var viewModel: MainViewModel
    private val args: NewsArticle? by lazy {
        arguments?.getSerializableObject(
            "news",
            NewsArticle::class.java
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).mainViewModel
        setupUI(view)
        setupObserver()
    }

    private fun setupUI(view: View) {
        binding.webView.apply {
            webViewClient = WebViewClient()
            args?.url?.let {
                loadUrl(it)
            }
        }

        binding.fab.setOnClickListener {
            args?.let { it1 -> viewModel.saveNews(it1) }
            Snackbar.make(view, "News article added to favorites.", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun setupObserver() {
        viewModel.getFavoriteNews().observe(viewLifecycleOwner) { news ->
            binding.fab.isGone = news.any { it.title == args?.title }
        }
    }
}