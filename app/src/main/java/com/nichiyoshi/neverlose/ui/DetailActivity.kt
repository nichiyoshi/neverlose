package com.nichiyoshi.neverlose.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import com.evernote.android.intent.EvernoteIntent
import com.nichiyoshi.neverlose.R
import com.nichiyoshi.neverlose.domain.EVNoteResult
import com.nichiyoshi.neverlose.domain.UserResult
import com.nichiyoshi.neverlose.domain.convertToTime
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_detail.my_toolbar

class DetailActivity: AppCompatActivity() {

    companion object{

        const val KEY_ARTICLE = "KEY_ARTICLE"

        fun createIntent(context: Context, Success: EVNoteResult.Success): Intent {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(KEY_ARTICLE, Success)
            return intent
        }
    }

    private val viewModel: DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_detail)
        setSupportActionBar(my_toolbar)
        my_toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
        my_toolbar.setNavigationOnClickListener {
            this.finish()
        }
        val article = intent.getSerializableExtra(KEY_ARTICLE) as? EVNoteResult.Success

        article?.let{
            updated_date.text = String.format(resources.getString(R.string.updated_date, it.updated.convertToTime()))
            article_title.text = it.title
            webview.loadData(it.content, "text/html", "UTF-8")
            button.setOnClickListener {view ->
                navigateToEvernote(it.guid)
            }
        }

        viewModel.evernoteUserResult.observe(this){
            when(it){
                is UserResult.Success -> {
                    article?.apply {
                        val url = viewModel.getEvernoteUserUrlWithGUID(article.guid, it.user)
                        val intent = Intent(Intent.ACTION_VIEW, url)
                        startActivity(intent)
                    }?: Log.e(this::class.java.simpleName, "failed to get article")
                    progress_bar.hide()
                }
                is UserResult.Fail -> {
                    progress_bar.hide()
                    Snackbar.make(container, R.string.failed_to_open_evernote, Snackbar.LENGTH_SHORT).show()
                }
                is UserResult.IsLoading -> {
                    progress_bar.show()
                }
            }
        }
    }

    private fun navigateToEvernote(guid: String){
        val intent = EvernoteIntent.viewNote().setNoteGuid(guid).create()
        if(intent.resolveActivity(packageManager) != null){
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }else{
            viewModel.fetchUser()
        }
    }

}