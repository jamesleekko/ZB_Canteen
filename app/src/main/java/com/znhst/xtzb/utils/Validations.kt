package com.znhst.xtzb.utils

import android.content.Context
import android.widget.Toast

object ValidationUtils {

    private fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun validUserName(context: Context,username: String): Boolean {
        val usernameRegex = "^[a-zA-Z0-9_-]{3,16}$".toRegex()
        if(username.isBlank()){
            showToast(context, "用户名不能为空")
            return false
        }
        if(!username.matches(usernameRegex)){
            showToast(context, "用户名为3-16位数字、英文、减号及下划线组成")
            return false
        }
        return true
    }

    fun validPassword(context: Context, password: String): Boolean {
        val passwordRegex = "^[a-zA-Z0-9_-]{6,16}$".toRegex()
        if(password.isBlank()){
            showToast(context, "密码不能为空")
            return false
        }
        if(!password.matches(passwordRegex)){
            showToast(context, "密码为6-16位数字、英文、减号及下划线组成")
            return false
        }
        return true
    }

    fun doPasswordsMatch(context: Context,password: String?, confirmPassword: String?): Boolean {
        if(password != confirmPassword){
            showToast(context, "两次密码输入不一致")
            return false
        }
        return true
    }

    fun validPhone(context: Context,phone: String?, nullable: Boolean = false): Boolean {
        val phoneRegex = "^1[3-9]\\d{9}$".toRegex() // 中国大陆手机号码格式
        if(nullable && phone.isNullOrBlank()){
            showToast(context, "手机号不能为空")
            return false
        }
        if (phone != null) {
            if(!phone.matches(phoneRegex)){
                showToast(context, "手机号不符合格式")
                return false
            }
        }
        return true
    }

    fun isEmailValid(context: Context, email: String?, nullable: Boolean = false): Boolean {
        if(!nullable && email.isNullOrBlank()){
            showToast(context, "邮箱不能为空")
            return false
        }
        if (email != null) {
            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                showToast(context, "邮箱不符合格式")
                return false
            }
        }
        return true
    }
}