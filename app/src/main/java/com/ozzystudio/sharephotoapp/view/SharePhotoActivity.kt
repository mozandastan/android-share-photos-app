package com.ozzystudio.sharephotoapp.view

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.ozzystudio.sharephotoapp.databinding.ActivitySharePhotoBinding
import java.util.UUID

class SharePhotoActivity : AppCompatActivity() {

    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    private var choosenImage: Uri? = null
    //private var choosenBitmap: Bitmap? = null

    private lateinit var binding : ActivitySharePhotoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_share_photo)
        binding = ActivitySharePhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        binding.textView.visibility= View.VISIBLE
    }

    fun sharePhoto(view: View) {
        //storage
        val reference = storage.reference
        //Image names must be unique in firestore
        //UUID (universal unique id).
        val uuid = UUID.randomUUID() // create random uuid
        val imageName = "${uuid}.jpg"
        val imageReference = reference.child("images").child(imageName)
        if (choosenImage != null) {
            // firebase storage image send
            imageReference.putFile(choosenImage!!).addOnSuccessListener { taskSnapShot ->
                println("Image uploaded")
                //Get the URL of the link where the image is found on the storage to save it in the database.
                val uploadedImageReference = FirebaseStorage.getInstance().reference.child("images").child(imageName)
                uploadedImageReference.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()
                    println(downloadUrl)
                    //save url to database

                    //prepare data write
                    val currentUserEmail = auth.currentUser!!.email.toString()
                    val userComment = binding.editTextText.text.toString()
                    val date = Timestamp.now() // get firebase time (timestamp)

                    val postHasyMap = hashMapOf<String, Any>()
                    postHasyMap.put("imageUrl", downloadUrl)
                    postHasyMap.put("userEmail", currentUserEmail)
                    postHasyMap.put("userComment", userComment)
                    postHasyMap.put("date", date)

                    //data write
                    database.collection("Post").add(postHasyMap).addOnCompleteListener {
                        if (it.isSuccessful) {
                            finish() // Since it come with intent, it returns automatically when you finish.
                        }
                    }.addOnFailureListener {
                        Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_LONG)
                            .show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }
    fun choosePhoto(view: View) {
        // Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                galleryLauncher.launch("image/*")
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 1)
            }
        }
        // Android 12-
        else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                galleryLauncher.launch("image/*")
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Access to the gallery if permission is granted
                galleryLauncher.launch("image/*")
            } else {
                // Show Message if permission is denied
                Toast.makeText(this, "Galeriye erişim izni reddedildi", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        val galleryUri = it
        try{
            choosenImage = galleryUri
            binding.imgChooseimg.setImageURI(galleryUri)
            binding.textView.visibility= View.GONE
        }catch(e:Exception){
            e.printStackTrace()
        }
    }
}

//DEPRECATED CODE
    /*
    fun choosePhoto(view:View){
        //contextcompat api seviyesine göre versiyonlarda bu iznin gerekli olup olmaması değişirse, versiyona göre aksiyon sağlar.
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                //eğer izin alınmamışsa iste
                println("1")
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
            }else{
                println("2")
                //eğer izin varsa galeriye git
                val galleryIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent,2)
                //pickImageLauncher.launch("image/*")
            }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 1){
            if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //eğer izin verildiyse galeriye git
                val galleryIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent,2)
                //pickImageLauncher.launch("image/*")
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        println("5")
        //galeri açıldıysa ve result ok ise, işlem yapıldıysa, geriye veri geldiyse
        if(requestCode == 2 && resultCode == RESULT_OK && data != null){
            choosenImage = data.data
            if(choosenImage != null){
                println("6")
                if(Build.VERSION.SDK_INT >= 28){
                    println("7")
                    val source = ImageDecoder.createSource(this.contentResolver,choosenImage!!)
                    choosenBitmap = ImageDecoder.decodeBitmap(source)
                    imgShare.setImageBitmap(choosenBitmap)
                }else{
                    choosenBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,choosenImage)
                    imgShare.setImageBitmap(choosenBitmap)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
*/