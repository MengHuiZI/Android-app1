package com.example.myapplication.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.GroupRegister
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.VolunteerRegister
import com.example.myapplication.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding?=null

    // This property is only valid between onCreateView and
    // onDestroyView.

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentHomeBinding.inflate(layoutInflater)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.vRegister.setOnClickListener {
            startActivity(Intent(requireContext(),VolunteerRegister::class.java))
        }
        binding!!.gRegister.setOnClickListener {
            startActivity(Intent(requireContext(),GroupRegister::class.java))
        }
        binding!!.joinProject.setOnClickListener {
            selectBottomNavigationItem(R.id.navigation_volunteerProjects)
        }
        binding!!.joinGroup.setOnClickListener {
            selectBottomNavigationItem(R.id.navigation_volunteerGroups)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    //跳转到MainActivity执行selectBottomNavigationItem方法实现片段切换
    fun selectBottomNavigationItem(itemId:Int){
        val mainActivity=requireActivity() as MainActivity
        mainActivity.selectBottomNavigationItem(itemId)
    }
}