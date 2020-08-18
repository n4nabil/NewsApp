package com.vooom.newsapp.ui.details

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.vooom.newsapp.R
import com.vooom.newsapp.ui.article.ListItem
import kotlinx.android.synthetic.main.activity_details.*
import java.util.*


class DetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)


        val stringExtra = intent.getStringExtra("listItem")
        val listItem = Gson().fromJson(stringExtra, ListItem::class.java)
        val article = listItem.article!!


        Picasso.get().load(article.urlToImage).resizeDimen(R.dimen.thumbnail_size, R.dimen.thumbnail_size)
            .centerCrop()
            .into(thumbnail)

        tv_title.text = article.title
        tv_description.text = article.description
        tv_time_stamp.text = calculateTime(article.publishedAt)
        tv_content.text = " - ${article!!.publisher}"
        tv_body_content.text = " - ${article!!.content}"

        btn_read.setOnClickListener {
            val url = "${article.url}"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }
    }


    private fun calculateTime(publishedDate: Date): String {
        val milliseconds =
            Calendar.getInstance(TimeZone.getTimeZone("UTC")).time.time - publishedDate.time
        val minutes = milliseconds / (1000 * 60)
        if (minutes < 60) return "${minutes}m ago"
        val hours = minutes / 60
        if (hours < 24) return "${hours}h ago"
        return "${hours / 24}d ago"
    }
}