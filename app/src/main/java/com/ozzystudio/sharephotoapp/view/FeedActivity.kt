package com.ozzystudio.sharephotoapp.view

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.ozzystudio.sharephotoapp.model.Post
import com.ozzystudio.sharephotoapp.R
import com.ozzystudio.sharephotoapp.adapter.FeedAdapter

class FeedActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseFirestore

    private var postList = ArrayList<Post>()

    private lateinit var feedAdapter: FeedAdapter
    private lateinit var recyclerPost: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        setToolbar()
        setFirebase()
        recyclerPost = findViewById(R.id.recyclerView)
        var layoutManager = LinearLayoutManager(this)
        recyclerPost.layoutManager = layoutManager

        feedAdapter = FeedAdapter(postList)
        recyclerPost.adapter = feedAdapter
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

                            val downloadedPost = Post(userEmail,userComment,imageUrl)
                            postList.add(downloadedPost)
                        }

                        feedAdapter.refreshData()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //Menuyu aktivity ile bağlama
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.option_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.share_photo){
            val intent = Intent(this, SharePhotoActivity::class.java)
            startActivity(intent)
            //finish() bu yok çünkü geri tuşunun çalışmasını istiyorum.
        }else if(item.itemId == R.id.logout){
            auth.signOut() // firebaseden çık
            val intent = Intent(this, UserActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}