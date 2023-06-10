package com.example.myapplication

import android.content.ContentValues
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import com.example.myapplication.DB.MyDBHelper
import com.example.myapplication.Thread.CountDown
import com.example.myapplication.Thread.SendEmail
import com.example.myapplication.databinding.ActivityGroupRegisterBinding
import org.jetbrains.annotations.TestOnly
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.random.Random

class GroupRegister : AppCompatActivity(),OnClickListener {
    lateinit var binding : ActivityGroupRegisterBinding
    lateinit var countDown:CountDown
    lateinit var handler:Handler
    var authCode="kls;dzhgvoiSAHEf4d5s4gs3d4fg6s18sfresfs"
    var image:ByteArray= ByteArray(1000)
    val emailRegex=Regex("[0-9a-zA-Z]{2,15}@[0-9a-zA-z]{1,10}[.](com|cn|net)")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityGroupRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.sendAuthCode.setOnClickListener(this)
        binding.GRegisterButton.setOnClickListener(this)
        handler=object :Handler(){
            override fun handleMessage(msg: Message) {
                when(msg.what){
                    1->{
                        if (msg.obj.toString().toInt()==0){
                            binding.sendAuthCode.setText("发送验证码")
                            binding.sendAuthCode.isEnabled=true
                            countDown.stopTime()
                        }else{
                            binding.sendAuthCode.setText(msg.obj.toString())
                            binding.sendAuthCode.isEnabled=false
                        }
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            //发送邮箱验证码
            R.id.send_authCode->{
                if (binding.GRegisterEmail.text.toString().matches(emailRegex)){
                    authCode=generateVerificationCode()
                    val sendEmail=SendEmail("梦回志愿团体注册验证码", "验证码：$authCode",binding.GRegisterEmail.text.toString())
                    sendEmail.start()
                    countDown=CountDown(handler,1)
                    countDown.start()
                }else{
                    Toast.makeText(this,"邮箱格式错误",Toast.LENGTH_SHORT).show()
                }

            }
            R.id.GRegister_button->{
                val account=binding.GRegisterAccount.text.toString()
                val password=binding.GRegisterPassword.text.toString()
                val rePassword=binding.GRegisterRePassword.text.toString()
                val email=binding.GRegisterEmail.text.toString()
                val writeAuthCode=binding.GRegisterEmailAuthCode.text.toString()
                val groupName=binding.GRegisterName.text.toString()
                val registerDate=getDate()
                val address=binding.GRegisterAddress.text.toString()
                val principalName=binding.GRegisterPrincipalName.text.toString()
                val principalPhoton=binding.GRegisterPrincipalPhoton.text.toString()

                if (account.length in 7..17&&password.length in 8..20&& password == rePassword&&email.matches(emailRegex)&&principalPhoton.length in 8..15&&groupName!=null&&principalName!=null&& authCode == writeAuthCode){
                    val myDBHelper= MyDBHelper(this)
                    val db=myDBHelper.writableDatabase
                    val groupSql="select `g_account` from `group` where `g_account`=?"
                    val volunteerSql="select `account` from `user` where `account`=?"
                    val groupInformation=db.rawQuery(groupSql, arrayOf(account))
                    val volunteerInformation=db.rawQuery(volunteerSql, arrayOf(account))
                    if (!groupInformation.moveToFirst()&&!volunteerInformation.moveToFirst()){
                        val values= ContentValues().apply {
                            put("g_account",account)
                            put("g_password",password)
                            put("g_email",email)
                            put("g_name",groupName)
                            put("g_address",address)
                            put("principal_name",principalName)
                            put("principal_phone",principalPhoton)
                            put("register_date",registerDate)
                            put("image",image)
                        }
                        val i=db.insert("`group`",null,values).toInt()
                        if (i==-1){
                            Toast.makeText(this,"注册失败", Toast.LENGTH_SHORT).show()
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
                    }else if(principalPhoton.length !in 8..15){
                        msg="手机号格式不正确"
                    }else if(authCode!=writeAuthCode){
                        msg="验证码错误"
                    }else if (address==null){
                        msg="地址为空"
                    }else if (principalName==null){
                        msg="负责人姓名为空"
                    }else if (groupName==null){
                        msg="团体名字为空"
                    }
                    Toast.makeText(this,msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    //获取注册日期
    fun getDate(): String {
        val calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        return simpleDateFormat.format(calendar.time)
    }

    //验证码生成器
    fun generateVerificationCode():String{
        val codeLength=6
        val code=StringBuffer()
        repeat(codeLength){
            val digit= Random.nextInt(10)
            code.append(digit)
        }
        return code.toString()
    }

    //默认图片
    fun default(){
        var bitmap=BitmapFactory.decodeResource(resources,R.drawable.ic_home_black_24dp)
        var os=ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG,100,os)
        image=os.toByteArray()
    }
}