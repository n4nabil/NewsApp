package com.vooom.newsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vooom.newsapp.ui.article.ArticleFragment

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
//                .replace(R.id.container, HomeFragment.newInstance())
                .replace(R.id.container, ArticleFragment.newInstance("top", sources="reuters,google-news"))
                .commitNow()
        }
    }
}