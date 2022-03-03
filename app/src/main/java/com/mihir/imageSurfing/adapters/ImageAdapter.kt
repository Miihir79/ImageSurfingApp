package com.mihir.imageSurfing.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mihir.imageSurfing.ui.ImageZoom
import com.mihir.imageSurfing.R
import com.mihir.imageSurfing.model.ImageModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.util.ArrayList

class ImageAdapter(private val context: Context, private val list: ArrayList<ImageModel>) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_recycler, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        CoroutineScope(Main).launch {
            Glide.with(context).load(list[position].urls.regular).into(holder.imageView)
            holder.imageView.setOnClickListener { v: View? ->
                val intent = Intent(context, ImageZoom::class.java)
                intent.putExtra("image", list[position].urls.regular)
                intent.putExtra("random", false)
                intent.putExtra("UserName",list[position].user.username)
                context.startActivity(intent)
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)

    }
}
