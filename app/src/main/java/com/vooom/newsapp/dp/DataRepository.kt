package com.vooom.newsapp.dp



import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.vooom.newsapp.network.ArticleResponse
import com.vooom.newsapp.network.NetworkResource
import com.vooom.newsapp.network.ServiceConfiguration
import org.jetbrains.anko.doAsync
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLEncoder


object DataRepository {

    private const val CACHE_TIME: Int = 15 * 60 * 1000
    var lastPage: Int = 1

    abstract class ArticleNetworkResponse(private val sectionName: String) : NetworkResource<ArticleResponse>() {
        override fun loadFromDb(): LiveData<ArticleResponse> {
            // transforms list of articles from database into same object as API call,
            // to be used later for paging
            val data = MyDatabase.db.articleDao().getBySection(sectionName)
            return Transformations.map(data) {
                ArticleResponse(it.size, it)
            }
        }

        override fun shouldDownload(): Boolean {
            val section = MyDatabase.db.articleDao().findSectionByName(sectionName)
            return if (section == null || lastPage != 1) {
                Log.e("Page" , " true")
                true
            } else {
                System.currentTimeMillis() - section.lastUpdated > CACHE_TIME
            }
        }

        abstract override fun createCall(): LiveData<ApiResponse<ArticleResponse?>>

        override fun saveResult(data: ArticleResponse) {
            MyDatabase.db.articleDao().insertAll(sectionName, data.articles)
        }

        override fun onFetchFailed() {
            //TODO: return failure response to show empty data message to user
        }
    }

    fun top(sectionName: String, country: String? = null, category: String? = null, sources: String? = null,
            keyword: String? = null, pageSize: Int = 50, page: Int = 1) : MediatorLiveData<ArticleResponse> {
        lastPage = page
        Log.e("Page" , " Page  $lastPage")
        return object : ArticleNetworkResponse(sectionName) {

            override fun createCall(): LiveData<ApiResponse<ArticleResponse?>> {
                return ServiceConfiguration().service.top(country?.toLowerCase(), category, sources,
                        keyword, pageSize, page).executeLiveData()
            }
        }.init().getLiveData()
    }

    fun loadMoreTop(sectionName: String, country: String? = null, category: String? = null, sources: String? = null,
            keyword: String? = null, pageSize: Int = 50, page: Int = 1) : MediatorLiveData<ArticleResponse> {
        lastPage = page
        Log.e("Page" , " Page  $lastPage")
        return object : ArticleNetworkResponse(sectionName) {

            override fun createCall(): LiveData<ApiResponse<ArticleResponse?>> {
                return ServiceConfiguration().service.top(country?.toLowerCase(), category, sources,
                        keyword, pageSize, page).executeLiveData()
            }
        }.init().getLiveData()
    }

    fun local(sectionName: String, city: String, state: String, pageSize: Int = 10, page: Int = 1) : MediatorLiveData<ArticleResponse> {
        val query = URLEncoder.encode("$city OR $state", "utf-8")
        return object : ArticleNetworkResponse(sectionName) {

            override fun createCall(): LiveData<ApiResponse<ArticleResponse?>> {
                return ServiceConfiguration().service.everything(query, pageSize=pageSize, page=page).executeLiveData()
            }

        }.init().getLiveData()

    }

    fun searchEverything(sectionName: String, query: String, state: String, pageSize: Int = 50, page: Int = 1) : MediatorLiveData<ArticleResponse> {
        lastPage=10
        return object : ArticleNetworkResponse(sectionName) {

            override fun createCall(): LiveData<ApiResponse<ArticleResponse?>> {
                return ServiceConfiguration().service.everything(query, pageSize=pageSize, page=page).executeLiveData()
            }

        }.init().getLiveData()

    }

    fun clearSection(sectionName: String) {
        doAsync {
            MyDatabase.db.articleDao().clearSection(sectionName)
        }
    }

    // wrap retrofit call into function that returns MutableLiveData instance
    private fun <T> Call<T>.executeLiveData(): MutableLiveData<ApiResponse<T?>> {
        val data = MutableLiveData<ApiResponse<T?>>()
        enqueue(object: Callback<T> {
            override fun onFailure(call: Call<T>?, t: Throwable) {
                data.value = ApiResponse(null)
            }

            override fun onResponse(call: Call<T>?, response: Response<T>) {
                data.value = ApiResponse(response)
            }
        })
        return data
    }


}

class ApiResponse<out T>(response: Response<T>?) {

    val isSuccessful = response?.isSuccessful ?: false
    val data = response?.body()


}
