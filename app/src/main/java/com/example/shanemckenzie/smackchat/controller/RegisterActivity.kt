package com.example.shanemckenzie.smackchat.controller

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.view.View
import android.widget.Toast
import com.example.shanemckenzie.smackchat.R
import com.example.shanemckenzie.smackchat.services.AuthService
import com.example.shanemckenzie.smackchat.utilities.BROADCAST_USER_DATA_CHANGE
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    var userAvatar = "profileDefault"
    var avatarColor = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        registerSpinner.visibility = View.INVISIBLE

    }

    fun registerAvatarImgOnClick(view: View) {
        // change avatar image
        val random = Random()
        val color = random.nextInt(2)
        val avatar = random.nextInt(28)

        if (color == 0) {
            userAvatar = "light$avatar"
        } else {
            userAvatar = "dark$avatar"
        }

        val resourceId = resources.getIdentifier(userAvatar, "drawable", packageName)
        registerAvatarImgView.setImageResource(resourceId)

    }

    fun genBgColorBtnOnClick(view: View) {
        // generate BG color for avatar
        val random = Random()
        val r = random.nextInt(255)
        val g = random.nextInt(255)
        val b = random.nextInt(255)

        registerAvatarImgView.setBackgroundColor(Color.rgb(r, g, b))

        val savedR = r.toDouble() / 255
        val savedG = g.toDouble() / 255
        val savedB = b.toDouble() / 255

        avatarColor = "[$savedR, $savedG, $savedB, 1]"

    }

    fun registerBtnOnClick(view: View) {
        // Register user
        enableRegisterSpinner(true)
        val userName = registerUsernameText.text.toString()
        val email = registerEmailText.text.toString()
        val password = registerPasswordText.text.toString()

        if(userName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
            AuthService.registerUser(this, email, password) { registerSuccess ->
                if(registerSuccess) {
                    AuthService.loginUser(this, email, password) { loginSuccess ->
                        if(loginSuccess) {
                            AuthService.createUser(this, userName, email,
                                    userAvatar, avatarColor) { createSuccess ->
                                if(createSuccess) {
                                    val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(userDataChange)
                                    enableRegisterSpinner(false)
                                    finish()
                                } else {
                                    errorToast()
                                }

                            }
                        } else {
                            errorToast()
                        }
                    }
                } else {
                    errorToast()
                }

            }
        } else {
            enableRegisterSpinner(false)
            Toast.makeText(this,
                    "Make sure username, email, and password fields are filled in",
                    Toast.LENGTH_LONG).show()
        }


    }

    fun errorToast() {
        enableRegisterSpinner(false)
        Toast.makeText(this, "Something went wrong, please try again.", Toast.LENGTH_LONG).show()
    }

    fun enableRegisterSpinner(enable: Boolean) {
        if (enable) {
            registerSpinner.visibility = View.VISIBLE
        } else {
            registerSpinner.visibility = View.INVISIBLE
        }

        registerBtn.isEnabled = !enable
        registerAvatarImgView.isEnabled = !enable
        generateBgColorBtn.isEnabled = !enable

    }

}
