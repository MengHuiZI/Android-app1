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
import com.example.myapplication.GroupInfo
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentVolunteerGroupRejectedBinding


class VolunteerGroupRejectedFragment : Fragment() {

    var binding:FragmentVolunteerGroupRejectedBinding?=null
    private var list=ArrayList<HashMap<String,String>>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentVolunteerGroupRejectedBinding.inflate(layoutInflater)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getInformation()
        //列表没想监听
        binding!!.volunteerGroupRejectedList.setOnItemClickListener { parent, view, position, id ->
            val gid=list[position]["gId"]
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
                    val db=MyDBHelper(requireContext()).writableDatabase
                    if (db.delete("volunteer_group","v_id=? and g_id=?", arrayOf(id.toString(),gid))>0) {
                        getInformation()
                    } else Toast.makeText(requireContext(),"删除失败", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                dialog.findViewById<TextView>(R.id.dialog_exist).setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
            }
            val intent= Intent(requireContext(), GroupInfo::class.java)
            intent.putExtra("groupId",gid)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding=null
    }

    @SuppressLint("Range")
    fun getInformation(){
        list.clear()
        val id=requireContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE).getString("userId","")?.split("-")?.get(0)
        val sql="select `g_name`,`principal_name`,`register_date`,`group`.`id` as `gId` from `group`,`volunteer_group` where `group`.`id`=g_id and `v_id`=? and `state`='已拒绝'"
        val sql1="select sum(`service_time`) as `sumServiceTime` from `user`,`volunteer_group` where `v_id`=`user`.`id`and `g_id`=?"
        val db= MyDBHelper(requireContext()).writableDatabase
        val groupInformation=db.rawQuery(sql, arrayOf(id))
        if (groupInformation.moveToFirst()){
            do {
                val map=HashMap<String,String>()
                map["principalName"] = "联系人："+groupInformation.getString(groupInformation.getColumnIndex("principal_name"))
                map["gName"] = groupInformation.getString(groupInformation.getColumnIndex("g_name"))
                map["registerDate"] = groupInformation.getString(groupInformation.getColumnIndex("register_date"))
                val gId = groupInformation.getString(groupInformation.getColumnIndex("gId"))
                map["gId"]=gId
                val gTime=db.rawQuery(sql1, arrayOf(gId))
                if (gTime.moveToFirst()){
                    map["gTime"]="共"+gTime.getString(gTime.getColumnIndex("sumServiceTime"))+"小时"
                }
                list.add(map)
            }while (groupInformation.moveToNext())
        }
        val adapter= SimpleAdapter(requireContext(),list,R.layout.volunteer_group_list,
            arrayOf("registerDate","gTime","gName","principalName"),
            intArrayOf(R.id.group_registerDate,R.id.group_time,R.id.group_name,R.id.group_principalName)
        )
        binding!!.volunteerGroupRejectedList.adapter=adapter
    }
}