package com.ozzystudio.sharephotoapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.ozzystudio.sharephotoapp.model.Post
import com.ozzystudio.sharephotoapp.R
import com.ozzystudio.sharephotoapp.adapter.FeedAdapter
import com.ozzystudio.sharephotoapp.databinding.ActivityFeedBinding
import java.text.SimpleDateFormat

class FeedActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseFirestore

    private var postList = ArrayList<Post>()

    private lateinit var feedAdapter: FeedAdapter
    private lateinit var binding : ActivityFeedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_feed)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar()
        setFirebase()
        var layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager

        feedAdapter = FeedAdapter(postList)
        binding.recyclerView.adapter = feedAdapter
    }

    private fun setToolbar(){
        setSupportActionBar(findViewById(R.id.main_toolbar))
    }
    private fun setFirebase(){
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        getDatasFromDatabase()
    }

    private fun getDatasFromDatabase(){
        database.collection("Post").orderBy("date", Query.Direction.DESCENDING)
                                                .addSnapshotListener { snapshot, error ->
            if(error != null){
                Toast.makeText(this,error.localizedMessage,Toast.LENGTH_LONG).show()
            }else{
                if(snapshot != null){
                    if(!snapshot.isEmpty){
                        val docs = snapshot.documents

                        postList.clear()
                        for(doc in docs){
                            val userEmail = doc.get("userEmail") as String
                            val userComment = doc.get("userComment") as String
                            val imageUrl = doc.get("imageUrl") as String
                            val timestamp = doc.get("date") as Timestamp

                            var date = timestamp.toDate()
                            val sdf = SimpleDateFormat("hh:mm \n d/MM/yyyy")
                            val dateString = sdf.format(date).toString()

                            val downloadedPost = Post(userEmail,userComment,imageUrl,dateString)
                            postList.add(downloadedPost)
                        }

                        feedAdapter.refreshData()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.option_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.share_photo){
            val intent = Intent(this, SharePhotoActivity::class.java)
            startActivity(intent)
            //finish() Comment, Back button active
        }else if(item.itemId == R.id.logout){
            auth.signOut() // firstly, sign out firebase
            val intent = Intent(this, UserActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}