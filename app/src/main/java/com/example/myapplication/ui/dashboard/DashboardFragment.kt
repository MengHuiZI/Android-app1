package com.example.myapplication.ui.dashboard

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.DB.MyDBHelper
import com.example.myapplication.GroupInfo
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentVolunteergroupsBinding

class DashboardFragment : Fragment() {

    private var binding: FragmentVolunteergroupsBinding? = null
    var list=ArrayList<HashMap<String,String>>()

    // This property is only valid between onCreateView and
    // onDestroyView.

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentVolunteergroupsBinding.inflate(layoutInflater)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sql1="select * from `group`"
        selectSQL(sql1)

        binding!!.groupList.setOnItemClickListener { parent, view, position, id ->
            val intent=Intent(requireContext(),GroupInfo::class.java)
            intent.putExtra("groupId", list[position]["id"])
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
    //加载list
    @SuppressLint("Range")
    fun selectSQL(sql1:String){
        list.clear()
        val myDBHelper=MyDBHelper(requireContext())
        val db=myDBHelper.readableDatabase
        val groupInformation=db.rawQuery(sql1,null)
        if (groupInformation.moveToFirst()){
            do {
                val map=HashMap<String,String>()
                map["id"]=groupInformation.getString(groupInformation.getColumnIndex("id"))
                map["group_name"] = groupInformation.getString(groupInformation.getColumnIndex("g_name"))
                map["group_address"]=groupInformation.getString(groupInformation.getColumnIndex("g_address"))
                map["group_registerDate"]="成立日期："+groupInformation.getString(groupInformation.getColumnIndex("register_date"))
                val sql2="select count(`volunteer_group`.`id`) as `number`,sum(`service_time`) as `sumServiceTime` from `user`,`volunteer_group` where `v_id`=`user`.`id`and `g_id`=?"
                val information=db.rawQuery(sql2, arrayOf(groupInformation.getString(groupInformation.getColumnIndex("id"))))
                if (information.moveToFirst()){
                    map["group_peopleNumber"]="正式成员："+information.getString(information.getColumnIndex("number"))
                    map["group_time"]="服务时间："+if(information.getString(information.getColumnIndex("sumServiceTime"))==null) "0"+"小时" else information.getString(information.getColumnIndex("sumServiceTime"))+"小时"
                }else {
                    map["group_peopleNumber"] = "正式成员：0"
                    map["group_time"] = "服务时间：0小时"
                }
                list.add(map)
            }while (groupInformation.moveToNext())
            val adapter=SimpleAdapter(requireContext(),list, R.layout.group_list,
            arrayOf("group_name","group_address","group_peopleNumber","group_time","group_registerDate"),
                intArrayOf(R.id.group_name,R.id.group_address,R.id.group_peopleNumber,R.id.group_time,R.id.group_registerDate)
            )
            binding!!.groupList.adapter=adapter
        }
    }
}