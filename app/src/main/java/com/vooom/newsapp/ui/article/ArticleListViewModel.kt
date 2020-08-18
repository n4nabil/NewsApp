package com.vooom.newsapp.ui.article


import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.vooom.newsapp.dp.DataRepository
import com.vooom.newsapp.network.ArticleResponse

class ArticleListViewModel : ViewModel() {

    var articles: LiveData<ArticleResponse>? = null

    fun initializeTop(section: String, country: String? = null, category: String? = null, sources: String? = null,
                      keyword: String? = null)  {
        articles = articles ?: DataRepository.top(section, country, category, sources, keyword)

    }


    fun loadMoreTop(page: String,section: String, country: String? = null, category: String? = null, sources: String? = null,
                      keyword: String? = null): MediatorLiveData<ArticleResponse> {
//        clear(section)
        return DataRepository.loadMoreTop(section, country, category, sources, keyword,page = page.toInt())
    }



    fun searchEverything(section: String, query: String): MediatorLiveData<ArticleResponse> {
        clear(section)
        return DataRepository.searchEverything(section, query,"")
    }

    fun initializeLocal(section: String, city: String, state: String) {
        articles = articles ?: DataRepository.local(section, city, state)
    }

    fun clear(section: String, lifecycleOwner: LifecycleOwner?=null) {
        DataRepository.clearSection(section)
//        articles?.removeObservers(lifecycleOwner)
//        articles = null
    }



}
