package com.example.myapplication.GroupMessage

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.SimpleAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.myapplication.DB.MyDBHelper
import com.example.myapplication.GroupInfo
import com.example.myapplication.ProjectInfo
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityGroupProjectBinding
import com.example.myapplication.volunteerMessage.VolunteerProject
import com.example.myapplication.volunteerMessage.VolunteerProjectApplyingFragment
import com.example.myapplication.volunteerMessage.VolunteerProjectJoinedFragment
import com.example.myapplication.volunteerMessage.VolunteerProjectRejectedFragment
import com.google.android.material.tabs.TabLayout

class GroupProject : AppCompatActivity() {
    lateinit var binding:ActivityGroupProjectBinding
    var list=ArrayList<HashMap<String,String>>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityGroupProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val myPagerAdapter= GroupProjectPagerAdapter(supportFragmentManager)
        val viewpager=binding.viewPager
        viewpager.adapter=myPagerAdapter
        val tabs=binding.tabs
        tabs.setupWithViewPager(viewpager)

        val tab1=tabs.newTab()
        tab1.text="已有项目"
        tabs.addTab(tab1)

        val tab2=tabs.newTab()
        tab2.text="加入申请"
        tabs.addTab(tab2)


        tabs.removeTabAt(0)
        tabs.removeTabAt(0)

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewpager.currentItem=tab?.position!!
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })
    }

    class GroupProjectPagerAdapter(fragment: FragmentManager): FragmentPagerAdapter(fragment){
        override fun getCount(): Int {
            return 2
        }

        override fun getItem(position: Int): Fragment {
            return when(position){
                0-> GroupProjectContainFragment()
                1-> GroupProjectVolunteerApplyingFragment()
                else->throw IllegalAccessException("无效位置：$position")
            }
        }

    }

}