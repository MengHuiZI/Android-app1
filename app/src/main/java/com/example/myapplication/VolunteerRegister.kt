package com.example.myapplication

import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import com.example.myapplication.DB.MyDBHelper
import com.example.myapplication.databinding.ActivityVolunteerRegisterBinding
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


class VolunteerRegister : AppCompatActivity(),OnClickListener {
    lateinit var binding:ActivityVolunteerRegisterBinding
    var sex="男"
    var image:ByteArray= ByteArray(1000)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityVolunteerRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.registerBirthday.setOnClickListener(this)
        binding.registerButton.setOnClickListener(this)
        var sexSpinner= arrayOf("男","女")
        val sexSpinnerAdapter=ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,sexSpinner)
        binding.registerSex.adapter=sexSpinnerAdapter
        binding.registerSex.onItemSelectedListener=object:OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                sex=sexSpinner.get(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.register_birthday->{
                showTimePickerDialog()
            }
            R.id.register_button->{
                default()
                val account=binding.registerAccount.text.toString()
                val password=binding.registerPassword.text.toString()
                val rePassword=binding.registerRePassword.text.toString()
                val email=binding.registerEmail.text.toString()
                val name=binding.registerName.text.toString()
                val birthDate=binding.registerBirthday.text.toString()
                val photon=binding.registerPhoton.text.toString()
                val emailRegex=Regex("[0-9a-zA-Z]{2,15}@[0-9a-zA-z]{1,10}[.](com|cn|net)")
                if (account.length in 7..17&&password.length in 8..20&& password == rePassword&&email.matches(emailRegex)&&photon.length in 8..15&&name!=null&&birthDate!=null){
                    val myDBHelper=MyDBHelper(this)
                    val db=myDBHelper.writableDatabase
                    val groupSql="select `g_account` from `group` where `g_account`=?"
                    val volunteerSql="select `account` from `user` where `account`=?"
                    val groupInformation=db.rawQuery(groupSql, arrayOf(account))
                    val volunteerInformation=db.rawQuery(volunteerSql, arrayOf(account))
                    if (!groupInformation.moveToFirst()&&!volunteerInformation.moveToFirst()){
                        val values=ContentValues().apply {
                            put("account",account)
                            put("password",password)
                            put("email",email)
                            put("name",name)
                            put("sex",sex)
                            put("birth_date",birthDate)
                            put("phone_number",photon)
                            put("head_portrait",image)
                        }
                        val i=db.insert("user",null,values).toInt()
                        if (i==-1){
                            Toast.makeText(this,"注册失败",Toast.LENGTH_SHORT).show()
                        }else{
                            startActivity(Intent(this,MainActivity::class.java))
                            finish()
                        }
                    }else Toast.makeText(this,"已有该账号", Toast.LENGTH_SHORT).show()
                }else{
                    var msg=""
                    if (account.length !in 7..17){
                        msg="账号长度应在7到17之间"
                    }else if(password != rePassword){
                        msg="两次密码不一致"
                    }else if(!email.matches(emailRegex)){
                        msg="邮箱格式不正确"
                    }else if(photon.length !in 8..15){
                        msg="手机号格式不正确"
                    }
                    Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //日期选择器
    fun showTimePickerDialog(){
        //获取当前时间
        val calendar=Calendar.getInstance()
        val year=calendar.get(Calendar.YEAR)
        val month=calendar.get(Calendar.MONTH)
        val day=calendar.get(Calendar.DAY_OF_MONTH)
        //创建时间选择对话框
        val datePickerDialog= DatePickerDialog(this,{ _:DatePicker, year:Int, month:Int, day:Int->
            calendar.set(Calendar.YEAR,year)
            calendar.set(Calendar.MONTH,month)
            calendar.set(Calendar.DAY_OF_MONTH,day)

            val simpleDateFormat=SimpleDateFormat("yyyy-MM-dd")
            val date=simpleDateFormat.format(calendar.time)
            binding.registerBirthday.setText(date)
        },year,month,day)

        datePickerDialog.show()
    }

    //默认头像
    fun default(){
        var bitmap=BitmapFactory.decodeResource(resources,R.drawable.touxiang128)
        val os: ByteArrayOutputStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG,100,os)
        image=os.toByteArray()
    }
}