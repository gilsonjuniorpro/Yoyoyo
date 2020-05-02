package com.yoyoyo.ca.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.jarvis.ca.Mark
import com.yoyoyo.ca.R
import com.yoyoyo.ca.databinding.ActivityContactsBinding
import com.yoyoyo.ca.databinding.ActivityRegisterBinding
import com.yoyoyo.ca.model.User
import com.yoyoyo.ca.repository.ChatRepository
import com.yoyoyo.ca.viewmodel.ChatViewModelFactory
import com.yoyoyo.ca.viewmodel.UserViewModel
import java.lang.Exception
import java.util.*

class RegisterActivity : AppCompatActivity() {

    lateinit var binding: ActivityRegisterBinding
    var imageUri: Uri? = null

    private val viewModel: UserViewModel by lazy {
        ViewModelProvider(
            this,
            ChatViewModelFactory(
                ChatRepository(this)
            )
        ).get(UserViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)

        binding.btImage.setOnClickListener{ uploadImage() }
        binding.btRegister.setOnClickListener{ registerUser() }
    }

    private fun registerUser() {
        val name = binding.etName.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        binding.progressBar.visibility = View.VISIBLE
        if(name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()){
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(OnCompleteListener {
                    binding.progressBar.visibility = View.GONE
                    if(it.isSuccessful){
                        saveUserFirebase()
                    }else{
                        Log.i("Yoyoyo", it.exception.toString())
                    }
                })
                .addOnFailureListener(OnFailureListener {
                    binding.progressBar.visibility = View.GONE
                    Log.i("Yoyoyo", it.message.toString())
                })
        }else{
            binding.progressBar.visibility = View.GONE
            Mark.showAlertError(this, getString(R.string.msg_fill_all_fields))
        }
    }

    private fun saveUserFirebase() {
        var fileName = UUID.randomUUID().toString()
        var storageRef = FirebaseStorage.getInstance().getReference("/images/$fileName")

        binding.progressBar.visibility = View.VISIBLE
        storageRef.putFile(imageUri!!)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener {
                    var uid = FirebaseAuth.getInstance().uid.toString()
                    var userName = binding.etName.text.toString()
                    var profileUrl = it.toString()

                    var user = User(uid, userName, profileUrl, null, false)

                    FirebaseFirestore.getInstance().collection("users")
                        .document(uid)
                        .set(user)
                        .addOnSuccessListener {
                            viewModel.onCreate(user)
                            binding.progressBar.visibility = View.GONE
                            var intent = Intent(this, MessagesActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                        .addOnFailureListener {
                            binding.progressBar.visibility = View.GONE
                            Log.i("Yoyoyo", it.message.toString())
                        }
                }
            }
            .addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                Log.i("Yoyoyo", it.message.toString())
            }
    }

    private fun uploadImage() {
        var intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0){
            imageUri = data?.data //ivProfile

            var bitmap: Bitmap?
            try{
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                binding.ivProfile.setImageDrawable(BitmapDrawable(bitmap))
                binding.btImage.alpha = 0f
            }catch (e: Exception){
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }
}
