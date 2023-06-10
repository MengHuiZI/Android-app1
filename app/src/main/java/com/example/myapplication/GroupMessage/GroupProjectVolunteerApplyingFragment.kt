package com.example.myapplication.GroupMessage

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleAdapter
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.DB.MyDBHelper
import com.example.myapplication.ProjectInfo
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentGroupProjectVolunteerApplyingBinding


class GroupProjectVolunteerApplyingFragment : Fragment() {

     var binding:FragmentGroupProjectVolunteerApplyingBinding?=null
    private var list=ArrayList<HashMap<String,String>>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentGroupProjectVolunteerApplyingBinding.inflate(layoutInflater)
        return binding!!.root
    }

    @SuppressLint("Range")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        binding!!.groupProjectVolunteerApplyingList.setOnItemClickListener { parent, view, position, id ->
            val dialog= Dialog(requireContext())
            dialog.setContentView(R.layout.groupinfo_dialog)
            dialog.setCancelable(false)
            //控件赋值
            dialog.findViewById<TextView>(R.id.groupInfo_dialog_photon).text="同意"
            dialog.findViewById<TextView>(R.id.groupInfo_dialog_email).text="拒绝"
            //从底部弹出
            dialog.window?.setGravity(Gravity.BOTTOM)
            //宽高设置
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            //弹出动画
            dialog.window?.setWindowAnimations(com.google.android.material.R.style.Animation_MaterialComponents_BottomSheetDialog)
            //设置dialog监听
            dialog.findViewById<TextView>(R.id.groupInfo_dialog_photon).setOnClickListener {
                val db=MyDBHelper(requireContext()).writableDatabase
                val value= ContentValues().apply {
                    put("state","已通过")
                }
                val i=db.update("`volunteer_project`",value,"v_id=? and p_id=?", arrayOf(list[position]["vId"],list[position]["pId"]))
                if (i>0){
                    dialog.dismiss()
                    Toast.makeText(requireContext(),"已通过", Toast.LENGTH_SHORT).show()
                    init()
                }else Toast.makeText(requireContext(),"修改失败", Toast.LENGTH_SHORT).show()
            }
            dialog.findViewById<TextView>(R.id.groupInfo_dialog_email).setOnClickListener {
                val db=MyDBHelper(requireContext()).writableDatabase
                val value= ContentValues().apply {
                    put("state","已拒绝")
                }
                val i=db.update("`volunteer_project`",value,"v_id=? and p_id=?", arrayOf(list[position]["vId"],list[position]["pId"]))
                if (i>0){
                    dialog.dismiss()
                    Toast.makeText(requireContext(),"已拒绝", Toast.LENGTH_SHORT).show()
                    init()
                }else Toast.makeText(requireContext(),"修改失败", Toast.LENGTH_SHORT).show()
            }
            dialog.findViewById<TextView>(R.id.groupInfo_dialog_exist).setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
    @SuppressLint("Range")
    fun init(){
        list.clear()
        val id=requireContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE).getString("userId","")?.split("-")?.get(0)
        val sql="select `id` from `project` where `g_id`=? "
        val sql1="select * from `volunteer_project`,`user` where `user`.`id`=`volunteer_project`.`v_id` and `p_id`=? and state='待审核'"
        val db= MyDBHelper(requireContext()).writableDatabase
        val groupInformation=db.rawQuery(sql, arrayOf(id))
        if (groupInformation.moveToFirst()){
            do {
                val pId = groupInformation.getString(groupInformation.getColumnIndex("id"))
                val gTime=db.rawQuery(sql1, arrayOf(pId))
                if (gTime.moveToFirst()){
                    do {
                        val map=HashMap<String,String>()
                        map["vId"]=gTime.getString(gTime.getColumnIndex("v_id"))
                        map["pId"]=gTime.getString(gTime.getColumnIndex("p_id"))
                        map["vName"]=gTime.getString(gTime.getColumnIndex("name"))
                        map["applyDate"]=gTime.getString(gTime.getColumnIndex("v_p_apply_date"))
                        map["vPhone"]=gTime.getString(gTime.getColumnIndex("phone_number"))
                        map["vEmail"]=gTime.getString(gTime.getColumnIndex("email"))
                        list.add(map)
                    }while (gTime.moveToNext())
                }
            }while (groupInformation.moveToNext())
        }
        val adapter= SimpleAdapter(requireContext(),list,R.layout.group_project_list,
            arrayOf("vName","vPhone","applyDate","vEmail"),
            intArrayOf(R.id.group_project_list_name,R.id.group_project_list_address,R.id.group_project_list_start_time,R.id.group_project_list_people)
        )
        binding!!.groupProjectVolunteerApplyingList.adapter=adapter
    }

}