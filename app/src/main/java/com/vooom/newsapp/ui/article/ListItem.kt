package com.vooom.newsapp.ui.article

import android.view.View
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import com.squareup.picasso.Picasso
import com.vooom.newsapp.R
import com.vooom.newsapp.network.Article
import kotlinx.android.synthetic.main.fragment_article.view.*
import java.io.Serializable
import java.util.*


data class ListItem
    (
    var article: Article? = null
) : Serializable, AbstractItem<ListItem.ViewHolder>() {

    /** defines the type defining this item. must be unique. preferably an id */
    override val type: Int
        get() = R.id.parent

    /** defines the layout which will be used for this item in the list  */
    override val layoutRes: Int
        get() = R.layout.fragment_article

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    class ViewHolder(view: View) : FastAdapter.ViewHolder<ListItem>(view) {

        override fun bindView(item: ListItem, payloads: MutableList<Any>) {

            val article = item.article!!


            itemView.title.text = article.title
            itemView.snippet.text = article.description
            itemView.time.text = calculateTime(article.publishedAt)
            itemView.source.text = " - ${article!!.publisher}"



            Picasso.get().load(article.urlToImage)
                .resizeDimen(R.dimen.thumbnail_size, R.dimen.thumbnail_size)
                .centerCrop()
                .into(itemView.image)


        }

        override fun unbindView(item: ListItem) {


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


}