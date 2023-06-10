package com.example.myapplication.GroupMessage

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.SimpleAdapter
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.DB.MyDBHelper
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityGroupTimeBinding

class GroupTime : AppCompatActivity() {
    lateinit var binding:ActivityGroupTimeBinding
    var list=ArrayList<HashMap<String,String>>()
    private lateinit var applyTime:String
    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityGroupTimeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        binding.groupApplyingTimeList.setOnItemClickListener { parent, view, position, id ->
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
                val db= MyDBHelper(this).writableDatabase
                val times=db.rawQuery("select `service_time` from `user`,`volunteer_project_time` where `user`.`id`=`v_id` and`v_id`=? and `p_id`=?", arrayOf(list[position]["vId"],list[position]["pId"]))
                times.moveToFirst()
                val map1=HashMap<String,String>()
                map1["service_time"]= (times.getString(times.getColumnIndex("service_time")).toInt()+ applyTime?.toInt()!!).toString()
                val value1=ContentValues().apply {
                    put("service_time",map1["service_time"])
                }
                val i1=db.update("`user`",value1,"`id`=?", arrayOf(list[position]["vId"]))

                val value= ContentValues().apply {
                    put("state","已通过")
                }
                val i=db.update("`volunteer_project_time`",value,"v_id=? and p_id=?", arrayOf(list[position]["vId"],list[position]["pId"]))
                if (i>0){
                    dialog.dismiss()
                    Toast.makeText(this,"已通过", Toast.LENGTH_SHORT).show()
                    init()
                }else Toast.makeText(this,"修改失败", Toast.LENGTH_SHORT).show()
            }
            dialog.findViewById<TextView>(R.id.groupInfo_dialog_email).setOnClickListener {
                val db= MyDBHelper(this).writableDatabase
                val value= ContentValues().apply {
                    put("state","已拒绝")
                }
                val gId=getSharedPreferences("UserInfo", Context.MODE_PRIVATE).getString("userId","")?.split("-")?.get(0)
                val i=db.update("`volunteer_project_time`",value,"v_id=? and p_id=?", arrayOf(list[position]["vId"],list[position]["pId"]))
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
        val sql="select `apply_date`,`name`,`v_id` ,`project`.`id` as `pId`,`apply_times`,`project_introduce`,`p_name` from `user`,`volunteer_project_time`,`project` where `user`.`id`=`volunteer_project_time`.`v_id` and `project`.`id`=`volunteer_project_time`.`p_id`and `g_id`=? and `state`='待审核'"
        val db= MyDBHelper(this).writableDatabase
        val groupInformation=db.rawQuery(sql, arrayOf(id))
        if (groupInformation.moveToFirst()){
            do {
                val map=HashMap<String,String>()
                map["volunteerName"] = groupInformation.getString(groupInformation.getColumnIndex("name"))
                map["applyDate"] = groupInformation.getString(groupInformation.getColumnIndex("apply_date"))
                map["applyTimes"] = groupInformation.getString(groupInformation.getColumnIndex("apply_times"))+"小时"
                applyTime=groupInformation.getString(groupInformation.getColumnIndex("apply_times"))
                map["projectIntroduce"] = groupInformation.getString(groupInformation.getColumnIndex("project_introduce"))
                map["projectName"] = groupInformation.getString(groupInformation.getColumnIndex("p_name"))
                map["vId"] = groupInformation.getString(groupInformation.getColumnIndex("v_id"))
                map["pId"] = groupInformation.getString(groupInformation.getColumnIndex("pId"))
                list.add(map)
            }while (groupInformation.moveToNext())
        }
        val adapter= SimpleAdapter(this,list,R.layout.volunteer_group_list,
            arrayOf("volunteerName","applyDate","applyTimes","projectName","projectIntroduce"),
            intArrayOf(R.id.group_registerDate,R.id.group_time,R.id.group_tab,R.id.group_name,R.id.group_principalName)
        )
        binding.groupApplyingTimeList.adapter=adapter
    }
}