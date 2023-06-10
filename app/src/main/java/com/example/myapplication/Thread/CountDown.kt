package com.example.myapplication.Thread

import android.os.Handler
import android.os.Message

class CountDown(var handler: Handler,var num:Int):Thread() {
    var flag=true
    override fun run() {
        var i=60
        while (flag){
            //封装信息
            val msg=Message()
            msg.obj=i
            msg.what=num
            //发送信息
            handler.sendMessage(msg)
            sleep(1000)
            i--
        }
    }
    fun stopTime(){
        flag=false
    }
}