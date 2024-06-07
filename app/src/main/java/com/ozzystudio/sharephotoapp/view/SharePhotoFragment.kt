package com.ozzystudio.sharephotoapp.view

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.ozzystudio.sharephotoapp.R
import com.ozzystudio.sharephotoapp.databinding.FragmentSharePhotoBinding
import java.util.UUID

class SharePhotoFragment : Fragment() {

    private lateinit var binding: FragmentSharePhotoBinding

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var chosenImage: Uri? = null
    private var chosenBitmap: Bitmap? = null

    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storage = Firebase.storage
        auth = Firebase.auth
        database = Firebase.firestore

        registerLaunchers()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //return inflater.inflate(R.layout.fragment_share_photo, container, false)
        binding = FragmentSharePhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardView.setOnClickListener{ choosePhoto(it)}
        binding.btnShare.setOnClickListener{ sharePhoto()}

    }
    private fun choosePhoto(view: View) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                //no permission
                if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_MEDIA_IMAGES)){
                    //ask permission
                    Snackbar.make(view,"Need permission to go to the gallery", Snackbar.LENGTH_INDEFINITE).setAction(
                        "Allow",View.OnClickListener {
                            //ask permission
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                        }).show()
                }else{
                    //ask permission
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            } else {
                //permission granted
                goToGallery()
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //no permission
                if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
                    //ask permission
                    Snackbar.make(view,"Need permission to go to the gallery", Snackbar.LENGTH_INDEFINITE).setAction(
                        "Allow",View.OnClickListener {
                            //ask permission
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }).show()
                }else{
                    //ask permission
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else {
                //permission granted
                goToGallery()
            }
        }
    }
    private fun sharePhoto() {
        //storage
        val reference = storage.reference
        //Image names must be unique in firestore
        //UUID (universal unique id).
        val uuid = UUID.randomUUID() // create random uuid
        val imageName = "${uuid}.jpg"
        val imageReference = reference.child("images").child(imageName)
        if (chosenImage != null) {
            // firebase storage image send
            imageReference.putFile(chosenImage!!).addOnSuccessListener { taskSnapShot ->
                println("Image uploaded")
                //Get the URL of the link where the image is found on the storage to save it in the database.
                imageReference.downloadUrl.addOnSuccessListener {
                    if(auth.currentUser != null){
                        val downloadUrl = it.toString()
                        println(downloadUrl)
                        //then save url to database

                        //prepare data write
                        val currentUserEmail = auth.currentUser!!.email.toString()
                        val userComment = binding.commentText.text.toString()
                        val date = Timestamp.now() // get firebase time (timestamp)

                        val postHashMap = hashMapOf<String, Any>()
                        postHashMap["imageUrl"] = downloadUrl
                        postHashMap["userEmail"] = currentUserEmail
                        postHashMap["userComment"] = userComment
                        postHashMap["date"] = date

                        //data write
                        database.collection("Post").add(postHashMap).addOnSuccessListener {
                            findNavController().navigate(R.id.action_sharePhotoFragment_to_feedFragment)
                        }.addOnFailureListener { exception ->
                            Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerLaunchers() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == RESULT_OK){
                val intentFromResult = result.data
                if(intentFromResult != null){
                    chosenImage = intentFromResult.data

                    try {
                        if(Build.VERSION.SDK_INT >= 28){
                            val source = ImageDecoder.createSource(requireActivity().contentResolver,chosenImage!!)
                            chosenBitmap = ImageDecoder.decodeBitmap(source)
                            binding.imgChooseimg.setImageBitmap(chosenBitmap)
                            binding.commentText.visibility= View.GONE
                        }else{
                            chosenBitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver,chosenImage)
                            binding.imgChooseimg.setImageBitmap(chosenBitmap)
                            binding.commentText.visibility= View.GONE
                        }
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                }
            }
        }
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ result ->
            if(result){
                goToGallery()
            }else{
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun goToGallery(){
        val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activityResultLauncher.launch(intentToGallery)
    }
}