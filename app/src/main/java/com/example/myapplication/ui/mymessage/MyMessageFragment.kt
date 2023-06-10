package com.example.myapplication.ui.mymessage

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.myapplication.DB.MyDBHelper
import com.example.myapplication.GroupMessage.*
import com.example.myapplication.Login
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentMyMessageBinding
import com.example.myapplication.databinding.FragmentMymessageGroupBinding
import com.example.myapplication.volunteerMessage.*
import java.io.ByteArrayOutputStream

class MyMessageFragment : Fragment() {
    var binding:FragmentMyMessageBinding?=null
    var bindingGroup:FragmentMymessageGroupBinding?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sp=requireContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        if (sp.getString("userId","")==""){
            val intent=Intent(requireContext(),Login::class.java)
            intent.putExtra("code","intent")
            startActivity(intent)
            onDestroyView()
            return binding?.root
        }else{
            if (sp.getString("userId","")?.split("-")?.get(1)=="volunteer"){
                binding= FragmentMyMessageBinding.inflate(layoutInflater)
                return binding!!.root
            }else{
                bindingGroup= FragmentMymessageGroupBinding.inflate(layoutInflater)
                return bindingGroup!!.root
            }
        }
    }

    @SuppressLint("Range")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        bindingGroup=null
    }

    override fun onResume() {
        super.onResume()
        initFragment()
    }

    @SuppressLint("Range")
    fun initFragment(){
        val sp=requireContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        if (sp.getString("userId","")?.split("-")?.get(1)=="volunteer"){
            //志愿者的视图
            //加载信息
            val myDBHelper=MyDBHelper(requireContext())
            val db=myDBHelper.readableDatabase
            val sql="select `name`,`id`,`head_portrait`,`service_time` from `user` where `id`=?"
            val sql1="select count(*) as `number` from `volunteer_project` where `v_id`=?"
            val sql2="select count(*) `number` from `volunteer_group` where `v_id`=? and `state`='已通过'"
            val id= sp.getString("userId","")?.split("-")?.get(0)
            val volunteerInformation=db.rawQuery(sql, arrayOf(id))
            val volunteerProject=db.rawQuery(sql1, arrayOf(id))
            val volunteerGroup=db.rawQuery(sql2, arrayOf(id))
            volunteerInformation.moveToFirst()
            volunteerProject.moveToFirst()
            volunteerGroup.moveToFirst()
            binding!!.vName.text=volunteerInformation.getString(volunteerInformation.getColumnIndex("name"))
            binding!!.vVolunteerId.text="志愿者编号："+volunteerInformation.getString(volunteerInformation.getColumnIndex("id"))
            val image=volunteerInformation.getBlob(volunteerInformation.getColumnIndex("head_portrait"))
            binding!!.ibHeadPortrait.setImageBitmap(BitmapFactory.decodeByteArray(image,0,image.size))
            binding!!.vTiems.text=volunteerInformation.getString(volunteerInformation.getColumnIndex("service_time"))+"小时"
            binding!!.vProjects.text=volunteerProject.getString(volunteerProject.getColumnIndex("number"))+"项目"
            binding!!.vGroups.text=volunteerGroup.getString(volunteerGroup.getColumnIndex("number"))+"团体"

            //监听
            //头像
            binding!!.ibHeadPortrait.setOnClickListener {
                val dialog= Dialog(requireContext())
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.mymessage_ibimage_dialog)
                dialog.window?.setGravity(Gravity.BOTTOM)
                dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
                dialog.window?.setWindowAnimations(com.google.android.material.R.style.Animation_MaterialComponents_BottomSheetDialog)
                dialog.findViewById<TextView>(R.id.dialog_exist).setOnClickListener{
                    dialog.dismiss()
                }
                dialog.findViewById<TextView>(R.id.dialog_image).setOnClickListener {
                    val intent=Intent(Intent.ACTION_OPEN_DOCUMENT)
                    intent.type="image/*"
                    startActivityForResult(intent,1)
                    dialog.dismiss()
                }
                dialog.show()
            }
            //项目
            binding!!.vProjects.setOnClickListener {
                startActivity(Intent(requireContext(), VolunteerProject::class.java))
            }
            //团体
            binding!!.vGroups.setOnClickListener {
                startActivity(Intent(requireContext(),VolunteerGroup::class.java))
            }
            //时长
            binding!!.vTiems.setOnClickListener {
                startActivity(Intent(requireContext(), VolunteerTime::class.java))
            }
            //详细信息
            binding!!.vUpdateMessage.setOnClickListener {
                startActivity(Intent(requireContext(), UpdateVolunteerMessage::class.java))

            }
            //密码修改
            binding!!.vUpdatePassword.setOnClickListener {
                startActivity(Intent(requireContext(), UpdateVolunteerPassword::class.java))

            }
            //退出登陆
            binding!!.exist.setOnClickListener {
                val edit=sp.edit().putString("userId","")
                edit.commit()
                val intent=Intent(requireContext(),Login::class.java)
                intent.putExtra("code","intent")
                startActivity(intent)
                requireActivity().finish()
            }
        }else{
            //团体的视图
            //加载信息
            val myDBHelper=MyDBHelper(requireContext())
            val db=myDBHelper.readableDatabase
            val sql="select `g_name`,`id`,`image` from `group` where `id`=?"
            val sql1="select count(*) as `number` from `project` where `g_id`=?"
            val sql2="select count(*) `number` from `volunteer_project_time`,`project` where `volunteer_project_time`.`p_id`=`project`.`id` and `g_id`=? and `volunteer_project_time`.`state`='待审核'"
            val sql3="select count(*) as `number` from `volunteer_group` where `g_id`=? and `state`='待审核'"
            val sql4="select count(*) as `number` from `volunteer_group` where `g_id`=? and `state`='已通过'"
            val sql5="select sum(`service_time`) as `sumServiceTime` from `user`,`volunteer_group` where `v_id`=`user`.`id`and `g_id`=?"
            val id= sp.getString("userId","")?.split("-")?.get(0)
            val groupInformation=db.rawQuery(sql, arrayOf(id))
            val groupProject=db.rawQuery(sql1, arrayOf(id))
            val groupApplyingTime=db.rawQuery(sql2, arrayOf(id))
            val groupVolunteer=db.rawQuery(sql3, arrayOf(id))
            val groupVolunteerNumber=db.rawQuery(sql4, arrayOf(id))
            val groupTime=db.rawQuery(sql5, arrayOf(id))
            groupInformation.moveToFirst()
            groupProject.moveToFirst()
            groupTime.moveToFirst()
            groupVolunteer.moveToFirst()
            groupApplyingTime.moveToFirst()
            groupVolunteerNumber.moveToFirst()
            bindingGroup!!.vName.text=groupInformation.getString(groupInformation.getColumnIndex("g_name"))
            bindingGroup!!.vVolunteerId.text="团体编号："+groupInformation.getString(groupInformation.getColumnIndex("id"))
            val image=groupInformation.getBlob(groupInformation.getColumnIndex("image"))
            bindingGroup!!.ibHeadPortrait.setImageBitmap(BitmapFactory.decodeByteArray(image,0,image.size))
            bindingGroup!!.gTimes.text=groupApplyingTime.getString(groupApplyingTime.getColumnIndex("number"))+"时长申请"
            bindingGroup!!.gProjects.text="项目"
            bindingGroup!!.gJoinedPeople.text="成员："+groupVolunteerNumber.getString(groupVolunteerNumber.getColumnIndex("number"))
            bindingGroup!!.gJoinedTime.text="时长："+groupTime.getString(groupTime.getColumnIndex("sumServiceTime"))
            bindingGroup!!.gPeople.text=groupVolunteer.getString(groupVolunteer.getColumnIndex("number"))+"成员申请"
            //监听
            //头像
            bindingGroup!!.ibHeadPortrait.setOnClickListener {
                val dialog= Dialog(requireContext())
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.mymessage_ibimage_dialog)
                dialog.window?.setGravity(Gravity.BOTTOM)
                dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
                dialog.window?.setWindowAnimations(com.google.android.material.R.style.Animation_MaterialComponents_BottomSheetDialog)
                dialog.findViewById<TextView>(R.id.dialog_exist).setOnClickListener{
                    dialog.dismiss()
                }
                dialog.findViewById<TextView>(R.id.dialog_image).setOnClickListener {
                    val intent=Intent(Intent.ACTION_OPEN_DOCUMENT)
                    intent.type="image/*"
                    startActivityForResult(intent,1)
                    dialog.dismiss()
                }
                dialog.show()
            }
            //项目
            bindingGroup!!.gProjects.setOnClickListener {
                startActivity(Intent(requireContext(), GroupProject::class.java))
            }
            //成员
            bindingGroup!!.gPeople.setOnClickListener {
                startActivity(Intent(requireContext(),GroupVolunteer::class.java))
            }
            //时长
            bindingGroup!!.gTimes.setOnClickListener {
                startActivity(Intent(requireContext(), GroupTime::class.java))
            }
            //详细信息
            bindingGroup!!.gUpdateMessage.setOnClickListener {
                startActivity(Intent(requireContext(), GroupUpdateMessage::class.java))

            }
            //密码修改
            bindingGroup!!.gUpdatePassword.setOnClickListener {
                startActivity(Intent(requireContext(), GroupUpdatePassword::class.java))

            }
            //添加项目
            bindingGroup!!.gAddProject.setOnClickListener {
                startActivity(Intent(requireContext(),GroupAddProject::class.java))
            }
            bindingGroup!!.exist.setOnClickListener {
                val edit=sp.edit().putString("userId","")
                edit.commit()
                val intent=Intent(requireContext(),Login::class.java)
                intent.putExtra("code","intent")
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }

    //接收回传的图片并处理
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            1->{
                if (resultCode== Activity.RESULT_OK&&data!=null){
                    data.data?.let {
                        val inputStream=context?.contentResolver?.openInputStream(it)
                        val bitmap=BitmapFactory.decodeStream(inputStream)
                        val id= requireContext().getSharedPreferences("UserInfo",Context.MODE_PRIVATE).getString("userId","")?.split("-")?.get(0)
                        if (requireContext().getSharedPreferences("UserInfo",Context.MODE_PRIVATE).getString("userId","")?.split("-")?.get(1)=="volunteer"){
                            binding!!.ibHeadPortrait.setImageBitmap(bitmap)
                            val os=ByteArrayOutputStream()
                            bitmap?.compress(Bitmap.CompressFormat.PNG,100,os)
                            val db=MyDBHelper(requireContext()).writableDatabase
                            val values=ContentValues().apply{
                                put("head_portrait",os.toByteArray())
                            }
                            db.update("user",values,"id=?", arrayOf(id))
                        }else{
                            bindingGroup!!.ibHeadPortrait.setImageBitmap(bitmap)
                            val os=ByteArrayOutputStream()
                            bitmap?.compress(Bitmap.CompressFormat.PNG,100,os)
                            val db=MyDBHelper(requireContext()).writableDatabase
                            val values=ContentValues().apply{
                                put("image",os.toByteArray())
                            }
                            db.update("`group`",values,"id=?", arrayOf(id))
                        }
                    }
                }
            }
        }
    }
}