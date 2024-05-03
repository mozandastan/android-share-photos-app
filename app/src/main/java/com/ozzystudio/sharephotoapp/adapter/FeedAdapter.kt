package com.ozzystudio.sharephotoapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ozzystudio.sharephotoapp.databinding.PostItemBinding
import com.ozzystudio.sharephotoapp.model.Post
import com.squareup.picasso.Picasso

class FeedAdapter(private val postList: ArrayList<Post>): RecyclerView.Adapter<FeedAdapter.PostHolder>() {

    /*
    inner class PostHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val txtEmail: TextView = itemView.findViewById(R.id.txt_email)
        val txtComment: TextView = itemView.findViewById(R.id.txt_comment)
        val txtDate: TextView = itemView.findViewById(R.id.txt_date)
        val imgPost: ImageView = itemView.findViewById(R.id.img_post)
    }
     */
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
        /*
        holder.txtEmail.text = postList[position].userEmail
        holder.txtComment.text = postList[position].userComment
        holder.txtDate.text = postList[position].date
        Picasso.get().load(postList[position].imageUrl).into(holder.imgPost)
        */
        holder.bind(postList[position])
    }

    fun refreshData(){
        notifyDataSetChanged()
    }
}