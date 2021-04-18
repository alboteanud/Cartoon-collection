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

class RecyclerViewAdapter(private val parentActivity: ListActivity) :
        RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    private val onClickListener: View.OnClickListener
    var values: List<Item> = listOf()

    init {
        onClickListener = View.OnClickListener { v ->
            val item = v.tag as Item
            val intent = Intent(v.context, PlayerActivity::class.java).apply {
                putExtra(PlayerActivity.ARG_ITEM_VIDEO_ID, item.videoId)
            }
            v.context.startActivity(intent)

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
        holder.contentTextView.text = item.description
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
        val contentTextView: TextView = view.findViewById(R.id.content)
        val imageView: ImageView = view.findViewById(R.id.thumbImageView)
    }
}


/*
* // For a simple view:
@Override public void onCreate(Bundle savedInstanceState) {
  ...
  ImageView imageView = (ImageView) findViewById(R.id.my_image_view);

  Glide.with(this).load("http://goo.gl/gEgYUd").into(imageView);
}

// For a simple image list:
@Override public View getView(int position, View recycled, ViewGroup container) {
  final ImageView myImageView;
  if (recycled == null) {
    myImageView = (ImageView) inflater.inflate(R.layout.my_image_view, container, false);
  } else {
    myImageView = (ImageView) recycled;
  }

  String url = myUrls.get(position);

  Glide
    .with(myFragment)
    .load(url)
    .centerCrop()
    .placeholder(R.drawable.loading_spinner)
    .into(myImageView);

  return myImageView;
}
*
* */