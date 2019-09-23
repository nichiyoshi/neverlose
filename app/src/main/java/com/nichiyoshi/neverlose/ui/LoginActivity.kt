package com.nichiyoshi.neverlose.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.evernote.client.android.EvernoteSession
import com.evernote.client.android.login.EvernoteLoginFragment
import com.nichiyoshi.neverlose.BuildConfig
import com.nichiyoshi.neverlose.R
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.content

class LoginActivity : AppCompatActivity(), EvernoteLoginFragment.ResultCallback {

    companion object{
        private const val EVERNOTE_CONSUMER_KEY= BuildConfig.EVERNOTE_CONSUMER_KEY
        private const val EVERNOTE_CONSUMER_SECRET=
            BuildConfig.EVERNOTE_CONSUMER_SECRET
        private val EVERNOTE_SERVICE = EvernoteSession.EvernoteService.PRODUCTION

        @JvmStatic
        fun createIntent(context: Context): Intent = Intent(context, LoginActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        EvernoteSession.Builder(this)
            .setEvernoteService(EVERNOTE_SERVICE)
            .build(
                EVERNOTE_CONSUMER_KEY,
                EVERNOTE_CONSUMER_SECRET
            )
            .asSingleton()

        EvernoteSession.getInstance().isLoggedIn.let { isLoggedIn ->

            if(isLoggedIn){
                startActivity(MainActivity.createIntent(this@LoginActivity))
            }
        }

        login.setOnClickListener {
            authenticate()
        }
    }

    private fun authenticate(){
        EvernoteSession.getInstance().authenticate(this@LoginActivity)
    }


    override fun onLoginFinished(successful: Boolean) {

        if(!successful){
            Snackbar.make(content, R.string.failed_to_login, Snackbar.LENGTH_LONG)
                .setAction(R.string.try_again
                ) { authenticate() }.show()
            return
        }

        startActivity(MainActivity.createIntent(this))
    }

}