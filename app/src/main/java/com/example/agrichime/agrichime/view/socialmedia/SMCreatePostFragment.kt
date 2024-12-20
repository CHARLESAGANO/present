package com.example.agrichime.agrichime.view.socialmedia

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.agrichime.R
import com.example.agrichime.agrichime.viewmodel.UserDataViewModel
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.IOException
import java.util.UUID

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class SMCreatePostFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var authUser: FirebaseAuth? = null
    private var postID: UUID? = null
    private var bitmap: Bitmap? = null
    lateinit var socialMediaPostsFragment: SocialMediaPostsFragment
    lateinit var userDataViewModel : UserDataViewModel
    val db = FirebaseFirestore.getInstance()
    val data2 = HashMap<String, Any>()

    private lateinit var progress_create_post: ProgressBar // Declare ProgressBar here
    private lateinit var progressTitle: TextView // Declare TextView here
    private lateinit var descPostSM: EditText // Declare EditText here
    private lateinit var postTitleSM: EditText // Declare EditText here
    private lateinit var uploadImagePreview: ImageButton // Declare ImageButton here
    private lateinit var createPostBtnSM: Button // Declare Button here


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        storageReference = FirebaseStorage.getInstance().reference
        authUser = FirebaseAuth.getInstance()
        firebaseStore = FirebaseStorage.getInstance()

        userDataViewModel = ViewModelProvider(requireActivity())[UserDataViewModel::class.java]


    }


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_s_m_create_post, container, false)
        progress_create_post=view.findViewById(R.id.progress_create_post)
        progressTitle=view.findViewById(R.id.progressTitle)
        descPostSM=view.findViewById(R.id.descPostSM)
        postTitleSM=view.findViewById(R.id.postTitleSM)
        uploadImagePreview=view.findViewById(R.id.uploadImagePreview)
        createPostBtnSM=view.findViewById(R.id.createPostBtnSM)




        return view
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SMCreatePostFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.title = "Social Media"

        progress_create_post.visibility = View.GONE
        progressTitle.visibility = View.GONE

        data2["uploadType"] = ""
        uploadImagePreview.setOnClickListener {
            val intent = Intent()
            intent.type = "image/* video/*"
            intent.action = Intent.ACTION_PICK
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE_REQUEST
            )
        }

        val googleLoggedUser = authUser!!.currentUser!!.displayName
        if (googleLoggedUser.isNullOrEmpty()) {
            db.collection("users").document(authUser!!.currentUser!!.email!!)
                .get()
                .addOnCompleteListener {
                    val data = it.result
                    data2["name"] = data!!.getString("name").toString()
                    data!!.getString("name")?.let { it1 -> Log.d("Google User", it1) }
                }
        } else {
            data2["name"] = googleLoggedUser.toString()
            Log.d("Normal User", googleLoggedUser)
        }

        createPostBtnSM.setOnClickListener {

            if (postTitleSM.text.toString().isNullOrEmpty()) {
                Toast.makeText(
                    requireActivity().applicationContext,
                    "Please enter title",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                uploadImage().setImageBitmap(bitmap)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) {
                return
            }

            filePath = data.data
            uploadImagePreview.setImageURI(filePath)
            try {
                val lastIndex = filePath.toString().length - 1
                val type =
                    filePath.toString().slice((filePath.toString().lastIndexOf(".") + 1)..lastIndex)

                Log.d("File Type", filePath.toString())

                if (filePath.toString().contains("png") || filePath.toString().contains("jpg") || filePath.toString().contains("jpeg") || filePath.toString().contains("image") || filePath.toString().contains("images")){
                    data2["uploadType"] = "image"
                } else if(filePath.toString().contains("videos") || filePath.toString().contains("video") || filePath.toString().contains("mp4")){
                    data2["uploadType"] = "video"
                }

                Log.d("File Type 3", data2["uploadType"].toString())
                bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, filePath)

//                uploadImage().setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImage() {
        progress_create_post.visibility = View.VISIBLE
        progressTitle.visibility = View.VISIBLE
        if (filePath != null) {
            postID = UUID.randomUUID()
            val ref = storageReference?.child("posts/" + postID.toString())
            val uploadTask = ref?.putFile(filePath!!)

            val urlTask =
                uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                            progress_create_post.visibility = View.GONE
                            progressTitle.visibility = View.GONE
                        }
                    }
                    return@Continuation ref.downloadUrl
                })?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        addUploadRecordWithImageToDb(downloadUri.toString(), postID!!)
//                        progress_create_post.visibility = View.GONE
//                        progressTitle.visibility = View.GONE
                    } else {
                        // Handle failures
                        progress_create_post.visibility = View.GONE
                        progressTitle.visibility = View.GONE
                    }
                }?.addOnFailureListener {
                    progress_create_post.visibility = View.GONE
                    progressTitle.visibility = View.GONE
//                    Toast.makeText(requireActivity().applicationContext, it.message, Toast.LENGTH_LONG).show()
                }
        } else {
            data2["uploadType"] = ""
            addUploadRecordWithImageToDb(null, null)
            Log.d("File Type 2", "Null")
        }
    }

    private fun addUploadRecordWithTextToDb() {
        addUploadRecordWithImageToDb(null, null)
    }

    private fun addUploadRecordWithImageToDb(uri: String?, postID: UUID?) {

        if (!uri.isNullOrEmpty()) {
            data2["imageUrl"] = uri.toString()
            data2["imageID"] = postID.toString()

        }

        val data3 = HashMap<String, Any>()
        val postTimeStamp = System.currentTimeMillis()

        data2["userID"] = authUser!!.currentUser?.email.toString()
        data2["timeStamp"] = postTimeStamp
        data2["title"] = postTitleSM.text.toString()
        data2["description"] = descPostSM.text.toString()



        db.collection("posts")
            .add(data2)
            .addOnSuccessListener { documentReference ->

                val data = HashMap<String, Any>()
                val posts = arrayListOf<String>()
                val postRecordID = documentReference.id.toString()

                posts.add(postRecordID)
                data["posts"] = posts


                db.collection("users")
                    .document(authUser!!.currentUser?.email.toString())
                    .update("posts", FieldValue.arrayUnion(postRecordID))

                    .addOnSuccessListener { documentReference ->
                        Toast.makeText(
                            requireActivity().applicationContext,
                            "Post Created",
                            Toast.LENGTH_LONG
                        ).show()


//                        projects/agrichime-d81b3/databases/(default)/documents/users/bhobhriaaman.33@gmail.com

                        progress_create_post.visibility = View.GONE
                        progressTitle.visibility = View.GONE
                        userDataViewModel.getUserData(authUser!!.currentUser?.email.toString())
                        socialMediaPostsFragment = SocialMediaPostsFragment()
                        val transaction = requireActivity().supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.frame_layout, socialMediaPostsFragment, "smPostList")
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .setReorderingAllowed(true)
                            .addToBackStack("smPostList")
                            .commit()


                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            requireActivity().applicationContext,
                            "Posted",
                            Toast.LENGTH_LONG
                        ).show()

                        Log.e("db1",e.message.toString())
                        progress_create_post.visibility = View.GONE
                        progressTitle.visibility = View.GONE
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireActivity().applicationContext,
                    "Posted Successfully .. "+e.message.toString(),
                    Toast.LENGTH_LONG
                ).show()
                progress_create_post.visibility = View.GONE
                progressTitle.visibility = View.GONE
            }





    }
}

private fun Any.setImageBitmap(bitmap: Bitmap?) {

}