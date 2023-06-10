package com.example.myapplication.volunteerMessage

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
import com.example.myapplication.ProjectInfo
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentVolunteerProjectRejectedBinding


class VolunteerProjectRejectedFragment : Fragment() {

    var binding:FragmentVolunteerProjectRejectedBinding?=null
    private var list=ArrayList<HashMap<String,String>>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentVolunteerProjectRejectedBinding.inflate(layoutInflater)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getInformation()
        //列表没想监听
        binding!!.volunteerProjectRejectedList.setOnItemClickListener { parent, view, position, id ->
            val pid=list[position]["pId"]
            //列表项中右上角按钮监听
            view.findViewById<TextView>(R.id.group_tab).setOnClickListener {
                val id=requireContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE).getString("userId","")?.split("-")?.get(0)
                val dialog= Dialog(requireContext())
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.mymessage_ibimage_dialog)
                dialog.window?.setGravity(Gravity.BOTTOM)
                dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                dialog.window?.setWindowAnimations(com.google.android.material.R.style.Animation_MaterialComponents_BottomSheetDialog)
                dialog.findViewById<TextView>(R.id.dialog_image).text="删除"
                dialog.findViewById<TextView>(R.id.dialog_image).setOnClickListener {
                    val db= MyDBHelper(requireContext()).writableDatabase
                    if (db.delete("volunteer_project","v_id=? and p_id=?", arrayOf(id.toString(),pid))>0) {
                        getInformation()
                    } else Toast.makeText(requireContext(),"删除失败", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                dialog.findViewById<TextView>(R.id.dialog_exist).setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
            }
            val intent= Intent(requireContext(), ProjectInfo::class.java)
            intent.putExtra("projectId",pid)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    @SuppressLint("Range")
    fun getInformation(){
        list.clear()
        val id=requireContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE).getString("userId","")?.split("-")?.get(0)
        val sql="select `p_name`,`p_start_time`,`project`.`id` as `pId`,`p_service_time` from `project`,`volunteer_project` where `project`.`id`=p_id and `v_id`=? and `state`='已拒绝'"
        val sql1="select `g_name` from `group`,`project` where `g_id`=`group`.`id`and `project`.`id`=?"
        val db= MyDBHelper(requireContext()).writableDatabase
        val groupInformation=db.rawQuery(sql, arrayOf(id))
        if (groupInformation.moveToFirst()){
            do {
                val map=HashMap<String,String>()
                map["gTime"] = groupInformation.getString(groupInformation.getColumnIndex("p_service_time"))+"小时"
                map["gName"] = groupInformation.getString(groupInformation.getColumnIndex("p_name"))
                map["registerDate"] = groupInformation.getString(groupInformation.getColumnIndex("p_start_time"))
                val pId = groupInformation.getString(groupInformation.getColumnIndex("pId"))
                map["pId"]=pId
                val gTime=db.rawQuery(sql1, arrayOf(pId))
                if (gTime.moveToFirst()){
                    map["principalName"]=gTime.getString(gTime.getColumnIndex("g_name"))
                }
                list.add(map)
            }while (groupInformation.moveToNext())
        }
        val adapter= SimpleAdapter(requireContext(),list,R.layout.volunteer_group_list,
            arrayOf("registerDate","gTime","gName","principalName"),
            intArrayOf(R.id.group_registerDate,R.id.group_time,R.id.group_name,R.id.group_principalName))
        binding!!.volunteerProjectRejectedList.adapter=adapter
    }
}