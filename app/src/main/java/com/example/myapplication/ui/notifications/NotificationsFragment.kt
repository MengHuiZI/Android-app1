package com.example.myapplication.ui.notifications

import android.annotation.SuppressLint
import android.content.Context
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
import com.example.myapplication.ProjectInfo
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentVolunteerprojectsBinding

class NotificationsFragment : Fragment() {

    private var binding: FragmentVolunteerprojectsBinding? = null
    var list=ArrayList<HashMap<String,String>>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentVolunteerprojectsBinding.inflate(layoutInflater)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id=requireContext().getSharedPreferences("UserInfo",Context.MODE_PRIVATE).getString("userId","")
        if (id!=""){
            if (id?.split("-")?.get(1)=="volunteer"){
                selectSQL()
                binding!!.projectList.setOnItemClickListener { parent, view, position, id ->
                    val intent= Intent(requireContext(), ProjectInfo::class.java)
                    intent.putExtra("projectId", list[position]["pId"])
                    startActivity(intent)
                }
            }else{
                val map=HashMap<String,String>()
                map["group_name"]="团体不能加入项目"
                list.add(map)
                val adapter= SimpleAdapter(requireContext(),list, R.layout.group_list,
                    arrayOf("group_name","group_address","group_peopleNumber","group_time","group_registerDate"),
                    intArrayOf(R.id.group_name, R.id.group_address, R.id.group_peopleNumber, R.id.group_time, R.id.group_registerDate)
                )
                binding!!.projectList.adapter=adapter
            }
        }else Toast.makeText(requireContext(),"登陆后并加入团体才能申请项目",Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    //加载list
    @SuppressLint("Range")
    fun selectSQL(){
        list.clear()
        val myDBHelper= MyDBHelper(requireContext())
        val db=myDBHelper.readableDatabase
        val id=requireContext().getSharedPreferences("UserInfo",Context.MODE_PRIVATE).getString("userId","")
        val groupInformation=db.rawQuery("select `g_id`from`volunteer_group` where `v_id`=? and `state`='已通过'", arrayOf(id?.split("-")?.get(0)))
        if (groupInformation.moveToFirst()){
            do {
                val sql1="select * from `project` where `g_id`=?"
                val information=db.rawQuery(sql1, arrayOf(groupInformation.getString(groupInformation.getColumnIndex("g_id"))))
                if (information.moveToFirst()){
                    do{
                        val map=HashMap<String,String>()
                        map["group_name"]=information.getString(information.getColumnIndex("p_name"))
                        map["group_time"]=information.getString(information.getColumnIndex("p_start_time"))
                        map["group_address"]=information.getString(information.getColumnIndex("p_address"))
                        val pId=information.getString(information.getColumnIndex("id"))
                        map["pId"]=pId
                        val number=db.rawQuery("select count(*)as `number` from `volunteer_project` where `p_id`=? and `state`='已通过'", arrayOf(pId))
                        number.moveToFirst()
                        map["group_peopleNumber"]=""+number.getString(number.getColumnIndex("number"))
                        list.add(map)
                    }while (information.moveToNext())
                }
            }while (groupInformation.moveToNext())
            val adapter= SimpleAdapter(requireContext(),list, R.layout.group_list,
                arrayOf("group_name","group_address","group_peopleNumber","group_time","group_registerDate"),
                intArrayOf(R.id.group_name, R.id.group_address, R.id.group_peopleNumber, R.id.group_time, R.id.group_registerDate)
            )
            binding!!.projectList.adapter=adapter
        }
    }
}