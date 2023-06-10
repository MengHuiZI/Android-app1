package com.example.myapplication.GroupMessage

import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import com.example.myapplication.DB.MyDBHelper
import com.example.myapplication.databinding.ActivityGroupAddProjectBinding
import java.text.SimpleDateFormat
import java.util.Calendar

class GroupAddProject : AppCompatActivity() {
    lateinit var binding:ActivityGroupAddProjectBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityGroupAddProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.groupAddProjectStartTime.setOnClickListener {
            chooseStartTime()
        }
        binding.groupAddProjectEndTime.setOnClickListener {
            chooseEndTime()
        }
        binding.addButton.setOnClickListener {
            val name=binding.groupAddProjectName.text.toString()
            val address=binding.groupAddProjectAddress.text.toString()
            val startTime=binding.groupAddProjectStartTime.text.toString()
            val endTime=binding.groupAddProjectEndTime.text.toString()
            val serviceTime=binding.groupAddProjectServiceTime.text.toString()
            val groupId= getSharedPreferences("UserInfo", Context.MODE_PRIVATE).getString("userId","")?.split("-")?.get(0)
            val db=MyDBHelper(this).writableDatabase
            val values=ContentValues().apply {
                put("p_name",name)
                put("p_address",address)
                put("p_start_time",startTime)
                put("p_end_time",endTime)
                put("p_service_time",serviceTime)
                put("g_id",groupId)
            }
            val i=db.insert("`project`",null,values)
            if (i>0) {
                Toast.makeText(this,"添加成功",Toast.LENGTH_SHORT).show()
                finish()
            } else Toast.makeText(this,"添加失败",Toast.LENGTH_SHORT).show()
        }
    }

    //时间选择器
    fun chooseStartTime(){
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(this,{ _: DatePicker, year: Int, month: Int, day: Int ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)

            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
            val date=simpleDateFormat.format(calendar.time)
            binding.groupAddProjectStartTime.text=date
            }, year, month, day)
        datePickerDialog.show()
    }
    fun chooseEndTime(){
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(this,{ _: DatePicker, year: Int, month: Int, day: Int ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)

            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
            val date=simpleDateFormat.format(calendar.time)
            binding.groupAddProjectEndTime.text=date
        }, year, month, day)
        datePickerDialog.show()
    }
}