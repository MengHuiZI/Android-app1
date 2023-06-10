package com.example.myapplication.volunteerMessage

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.myapplication.databinding.ActivityVolunteerGroupBinding


class VolunteerGroup : AppCompatActivity() {

    private lateinit var binding: ActivityVolunteerGroupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVolunteerGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val myPagerAdapter = VolunteerGroupPagerAdapter(supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = myPagerAdapter
        //创建tab，因为MyPaperAdapter里的getCount方法，所以会自带三个tab，要删除
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)

        val tab1=tabs.newTab()
        tab1.text="已加入"
        tabs.addTab(tab1)

        val tab2=tabs.newTab()
        tab2.text="申请中"
        tabs.addTab(tab2)

        val tab3=tabs.newTab()
        tab3.text="被拒绝"
        tabs.addTab(tab3)

        //删除默认的三个个tab
        tabs.removeTabAt(0)
        tabs.removeTabAt(0)
        tabs.removeTabAt(0)
        //监听选项的点击事件
        tabs.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem= tab?.position!!
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
//                Toast.makeText(this@VolunteerGroup,"无效tab",Toast.LENGTH_SHORT).show()
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })
    }
    class VolunteerGroupPagerAdapter(fragmentManager: FragmentManager):FragmentPagerAdapter(fragmentManager){
        //返回fragment数量
        override fun getCount(): Int {
            return 3
        }
        //根据位置返回fragment
        override fun getItem(position: Int): Fragment {
            return when(position){
                0->VolunteerGroupJoinedFragment()
                1->VolunteerGroupApplyingFragment()
                2->VolunteerGroupRejectedFragment()
                else-> throw IllegalAccessException("无效位置: $position")
            }
        }

    }
}