package com.yoyoyo.ca.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.yoyoyo.ca.R
import com.yoyoyo.ca.databinding.ActivityContactsBinding
import com.yoyoyo.ca.databinding.ActivityRegisterBinding
import com.yoyoyo.ca.model.User
import java.lang.Exception
import java.util.*

class RegisterActivity : AppCompatActivity() {

    lateinit var binding: ActivityRegisterBinding
    var imageUri: Uri? = null

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

        if(name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()){
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(OnCompleteListener {
                    if(it.isSuccessful){
                        Log.i("Yoyoyo", "user created: " + it.result.user.uid)
                        saveUserFirebase()
                    }else{
                        Log.i("Yoyoyo", it.exception.toString())
                    }
                })
                .addOnFailureListener(OnFailureListener {
                    Log.i("Yoyoyo", it.message.toString())
                })
        }else{
            Toast.makeText(this, getString(R.string.msg_fill_all_fields), Toast.LENGTH_LONG).show()
        }
    }

    private fun saveUserFirebase() {
        var fileName = UUID.randomUUID().toString()
        var storageRef = FirebaseStorage.getInstance().getReference("/images/$fileName")
        storageRef.putFile(imageUri!!)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener {
                    Log.i("Yoyoyo", it.toString())

                    var uid = FirebaseAuth.getInstance().uid.toString()
                    var userName = binding.etName.text.toString()
                    var profileUrl = it.toString()

                    var user = User(uid, userName, profileUrl)

                    FirebaseFirestore.getInstance().collection("users")
                        .document(uid)
                        .set(user)
                        .addOnSuccessListener {
                            var intent = Intent(this, ChatActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                        .addOnFailureListener {
                            Log.i("Yoyoyo", it.message.toString())
                        }
                }
            }
            .addOnFailureListener {
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
