package com.ozzystudio.sharephotoapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ozzystudio.sharephotoapp.R
import com.ozzystudio.sharephotoapp.model.Post
import com.squareup.picasso.Picasso

class FeedAdapter(private val postList: ArrayList<Post>): RecyclerView.Adapter<FeedAdapter.PostHolder>() {

    inner class PostHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val txtEmail: TextView = itemView.findViewById(R.id.txt_email)
        val txtComment: TextView = itemView.findViewById(R.id.txt_comment)
        val imgPost: ImageView = itemView.findViewById(R.id.img_post)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.post_item,parent,false)
        return PostHolder(view)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.txtEmail.text = postList[position].userEmail
        holder.txtComment.text = postList[position].userComment
        Picasso.get().load(postList[position].imageUrl).into(holder.imgPost)
    }

    fun refreshData(){
        notifyDataSetChanged()
    }
}