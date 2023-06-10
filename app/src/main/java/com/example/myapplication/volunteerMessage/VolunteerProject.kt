package com.example.myapplication.volunteerMessage

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.myapplication.databinding.ActivityVolunteerProjectBinding


class VolunteerProject : AppCompatActivity() {

    lateinit var binding: ActivityVolunteerProjectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityVolunteerProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val myPagerAdapter=VolunteerProjectPagerAdapter(supportFragmentManager)
        val viewpager=binding.viewPager
        viewpager.adapter=myPagerAdapter
        val tabs=binding.tabs
        tabs.setupWithViewPager(viewpager)

        val tab1=tabs.newTab()
        tab1.text="已加入"
        tabs.addTab(tab1)

        val tab2=tabs.newTab()
        tab2.text="申请中"
        tabs.addTab(tab2)

        val tab3=tabs.newTab()
        tab3.text="被拒绝"
        tabs.addTab(tab3)

        tabs.removeTabAt(0)
        tabs.removeTabAt(0)
        tabs.removeTabAt(0)

        tabs.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewpager.currentItem=tab?.position!!
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })
    }

    class VolunteerProjectPagerAdapter(fragment: FragmentManager): FragmentPagerAdapter(fragment){
        override fun getCount(): Int {
            return 3
        }

        override fun getItem(position: Int): Fragment {
            return when(position){
                0->VolunteerProjectJoinedFragment()
                1->VolunteerProjectApplyingFragment()
                2->VolunteerProjectRejectedFragment()
                else->throw IllegalAccessException("无效位置：$position")
            }
        }

    }
}