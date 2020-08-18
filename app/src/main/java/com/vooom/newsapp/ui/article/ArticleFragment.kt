package com.vooom.newsapp.ui.article

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.adapters.GenericFastItemAdapter
import com.mikepenz.fastadapter.adapters.GenericItemAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.fastadapter.scroll.EndlessRecyclerOnScrollListener
import com.vooom.newsapp.R
import com.vooom.newsapp.network.ArticleResponse
import com.vooom.newsapp.network.ServiceConfiguration
import com.vooom.newsapp.ui.details.DetailsActivity
import kotlinx.android.synthetic.main.fragment_article_list.*
import retrofit2.Call
import retrofit2.Callback


/**
 * Fragment to show a list of articles from a list of sources
 */
class ArticleFragment : Fragment() {
    protected var viewModel: ArticleListViewModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ArticleListViewModel::class.java)
        arguments?.apply {
            viewModel?.initializeTop(getString("section", ""), sources = getString("sources"))
        }
    }

    companion object {
        fun newInstance(section: String, sources: String? = null): ArticleFragment {
            val articleFragment = ArticleFragment()
            with(Bundle()) {
                putString("sources", sources)
                putString("section", section)
                articleFragment.arguments = this
            }
            return articleFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            inflater.inflate(R.layout.fragment_article_list, container, false)


        //set our adapters to the RecyclerView
//        view.adapter = fastAdapter


        pageNumber = 1
//        view.adapter = adapter
        observeArticles(pageNumber)

        return view
    }

    private fun observeArticles(pageNumber: Int) {


        viewModel?.articles?.observe(viewLifecycleOwner, Observer { articleResponse ->
            if (articleResponse == null) return@Observer


            val list = ArrayList<ListItem>()
            articleResponse.articles.forEach {
                list.add(ListItem(it))
            }
            itemAdapter.add(list)
            fastItemAdapter.add(list)
        })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list.layoutManager = LinearLayoutManager(view.context)
        list.itemAnimator = DefaultItemAnimator()
        initAdapter()

        editText.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(editText.text.toString())
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun performSearch(query: String) {
        initAdapter()
        observeSearchArticles(query = query)



    }


    //create the ItemAdapter holding your Items
    val itemAdapter = ItemAdapter<ListItem>()

    //create the managing FastAdapter, by passing in the itemAdapter
    val fastAdapter = FastAdapter.with(itemAdapter)

    //save our FastAdapter
    private lateinit var fastItemAdapter: GenericFastItemAdapter
    private lateinit var footerAdapter: GenericItemAdapter

    //endless scroll
    private lateinit var endlessRecyclerOnScrollListener: EndlessRecyclerOnScrollListener
    private var pageNumber = 1

    private fun initAdapter() {

        System.gc()


        //create our FastAdapter which will manage everything
        fastItemAdapter = FastItemAdapter()
//        val selectExtension = fastItemAdapter.getSelectExtension()
//        selectExtension.isSelectable = true

        //create our FooterAdapter which will manage the progress items
        footerAdapter = ItemAdapter.items()
        fastItemAdapter.addAdapter(1, footerAdapter)


        list.adapter = fastItemAdapter


        pageNumber = 1
        endlessRecyclerOnScrollListener = object : EndlessRecyclerOnScrollListener(footerAdapter) {
            override fun onLoadMore(currentPage: Int) {

//                footerAdapter.clear()
//                val progressItem = ProgressItem()
//                progressItem.isEnabled = false
//                footerAdapter.add(progressItem)


                pageNumber++
                observeMoreArticles(pageNumber)
                Log.e("pageNumber", " $pageNumber")


            }
        }
        list.addOnScrollListener(endlessRecyclerOnScrollListener)


        fastItemAdapter.addEventHook(object : ClickEventHook<ListItem>() {

            override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
                //return the views on which you want to bind this event
                return if (viewHolder is ListItem.ViewHolder) {
                    viewHolder.itemView
                } else null
            }

            override fun onClick(
                v: View,
                position: Int,
                fastAdapter: FastAdapter<ListItem>,
                item: ListItem
            ) {

                Intent(requireContext(), DetailsActivity::class.java).apply {

                    putExtra("listItem", Gson().toJson(item))

                    startActivity(this)
//                    finish()
                }
            }

        })

    }


    private fun observeMoreArticles(pageNumber: Int) {
        viewModel?.loadMoreTop(
            page = pageNumber.toString(),
            section = "top",
            sources = "reuters,google-news"
        )?.observe(viewLifecycleOwner, Observer { articleResponse ->
            if (articleResponse == null) {
                footerAdapter.clear()
                return@Observer
            }


            val list = ArrayList<ListItem>()
            articleResponse.articles.forEach {
                list.add(ListItem(it))
            }
            fastItemAdapter.add(list)
        })
    }


    private fun observeSearchArticles(query: String) {


        ServiceConfiguration().service.everything(query, pageSize = 50,page = 1).enqueue(
            object : Callback<ArticleResponse> {
                override fun onFailure(call: Call<ArticleResponse>, t: Throwable) {


                }

                override fun onResponse(
                    call: Call<ArticleResponse>,
                    response: retrofit2.Response<ArticleResponse>
                ) {

                    val list = ArrayList<ListItem>()
                    response.body()!!.articles.forEach {
                        list.add(ListItem(it))
                    }
                    fastItemAdapter.add(list)
                }

            }
        )


    }
}
