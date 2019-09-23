package com.nichiyoshi.neverlose.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.evernote.client.android.EvernoteSession
import com.evernote.client.android.asyncclient.EvernoteCallback
import com.evernote.edam.error.EDAMErrorCode
import com.evernote.edam.error.EDAMUserException
import com.evernote.edam.notestore.NoteList
import com.nichiyoshi.neverlose.domain.EVNoteFilter
import com.nichiyoshi.neverlose.domain.NoteCount
import java.lang.Exception

class MainViewModel: ViewModel() {

    fun logout(){
        EvernoteSession.getInstance().logOut()
    }

    val tag: String = this::class.java.simpleName

    private val noteStoreClient = EvernoteSession.getInstance().evernoteClientFactory.noteStoreClient

    val noteFilters = arrayListOf<EVNoteFilter>().apply {
        add(EVNoteFilter(0, 0))
        add(EVNoteFilter(1, 1))
        add(EVNoteFilter(2, 8))
        add(EVNoteFilter(3, 22))
        add(EVNoteFilter(4, 52))
    }

    private val mutableNoteCount: MutableLiveData<NoteCount> = MutableLiveData()
    val noteCount : LiveData<NoteCount> = mutableNoteCount

    fun fetchAllNotesCount(){
        noteFilters.forEach {
            getNoteCountWithFilter(it)
        }
    }

    fun getNoteCountWithFilter(filter: EVNoteFilter){

        noteStoreClient.findNotesAsync(filter, 0, 50, object: EvernoteCallback<NoteList> {
            override fun onSuccess(notes: NoteList?) {
                Log.d(tag, "success, and result count is ${notes?.notesSize}")

                notes?.notesSize?.apply {
                    mutableNoteCount.postValue(NoteCount.Success(this, filter))
                }
            }

            override fun onException(exception: Exception?) {
                Log.e(tag, "failed to get noteCount", exception)
                if(exception is EDAMUserException){
                    if(exception.errorCode == EDAMErrorCode.AUTH_EXPIRED){
                        Log.e(tag, "auth is expired", exception)
                        EvernoteSession.getInstance().logOut()
                    }
                }
                mutableNoteCount.postValue(NoteCount.Fail(exception?:return, filter))
            }
        })
    }


}