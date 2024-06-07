package com.ozzystudio.sharephotoapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ozzystudio.sharephotoapp.databinding.PostItemBinding
import com.ozzystudio.sharephotoapp.model.Post
import com.squareup.picasso.Picasso

class FeedAdapter(private val postList: ArrayList<Post>): RecyclerView.Adapter<FeedAdapter.PostHolder>() {

    inner class PostHolder(private val binding: PostItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(post: Post){
            binding.txtEmail.text = post.userEmail
            binding.txtComment.text = post.userComment
            binding.txtDate.text = post.date
            Picasso.get().load(post.imageUrl).into(binding.imgPost)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val inflater = LayoutInflater.from(parent.context)
        //val view = inflater.inflate(R.layout.post_item,parent,false)
        //return PostHolder(view)
        val binding = PostItemBinding.inflate(inflater, parent, false)
        return PostHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.bind(postList[position])
    }

    fun refreshData(){
        notifyDataSetChanged()
    }
}