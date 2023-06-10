package com.example.myapplication.volunteerMessage

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.myapplication.DB.MyDBHelper
import com.example.myapplication.databinding.ActivityUpdateVolunteerMessageBinding

class UpdateVolunteerMessage : AppCompatActivity() {
    lateinit var binding:ActivityUpdateVolunteerMessageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityUpdateVolunteerMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        binding.vUpdateMessageButton.setOnClickListener {
            val emailRegex=Regex("[0-9a-zA-Z]{2,15}@[0-9a-zA-z]{1,10}[.](com|cn|net)")
            val email=binding.showVolunteerEmail.text.toString()
            val phone=binding.showVolunteerPhone.text.toString()
            val id=getSharedPreferences("UserInfo", Context.MODE_PRIVATE).getString("userId","")?.split("-")?.get(0)

            if (email.matches(emailRegex)&&phone.length in 8..15){
                val db= MyDBHelper(this).writableDatabase
                val values= ContentValues().apply {
                    put("email",email)
                    put("phone_number",phone)
                }
                val i=db.update("user",values,"`id`=?", arrayOf(id))
                if (i>0){
                    finish()
                }else if (i==0) Toast.makeText(this,"请修改后提交", Toast.LENGTH_SHORT).show()
                else if(i==-1) Toast.makeText(this,"修改失败", Toast.LENGTH_SHORT).show()
            }else if (!email.matches(emailRegex)) Toast.makeText(this,"邮箱格式错误", Toast.LENGTH_SHORT).show()
            else if (phone.length !in 8..15) Toast.makeText(this,"手机号错误",Toast.LENGTH_SHORT).show()
        }

    }

    @SuppressLint("Range")
    fun init(){
        val id=getSharedPreferences("UserInfo", Context.MODE_PRIVATE).getString("userId","")?.split("-")?.get(0)
        val sql="select * from `user` where `id`=? "
        val db= MyDBHelper(this).writableDatabase
        val oldInformation=db.rawQuery(sql, arrayOf(id))
        oldInformation.moveToFirst()
        binding.showVolunteerAccount.text = oldInformation.getString(oldInformation.getColumnIndex("account"))
        binding.showVolunteerEmail.setText(oldInformation.getString(oldInformation.getColumnIndex("email")))
        binding.showVolunteerName.text  = oldInformation.getString(oldInformation.getColumnIndex("name"))
        binding.showVolunteerSex.text =oldInformation.getString(oldInformation.getColumnIndex("sex"))
        binding.showVolunteerBirthday.text =oldInformation.getString(oldInformation.getColumnIndex("birth_date"))
        binding.showVolunteerPhone.setText(oldInformation.getString(oldInformation.getColumnIndex("phone_number")))
    }
}