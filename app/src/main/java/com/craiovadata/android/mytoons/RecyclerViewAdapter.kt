package com.craiovadata.android.mytoons

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.craiovadata.android.mytoons.model.Item
import timber.log.Timber

class RecyclerViewAdapter(private val parentActivity: ListActivity) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    private val onClickListener: View.OnClickListener
    var values: List<Item> = listOf()

    init {
        onClickListener = View.OnClickListener { v ->
            val item = v.tag as Item
            val intent = Intent(v.context, PlayerActivity::class.java).apply {
                putExtra(PlayerActivity.ARG_ITEM_ID, item.videoId)
            }
            v.context.startActivity(intent)
            Timber.d("clicked on: %s %s", item.thumbUrl, item.videoId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_content, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.titleTextView.text = item.title
        Glide.with(parentActivity)
            .load(item.thumbUrl)
            .into(holder.imageView)

        with(holder.imageView) {
            tag = item
            setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount() = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.titleText)
        val imageView: ImageView = view.findViewById(R.id.thumbImageView)
    }
}
