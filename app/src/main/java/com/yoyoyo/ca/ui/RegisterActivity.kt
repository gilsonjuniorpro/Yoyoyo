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
import com.yoyoyo.ca.R
import com.yoyoyo.ca.databinding.ActivityContactsBinding
import com.yoyoyo.ca.databinding.ActivityRegisterBinding
import java.lang.Exception

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
                    }else{
                        Log.i("Yoyoyo", it.result.user.uid)
                    }
                })
                .addOnFailureListener(OnFailureListener {
                    Log.i("Yoyoyo", it.message.toString())
                })
        }else{
            Toast.makeText(this, getString(R.string.msg_fill_all_fields), Toast.LENGTH_LONG).show()
        }
    }

    private fun uploadImage() {
        var it = Intent(Intent.ACTION_PICK)
        it.type = "image/*"
        startActivityForResult(it, 0)
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
