package com.example.myapplication.volunteerMessage

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleAdapter
import com.example.myapplication.DB.MyDBHelper
import com.example.myapplication.ProjectInfo
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentVolunteerTimeJoinedBinding

class VolunteerTimeJoinedFragment : Fragment() {
    var binding: FragmentVolunteerTimeJoinedBinding?=null
    private var list=ArrayList<HashMap<String,String>>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentVolunteerTimeJoinedBinding.inflate(layoutInflater)
        return binding!!.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getInformation()
        //列表没想监听
        binding!!.volunteerTimeJoinedList.setOnItemClickListener { parent, view, position, id ->
            val pid=list[position]["pId"]
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
        val sql="select `p_name`,`apply_times`,`apply_date`,`p_id` from `project`,`volunteer_project_time` where `project`.`id`=p_id and `v_id`=? and `state`='已通过'"
        val sql1="select `g_name` from `group`,`project` where `g_id`=`group`.`id`and `project`.`id`=?"
        val db= MyDBHelper(requireContext()).writableDatabase
        val groupInformation=db.rawQuery(sql, arrayOf(id))
        if (groupInformation.moveToFirst()){
            do {
                val map=HashMap<String,String>()
                map["timeApplyDate"] = groupInformation.getString(groupInformation.getColumnIndex("apply_date"))
                map["times"] = groupInformation.getString(groupInformation.getColumnIndex("apply_times"))+"小时"
                map["projectName"] = groupInformation.getString(groupInformation.getColumnIndex("p_name"))
                val pId = groupInformation.getString(groupInformation.getColumnIndex("p_id"))
                map["pId"]=pId
                val gTime=db.rawQuery(sql1, arrayOf(pId))
                if (gTime.moveToFirst()){
                    map["groupName"]=gTime.getString(gTime.getColumnIndex("g_name"))
                }
                list.add(map)
            }while (groupInformation.moveToNext())
        }
        val adapter= SimpleAdapter(requireContext(),list,R.layout.volunteer_time_list,
            arrayOf("timeApplyDate","times","projectName","groupName"),
            intArrayOf(R.id.time_applyDate,R.id.times,R.id.project_name,R.id.group_name))
        binding!!.volunteerTimeJoinedList.adapter=adapter
    }
}