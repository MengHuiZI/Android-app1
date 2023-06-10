package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.myapplication.DB.MyDBHelper
import com.example.myapplication.databinding.ActivityLoginBinding

class Login : AppCompatActivity() {
    lateinit var binding:ActivityLoginBinding
    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //登陆
        binding.loginButton.setOnClickListener {
            val account=binding.loginUser.text.toString()
            val password=binding.loginPassword.text.toString()
            if (account!=""&&password!=""){
                val db=MyDBHelper(this).readableDatabase
                val sql="select `id`,`account`,`password` from `user` where `account`=?"
                val userInformation=db.rawQuery(sql, arrayOf(account))
                if (userInformation.moveToFirst()){
                    if (userInformation.getString(userInformation.getColumnIndex("password"))==password){
                        val sp=getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
                        val edit=sp.edit()
                        edit.putString("userId",userInformation.getString(userInformation.getColumnIndex("id"))+"-volunteer")
                        edit.commit()
                        val code=intent.getStringExtra("code")
                        if (code=="return"){
                            finish()
                        }else{
                            val intent= Intent(this,MainActivity::class.java)
                            intent.putExtra("fragment",R.id.navigation_myMessage)
                            startActivity(intent)
                            finish()
                        }
                    }else Toast.makeText(this,"密码错误",Toast.LENGTH_SHORT).show()
                }else {
                    val sql2="select `id`,`g_account`,`g_password` from `group` where `g_account`=?"
                    val userInformation=db.rawQuery(sql2, arrayOf(account))
                    if (userInformation.moveToFirst()){
                        if (userInformation.getString(userInformation.getColumnIndex("g_password"))==password){
                            val sp=getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
                            val edit=sp.edit()
                            edit.putString("userId",userInformation.getString(userInformation.getColumnIndex("id"))+"-group")
                            edit.commit()
                            val code=intent.getStringExtra("code")
                            if (code=="return"){
                                finish()
                            }else{
                                val intent= Intent(this,MainActivity::class.java)
                                intent.putExtra("fragment",R.id.navigation_myMessage)
                                startActivity(intent)
                                finish()
                            }
                        }else Toast.makeText(this,"密码错误",Toast.LENGTH_SHORT).show()
                    }else Toast.makeText(this,"没有该账号",Toast.LENGTH_SHORT).show()
                }
            }else Toast.makeText(this,"请输入账号或密码",Toast.LENGTH_SHORT).show()
        }
    }
}