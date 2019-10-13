package com.nichiyoshi.neverlose.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nichiyoshi.neverlose.R
import com.nichiyoshi.neverlose.domain.FetchNoteError
import com.nichiyoshi.neverlose.domain.EVNoteFilter
import com.nichiyoshi.neverlose.domain.EVNoteResult
import com.google.android.material.snackbar.Snackbar
import com.nichiyoshi.neverlose.util.LogUtil
import kotlinx.android.synthetic.main.fragment_main.*
import java.lang.Exception

class ArticleFragment : Fragment() {

    companion object {

        const val KEY_FILTER = "KEY_FILTER"

        private val TAG = this::class.java.simpleName

        @JvmStatic
        fun newInstance(filter: EVNoteFilter): ArticleFragment {
            val args = Bundle()
            args.putSerializable(KEY_FILTER, filter)
            return ArticleFragment().apply {
                arguments = args
            }
        }
    }

    private lateinit var viewAdapter: MyAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var filter: EVNoteFilter

    private val fragmentViewModel: ArticleFragmentViewModel by viewModels()
    private val activityViewModel: MainViewModel by viewModels({requireActivity()})

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        filter = arguments?.getSerializable(KEY_FILTER) as? EVNoteFilter
            ?:throw Exception("could not get EVNoteFilter from arguments in ${this::class.java.simpleName}")

        fragmentViewModel.articles.observe(this){
            when(it){
                is EVNoteResult.Success -> {
                    showArticleView()
                    viewAdapter.setData(it)
                    progress_bar.hide()
                }
                is EVNoteResult.IsLoading -> {
                    progress_bar.show()
                }
                is EVNoteResult.ExceptionFetchingNotes -> {

                    showErrorView()

                    when(it.error){
                        is FetchNoteError.AuthExpiredError -> {
                            startActivity(LoginActivity.createIntent(view?.context?:return@observe))
                            Snackbar.make(view?:return@observe, R.string.authentication_required, Snackbar.LENGTH_SHORT).show()
                        }
                        is FetchNoteError.RateLimitReachedError -> {
                            LogUtil.e(TAG, it.error.message)
                            Snackbar.make(view?:return@observe, it.error.message, Snackbar.LENGTH_LONG).show()
                        }
                        is FetchNoteError.OtherError -> {
                            LogUtil.e(TAG, it.error.error.message)
                            Snackbar.make(view?:return@observe, R.string.failed_to_fetch_notes, Snackbar.LENGTH_SHORT).show()
                        }
                    }
                    progress_bar.hide()
                }
                is EVNoteResult.ExceptionLoadingHTML -> {

                    showErrorView()

                    when(it.error){
                        is FetchNoteError.AuthExpiredError -> {
                            startActivity(LoginActivity.createIntent(view?.context?:return@observe))
                            Snackbar.make(view?:return@observe, R.string.authentication_required, Snackbar.LENGTH_SHORT).show()
                        }
                        is FetchNoteError.RateLimitReachedError -> {
                            LogUtil.e(TAG, it.error.message)
                            Snackbar.make(view?:return@observe, it.error.message, Snackbar.LENGTH_LONG).show()
                        }
                        is FetchNoteError.OtherError -> {
                            LogUtil.e(TAG, it.error.error.message)
                            Snackbar.make(view?:return@observe, R.string.failed_to_fetch_notes, Snackbar.LENGTH_SHORT).show()
                        }
                    }
                    progress_bar.hide()
                }
                is EVNoteResult.Empty -> {
                    showEmptyView()
                    progress_bar.hide()
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewManager = LinearLayoutManager(view.context)
        viewAdapter = MyAdapter(object :
            MyAdapter.OnClickArticleListener {
            override fun onClick(Success: EVNoteResult.Success) {
                DetailActivity.createIntent(view.context, Success).apply {
                    startActivity(this)
                }
            }
        })

        my_recycler_view.apply {
            adapter = viewAdapter
            layoutManager = viewManager
        }

        fab.setOnClickListener {
            getArticlesAndSize()
        }


        getArticlesAndSize()
    }

    private fun getArticlesAndSize(){

        viewAdapter.clearData()

        // this is to refresh contents
        fragmentViewModel.getArticles(filter)

        // this is to refresh tab batch corresponding to this fragment
        activityViewModel.getNoteCountWithFilter(filter)
    }

    private fun showEmptyView(){
        my_recycler_view.isVisible = false
        error_view.isVisible = false
        empty_view.isVisible = true
    }

    private fun showErrorView(){
        my_recycler_view.isVisible = false
        error_view.isVisible = true
        empty_view.isVisible = false
    }

    private fun showArticleView(){
        my_recycler_view.isVisible = true
        error_view.isVisible = false
        empty_view.isVisible = false
    }
}