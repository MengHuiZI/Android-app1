package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_volunteerGroups, R.id.navigation_volunteerProjects,R.id.navigation_myMessage
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val intentMyMessage=intent.getIntExtra("fragment",0)
        if (intentMyMessage==R.id.navigation_myMessage){
            selectBottomNavigationItem(intentMyMessage)
        }
    }
    //给home片段中的“加入项目”和“加入团体”使用的片段切换
    fun selectBottomNavigationItem(itemId: Int) {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navController.navigate(itemId)
        val navView: BottomNavigationView = binding.navView
        //导航栏事件监听
        navView.setOnItemSelectedListener {menuItem->
            when(menuItem.itemId){
                R.id.navigation_home->{
                    //返回根片段
                    navController.popBackStack(R.id.navigation_home,false)
                }
                R.id.navigation_volunteerProjects->{
                    navController.navigate(R.id.navigation_volunteerProjects)
                    true
                }
                R.id.navigation_volunteerGroups->{
                    navController.navigate(R.id.navigation_volunteerGroups)
                    true
                }
                R.id.navigation_myMessage->{
                    navController.navigate(R.id.navigation_myMessage)
                    true
                }
                else -> false
            }
        }
    }
}