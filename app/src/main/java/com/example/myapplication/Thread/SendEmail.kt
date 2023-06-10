package com.example.myapplication.Thread

import com.sun.mail.util.MailSSLSocketFactory
import java.util.*
import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class SendEmail(val title:String,val content:String,val toEmail:String) :Thread(){
    override fun run() {
        val properties = Properties()
        properties.setProperty("mail.debug", "true")
        properties.setProperty("mail.smtp.auth", "true")
        properties.setProperty("mail.host", "smtp.qq.com")
        properties.setProperty("mail.transport.protocol", "smtp")
        val socketFactory = MailSSLSocketFactory()
        socketFactory.setTrustAllHosts(true)
        properties.put("mail.smtp.ssl.enable", "true")
        properties.put("mail.smtp.ssl.socketFactory", socketFactory)
        val session: Session = Session.getInstance(properties)
        val msg: Message = MimeMessage(session)
        msg.setSubject(title)
        val builder = StringBuilder()
        builder.append(content)

        msg.setText(builder.toString())
        //发出邮箱
        //发出邮箱
        msg.setFrom(InternetAddress(""))

        val transport: Transport = session.getTransport()
        transport.connect("smtp.qq.com", "", "")
        //internetaddress单个发送直接写邮箱，群发写多个邮箱，中间用逗号（英文）隔开
        //发送的信息和目标邮箱
        transport.sendMessage(msg, InternetAddress.parse(toEmail))

        transport.close()
    }
}