package com.nichiyoshi.neverlose.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import com.nichiyoshi.neverlose.R
import com.nichiyoshi.neverlose.domain.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drawer_layout.*


class MainActivity : AppCompatActivity() {

    companion object{
        @JvmStatic
        fun createIntent(context: Context): Intent = Intent(context, MainActivity::class.java)
    }

    val tag = this::class.java.simpleName

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(my_toolbar)

        val actionBarDrawerToggle = ActionBarDrawerToggle(this, drawer_layout, my_toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        )
        drawer_layout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigation_drawer.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.logout -> {
                    // TODO Cancel Token
                    viewModel.logout()
                    startActivity(LoginActivity.createIntent(this))
                }
            }
           false
        }

        val sectionsPagerAdapter = SectionsPagerAdapter(
            viewModel.noteFilters,
            supportFragmentManager
        )

        view_pager.adapter = sectionsPagerAdapter
        view_pager.offscreenPageLimit = 2
        tabs.titles = viewModel.noteFilters.map { it.toPageTitle() }.map {
            when(it){
                is PageTitle.Today -> {
                    String.format(resources.getString(it.stringId))
                }
                is PageTitle.Yesterday -> {
                    String.format(resources.getString(it.stringId))
                }
                is PageTitle.DaysAgo -> {
                    String.format(resources.getString(it.stringId), it.daysAgo)
                }
            }
        }
        tabs.setupWithViewPager(view_pager)

        viewModel.noteCount.observe(this){
            when(it){
                is NoteCount.Success -> {
                    Log.d(tag, "note count: ${it.size}, index: ${it.filter.index}")
                    tabs.updateBatch(Batch(it.size), it.filter.index)
                }
                is NoteCount.Fail -> {
                    when(it.error){
                        is FetchNoteError.AuthExpiredError -> {
                            startActivity(LoginActivity.createIntent(content?.context?:return@observe))
                            Snackbar.make(content?:return@observe, R.string.authentication_required, Snackbar.LENGTH_SHORT).show()
                        }
                        is FetchNoteError.RateLimitReachedError -> {
                            Log.e(tag, it.error.message)
                            Snackbar.make(content?:return@observe, it.error.message, Snackbar.LENGTH_LONG).show()
                        }
                        is FetchNoteError.OtherError -> {
                            Log.e(tag, it.error.error.message)
                            Snackbar.make(content?:return@observe, R.string.failed_to_fetch_notes, Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }


        viewModel.fetchAllNotesCount()
    }

}