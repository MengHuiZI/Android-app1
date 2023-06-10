package com.example.myapplication.GroupMessage

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.SimpleAdapter
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.DB.MyDBHelper
import com.example.myapplication.ProjectInfo
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityGroupVolunteerBinding
import com.example.myapplication.ui.mymessage.MyMessageFragment

class GroupVolunteer : AppCompatActivity() {
    lateinit var binding:ActivityGroupVolunteerBinding
    var list=ArrayList<HashMap<String,String>>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityGroupVolunteerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        binding.GroupProjectList.setOnItemClickListener { parent, view, position, id ->
            val dialog= Dialog(this)
            dialog.setContentView(R.layout.groupinfo_dialog)
            dialog.setCancelable(false)
            //控件赋值
            dialog.findViewById<TextView>(R.id.groupInfo_dialog_photon).text="通过"
            dialog.findViewById<TextView>(R.id.groupInfo_dialog_email).text="拒绝"
            //从底部弹出
            dialog.window?.setGravity(Gravity.BOTTOM)
            //宽高设置
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            //弹出动画
            dialog.window?.setWindowAnimations(com.google.android.material.R.style.Animation_MaterialComponents_BottomSheetDialog)
            //设置dialog监听
            dialog.findViewById<TextView>(R.id.groupInfo_dialog_photon).setOnClickListener {
                val db=MyDBHelper(this).writableDatabase
                val value=ContentValues().apply {
                    put("state","已通过")
                }
                val gId=getSharedPreferences("UserInfo",Context.MODE_PRIVATE).getString("userId","")?.split("-")?.get(0)
                val i=db.update("`volunteer_group`",value,"v_id=? and g_id=?", arrayOf(list[position]["vId"],gId))
                if (i>0){
                    dialog.dismiss()
                    Toast.makeText(this,"已通过", Toast.LENGTH_SHORT).show()
                    init()
                }else Toast.makeText(this,"修改失败", Toast.LENGTH_SHORT).show()
            }
            dialog.findViewById<TextView>(R.id.groupInfo_dialog_email).setOnClickListener {
                val db=MyDBHelper(this).writableDatabase
                val value=ContentValues().apply {
                    put("state","已拒绝")
                }
                val gId=getSharedPreferences("UserInfo",Context.MODE_PRIVATE).getString("userId","")?.split("-")?.get(0)
                val i=db.update("`volunteer_group`",value,"v_id=? and g_id=?", arrayOf(list[position]["vId"],gId))
                if (i>0){
                    dialog.dismiss()
                    Toast.makeText(this,"已拒绝", Toast.LENGTH_SHORT).show()
                    init()
                }else Toast.makeText(this,"修改失败", Toast.LENGTH_SHORT).show()
            }
            dialog.findViewById<TextView>(R.id.groupInfo_dialog_exist).setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    @SuppressLint("Range")
    fun init(){
        list.clear()
        val id=getSharedPreferences("UserInfo", Context.MODE_PRIVATE).getString("userId","")?.split("-")?.get(0)
        val sql="select `v_g_apply_date`,`name`,`email`,`v_id` from `user`,`volunteer_group` where `g_id`=? and `user`.`id`=`volunteer_group`.`v_id`and `state`='待审核'"
        val db= MyDBHelper(this).writableDatabase
        val groupInformation=db.rawQuery(sql, arrayOf(id))
        if (groupInformation.moveToFirst()){
            do {
                val map=HashMap<String,String>()
                map["volunteerName"] = groupInformation.getString(groupInformation.getColumnIndex("name"))
                map["volunteerEmail"] = groupInformation.getString(groupInformation.getColumnIndex("email"))
                map["applyDate"] = groupInformation.getString(groupInformation.getColumnIndex("v_g_apply_date"))
                map["vId"] = groupInformation.getString(groupInformation.getColumnIndex("v_id"))
                list.add(map)
            }while (groupInformation.moveToNext())
        }
        val adapter= SimpleAdapter(this,list,R.layout.group_volunteer_list,
            arrayOf("volunteerName","volunteerEmail","applyDate"),
            intArrayOf(R.id.group_volunteer_list_name,R.id.group_volunteer_list_email,R.id.group_volunteer_list_apply_date)
        )
        binding.GroupProjectList.adapter=adapter
    }
}