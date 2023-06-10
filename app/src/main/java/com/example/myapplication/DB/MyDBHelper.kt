package com.example.myapplication.DB

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDBHelper(val context:Context):SQLiteOpenHelper(context,"VolunteerDB",null,1) {
    override fun onCreate(db: SQLiteDatabase?) {
        //志愿者表
        val createUserTable="create table `user`(id integer primary key autoincrement," +
                "account text," +
                "password text," +
                "email text," +
                "name text," +
                "sex text," +
                "birth_date text," +
                "phone_number text," +
                "head_portrait BLOB," +
                "service_time text default '0')"
        //团体表
        val createGroupTable="create table `group`(id integer primary key autoincrement," +
                "g_account text," +
                "g_password text," +
                "g_email text," +
                "g_name text," +
                "g_address text," +
                "principal_name text," +
                "principal_phone text," +
                "register_date text," +
                "image BLOB)"
        //项目表
        val createProject="create table `project`(id integer primary key autoincrement," +
                "p_name text," +
                "p_address text," +
                "p_start_time text," +
                "p_end_time text," +
                "p_service_time text," +
                "g_id text)"
        //志愿者-团体表
        val createVolunteerAndGroup="create table `volunteer_group`(id integer primary key autoincrement," +
                "v_id text," +
                "g_id text," +
                "v_g_apply_date text,"+
                "state text)"
        //志愿者-项目表
        val createVolunteerAndProject="create table `volunteer_project`(id integer primary key autoincrement," +
                "v_id text," +
                "p_id text," +
                "v_p_apply_date text,"+
                "state text)"
//        //项目-团体表
//        val createProjectAndGroup="create table `project_group`(id integer primary key autoincrement," +
//                "p_id text," +
//                "g_id text," +
//                "state text)"
        //志愿者-项目-时长表
        val createVolunteerProjectTime="create table `volunteer_project_time`(id integer primary key autoincrement," +
                "v_id text," +
                "p_id text," +
                "apply_times text," +
                "project_introduce text," +
                "apply_date text," +
                "state text)"
        db?.execSQL(createUserTable)
        db?.execSQL(createGroupTable)
        db?.execSQL(createProject)
        db?.execSQL(createVolunteerAndGroup)
        db?.execSQL(createVolunteerAndProject)
//        db?.execSQL(createProjectAndGroup)
        db?.execSQL(createVolunteerProjectTime)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

}