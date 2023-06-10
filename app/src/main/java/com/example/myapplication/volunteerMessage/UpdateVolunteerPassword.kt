package com.example.myapplication.volunteerMessage

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.myapplication.DB.MyDBHelper
import com.example.myapplication.databinding.ActivityUpdateVolunteerPasswordBinding

class UpdateVolunteerPassword : AppCompatActivity() {
    lateinit var binding: ActivityUpdateVolunteerPasswordBinding
    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityUpdateVolunteerPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.vUpdateMessageButton.setOnClickListener {
            val password=binding.volunteerNewPassword.text.toString()
            val oldpassword=binding.volunteerOldPassword.text.toString()
            val repassword=binding.volunteerNewRePassword.text.toString()
            val id=getSharedPreferences("UserInfo", Context.MODE_PRIVATE).getString("userId","")?.split("-")?.get(0)
            val db= MyDBHelper(this).writableDatabase
            val information=db.rawQuery("select `password` from `user` where `id`=? ", arrayOf(id))
            information.moveToFirst()
            val sqlpassword=information.getString(information.getColumnIndex("password"))
            if (oldpassword==sqlpassword&&password.length in 8..20&& password == repassword){
                val values= ContentValues().apply {
                    put("password",password)
                }
                val i=db.update("user",values,"`id`=?", arrayOf(id))
                if (i>0) finish()
            }else if (oldpassword!=sqlpassword) Toast.makeText(this,"密码错误", Toast.LENGTH_SHORT).show()
            else if(password.length !in 8..20) Toast.makeText(this,"新密码错误", Toast.LENGTH_SHORT).show()
            else if(password != repassword) Toast.makeText(this,"两次密码错误", Toast.LENGTH_SHORT).show()
        }
    }
}