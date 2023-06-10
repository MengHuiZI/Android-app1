package com.example.myapplication.GroupMessage

import android.annotation.SuppressLint
import android.app.Dialog
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
import com.example.myapplication.GroupInfo
import com.example.myapplication.ProjectInfo
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentGroupProjectContainBinding
import com.example.myapplication.databinding.FragmentVolunteerGroupJoinedBinding


class GroupProjectContainFragment : Fragment() {

    var binding: FragmentGroupProjectContainBinding?=null
    private var list=ArrayList<HashMap<String,String>>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentGroupProjectContainBinding.inflate(layoutInflater)
        return binding!!.root
    }

    @SuppressLint("Range")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        binding!!.groupProjectContainList.setOnItemClickListener { parent, view, position, id ->
            val dialog= Dialog(requireContext())
            dialog.setContentView(R.layout.groupinfo_dialog)
            dialog.setCancelable(false)
            //控件赋值
            dialog.findViewById<TextView>(R.id.groupInfo_dialog_photon).text="详细信息"
            dialog.findViewById<TextView>(R.id.groupInfo_dialog_email).text="删除"
            //从底部弹出
            dialog.window?.setGravity(Gravity.BOTTOM)
            //宽高设置
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            //弹出动画
            dialog.window?.setWindowAnimations(com.google.android.material.R.style.Animation_MaterialComponents_BottomSheetDialog)
            //设置dialog监听
            dialog.findViewById<TextView>(R.id.groupInfo_dialog_photon).setOnClickListener {
                val intent= Intent(requireContext(), ProjectInfo::class.java)
                intent.putExtra("projectId", list[position]["pId"])
                startActivity(intent)
                dialog.dismiss()
            }
            dialog.findViewById<TextView>(R.id.groupInfo_dialog_email).setOnClickListener {
                val db=MyDBHelper(requireContext()).writableDatabase
                val i=db.delete("`project`","`id`=?", arrayOf(list[position]["pId"]))
                if (i>0){
                    dialog.dismiss()
                    init()
                    Toast.makeText(requireContext(),"删除成功",Toast.LENGTH_SHORT).show()
                }else Toast.makeText(requireContext(),"删除失败",Toast.LENGTH_SHORT).show()
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
        val sql="select `p_name`,`p_address`,`p_start_time`,`id` from `project` where `g_id`=? "
        val sql1="select count(*) as`number` from `volunteer_project` where `p_id`=? and state='已通过'"
        val db= MyDBHelper(requireContext()).writableDatabase
        val groupInformation=db.rawQuery(sql, arrayOf(id))
        if (groupInformation.moveToFirst()){
            do {
                val map=HashMap<String,String>()
                map["projectName"] = groupInformation.getString(groupInformation.getColumnIndex("p_name"))
                map["projectAddress"] = groupInformation.getString(groupInformation.getColumnIndex("p_address"))
                map["projectStartTime"] = groupInformation.getString(groupInformation.getColumnIndex("p_start_time"))
                val pId = groupInformation.getString(groupInformation.getColumnIndex("id"))
                map["pId"]=pId
                val gTime=db.rawQuery(sql1, arrayOf(pId))
                if (gTime.moveToFirst()){
                    map["projectNumber"]="共"+gTime.getString(gTime.getColumnIndex("number"))+"人"
                }
                list.add(map)
            }while (groupInformation.moveToNext())
        }
        val adapter= SimpleAdapter(requireContext(),list,R.layout.group_project_list,
            arrayOf("projectName","projectAddress","projectStartTime","projectNumber"),
            intArrayOf(R.id.group_project_list_name,R.id.group_project_list_address,R.id.group_project_list_start_time,R.id.group_project_list_people)
        )
        binding!!.groupProjectContainList.adapter=adapter
    }
}