package com.example.myapplication

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.DB.MyDBHelper
import com.example.myapplication.databinding.ActivityProjectInfoBinding
import java.text.SimpleDateFormat
import java.util.*

class ProjectInfo : AppCompatActivity() {
    lateinit var binding:ActivityProjectInfoBinding
    lateinit var photon:String
    lateinit var email:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityProjectInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getInformation()

        //弹出底部dialog
        binding.contactNumber.setOnClickListener {
            val dialog= Dialog(this)
            dialog.setContentView(R.layout.groupinfo_dialog)
            dialog.setCancelable(false)
            //控件赋值
            dialog.findViewById<TextView>(R.id.groupInfo_dialog_photon).text=photon
            dialog.findViewById<TextView>(R.id.groupInfo_dialog_email).text=email
            //从底部弹出
            dialog.window?.setGravity(Gravity.BOTTOM)
            //宽高设置
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            //弹出动画
            dialog.window?.setWindowAnimations(com.google.android.material.R.style.Animation_MaterialComponents_BottomSheetDialog)

            //设置dialog监听
            dialog.findViewById<TextView>(R.id.groupInfo_dialog_photon).setOnClickListener {
                val dialogPhotonNumber=dialog.findViewById<TextView>(R.id.groupInfo_dialog_photon).text.toString()
                val clipboardManager=getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboardManager.setPrimaryClip(ClipData.newPlainText("principalPhoton",dialogPhotonNumber))
                Toast.makeText(this,"复制成功", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            dialog.findViewById<TextView>(R.id.groupInfo_dialog_email).setOnClickListener {
                val emailNumber=dialog.findViewById<TextView>(R.id.groupInfo_dialog_email).text.toString()
                val clipboardManager=getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboardManager.setPrimaryClip(ClipData.newPlainText("principalEmail",emailNumber))
                Toast.makeText(this,"复制成功", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            dialog.findViewById<TextView>(R.id.groupInfo_dialog_exist).setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()

        }

        //底部导航栏监听
        val bottomNavigationView=binding.projectBottomNavigation
        bottomNavigationView.setOnItemSelectedListener { menuItem->
            when(menuItem.itemId){
                R.id.times->{
                    Toast.makeText(this,"时长", Toast.LENGTH_SHORT).show()
                    true
                }
                //加入项目的一些操作
                R.id.join_project->{
                    val sp=getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
                    val userId=sp.getString("userId","")
                    if (userId!=""){
                        if (userId?.split("-")?.get(1)=="volunteer"){
                            val myDBHelper=MyDBHelper(this)
                            val db=myDBHelper.writableDatabase
                            val sql="select v_id from volunteer_project where v_id=?and `p_id`=?"
                            if (!db.rawQuery(sql, arrayOf(userId.split("-")[0],intent.getStringExtra("projectId"))).moveToFirst()){
                                val values= ContentValues().apply {
                                    put("v_id", userId.split("-")[0])
                                    put("p_id",intent.getStringExtra("projectId"))
                                    put("v_p_apply_date",getDate())
                                    put("state","待审核")
                                }
                                val i=db.insert("volunteer_project",null,values).toInt()
                                if (i!=-1){
                                    Toast.makeText(this,"申请成功，请等待审核", Toast.LENGTH_SHORT).show()
                                }else{
                                    Toast.makeText(this,"申请失败", Toast.LENGTH_SHORT).show()
                                }
                            }else{
                                Toast.makeText(this,"已申请，请不要重复申请", Toast.LENGTH_SHORT).show()
                            }
                        }else Toast.makeText(this,"团体账号不能加入项目", Toast.LENGTH_SHORT).show()
                    }else{
                        val alertDialog= AlertDialog.Builder(this)
                        alertDialog.setCancelable(false)
                        alertDialog.setMessage("登陆后加入团体")
                        alertDialog.setPositiveButton("确定") { _, _ ->
                            val intent= Intent(this, Login::class.java)
                            intent.putExtra("code","return")
                            startActivity(intent)
                        }
                        alertDialog.setNegativeButton("取消"){_,_-> }
                        alertDialog.show()
                    }
                    true
                }
                else->false
            }
        }
    }

    //初始数据
    @SuppressLint("Range")
    fun getInformation(){
        val sql="select * from `project` where `id`=?"
        val sql2="select `principal_name`,`g_name`,`principal_phone`,`g_email` from `group`,`project` where `project`.`id`=? and `group`.`id`=`project`.`g_id`"
        val myDBHelper= MyDBHelper(this)
        val db=myDBHelper.readableDatabase
        val information=db.rawQuery(sql, arrayOf(intent.getStringExtra("projectId")))
        val information2=db.rawQuery(sql2, arrayOf(intent.getStringExtra("projectId")))
        if (information.moveToFirst()&&information2.moveToFirst()){
            binding.projectName.text=information.getString(information.getColumnIndex("p_name"))
            binding.projectStartEndTime.text=information.getString(information.getColumnIndex("p_start_time"))+"至"+information.getString(information.getColumnIndex("p_end_time"))
            binding.projectAddress.text=information.getString(information.getColumnIndex("p_address"))
            binding.projectServiceTime.text=information.getString(information.getColumnIndex("p_service_time"))
            binding.projectGroup.text=information2.getString(information2.getColumnIndex("g_name"))
            binding.projectPrincipalName.text=information2.getString(information2.getColumnIndex("principal_name"))
            photon=information2.getString(information2.getColumnIndex("principal_phone"))
            email=information2.getString(information2.getColumnIndex("g_email"))
        }

    }
    fun getDate():String{
        val calendar= Calendar.getInstance()
        val simpleDateFormat= SimpleDateFormat("yyyy-MM-dd")
        return simpleDateFormat.format(calendar.time)
    }
}