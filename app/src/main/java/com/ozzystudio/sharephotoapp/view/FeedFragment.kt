package com.ozzystudio.sharephotoapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ozzystudio.sharephotoapp.R
import com.ozzystudio.sharephotoapp.adapter.FeedAdapter
import com.ozzystudio.sharephotoapp.databinding.FragmentFeedBinding
import com.ozzystudio.sharephotoapp.model.Post
import java.text.SimpleDateFormat

class FeedFragment : Fragment(), PopupMenu.OnMenuItemClickListener {

    private lateinit var binding: FragmentFeedBinding
    private lateinit var popup: PopupMenu

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    private var postList = ArrayList<Post>()
    private lateinit var feedAdapter: FeedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        database = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        //return inflater.inflate(R.layout.fragment_feed, container, false)
        binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        popup = PopupMenu(requireContext(), binding.btnFloating)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.menu_popup, popup.menu)

        popup.setOnMenuItemClickListener(this)

        binding.btnFloating.setOnClickListener { floatBtn() }

        getDatasFromDatabase()

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        feedAdapter = FeedAdapter(postList)
        binding.recyclerView.adapter = feedAdapter
    }
    private fun floatBtn() {
        popup.show()
    }
    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.item_share) {
            findNavController().navigate(R.id.action_feedFragment_to_sharePhotoFragment)
        } else if (item?.itemId == R.id.item_logout) {
            auth.signOut()
            findNavController().navigate(R.id.action_feedFragment_to_userFragment)
        }
        return true
    }
    private fun getDatasFromDatabase() {
        database.collection("Post").orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(requireContext(), error.localizedMessage, Toast.LENGTH_LONG).show()
                } else {
                    if (snapshot != null) {
                        if (!snapshot.isEmpty) {
                            val docs = snapshot.documents

                            postList.clear()
                            for (doc in docs) {
                                val userEmail = doc.get("userEmail") as String
                                val userComment = doc.get("userComment") as String
                                val imageUrl = doc.get("imageUrl") as String
                                val timestamp = doc.get("date") as Timestamp

                                var date = timestamp.toDate()
                                val sdf = SimpleDateFormat("hh:mm \n d/MM/yyyy")
                                val dateString = sdf.format(date).toString()

                                val downloadedPost = Post(userEmail, userComment, imageUrl, dateString)
                                postList.add(downloadedPost)
                            }

                            feedAdapter.refreshData()
                        }
                    }
                }
            }
    }
}