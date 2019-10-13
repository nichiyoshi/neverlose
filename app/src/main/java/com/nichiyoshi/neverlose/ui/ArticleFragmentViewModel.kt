package com.nichiyoshi.neverlose.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.evernote.client.android.EvernoteSession
import com.evernote.client.android.asyncclient.EvernoteCallback
import com.evernote.client.android.asyncclient.EvernoteClientFactory
import com.evernote.edam.error.EDAMErrorCode
import com.evernote.edam.error.EDAMUserException
import com.evernote.edam.notestore.NoteList
import com.nichiyoshi.neverlose.domain.EVNoteFilter
import com.nichiyoshi.neverlose.domain.EVNoteResult
import com.nichiyoshi.neverlose.util.LogUtil
import com.squareup.okhttp.Response
import java.lang.Exception

class ArticleFragmentViewModel: ViewModel() {

    private val tag = this::class.java.simpleName

    private val everNoteHtmlHelper = EvernoteClientFactory.Builder(EvernoteSession.getInstance()).build().htmlHelperDefault

    private val mutableArticles: MutableLiveData<EVNoteResult> = MutableLiveData()
    val articles : LiveData<EVNoteResult> = mutableArticles

    fun getArticles(filter: EVNoteFilter){

        mutableArticles.postValue(EVNoteResult.IsLoading)

        val noteStoreClient = EvernoteSession.getInstance().evernoteClientFactory.noteStoreClient

        noteStoreClient.findNotesAsync(filter, 0, 50, object: EvernoteCallback<NoteList> {
            override fun onSuccess(notes: NoteList?) {
                LogUtil.d(tag, "success, and result count is ${notes?.notesSize}")

                notes?.notesSize?.apply {
                    if (this == 0) {
                        mutableArticles.postValue(EVNoteResult.Empty)
                        return@onSuccess
                    }
                }

                notes?.notes?.forEach{
                    val guid = it.guid
                    everNoteHtmlHelper.downloadNoteAsync(guid, object: EvernoteCallback<Response>{
                        override fun onSuccess(result: Response?) {
                            val contentHTML = String(result?.body()?.bytes()?: byteArrayOf())
                            mutableArticles.postValue(EVNoteResult.Success(it.updated, it.title, contentHTML , filter, guid))
                        }

                        override fun onException(exception: Exception?) {
                            if(exception is EDAMUserException){
                                if(exception.errorCode == EDAMErrorCode.AUTH_EXPIRED){
                                    LogUtil.e(tag, "auth is expired", exception)
                                    EvernoteSession.getInstance().logOut()
                                }
                            }
                            mutableArticles.postValue(EVNoteResult.ExceptionLoadingHTML(exception, it.title))
                        }
                    })
                }
            }

            override fun onException(exception: Exception?) {
                LogUtil.e(tag, "failed to get articles", exception)
                if(exception is EDAMUserException){
                    if(exception.errorCode == EDAMErrorCode.AUTH_EXPIRED){
                        LogUtil.e(tag, "auth is expired", exception)
                        EvernoteSession.getInstance().logOut()
                    }
                }
                mutableArticles.postValue(EVNoteResult.ExceptionFetchingNotes(exception))
            }
        })
    }

}