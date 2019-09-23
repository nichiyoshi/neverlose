package com.nichiyoshi.neverlose.domain

import com.evernote.edam.error.EDAMErrorCode
import com.evernote.edam.error.EDAMSystemException
import com.evernote.edam.error.EDAMUserException
import com.evernote.edam.notestore.NoteFilter
import com.nichiyoshi.neverlose.R
import java.io.Serializable
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

data class EVNoteFilter(val index: Int, val daysAgo: Int): NoteFilter(), Serializable{

    init {
        this.words = when(daysAgo){
            0 -> "updated:day"
            else -> "updated:day-${daysAgo} -updated:day-${daysAgo-1}"
        }

        this.timeZone = "Japan"
    }

}

fun EVNoteFilter.toPageTitle(): PageTitle =  when(this.daysAgo){
    0 -> PageTitle.Today
    1 -> PageTitle.Yesterday
    else -> PageTitle.DaysAgo(this.daysAgo)
}

sealed class PageTitle{
    object Today: PageTitle(){
        const val stringId = R.string.today
    }

    object Yesterday: PageTitle(){
        const val stringId = R.string.yesterday
    }

    data class DaysAgo(val daysAgo: Int): PageTitle(){
        val stringId = R.string.daysago
    }
}

sealed class EVNoteResult: Serializable{

    data class Success(val updated: Long, val title: String, val content: String, val filter: EVNoteFilter, val guid: String): EVNoteResult(),
        Comparable<Success>{
        override fun compareTo(other: Success): Int {
            if(this.updated > other.updated ) return -1
            if(this.updated < other.updated ) return 1
            return 0
        }
    }
    object Empty: EVNoteResult()
    object IsLoading: EVNoteResult()
    data class ExceptionLoadingHTML(val exception: Exception?, val title: String): EVNoteResult(){
        val error = exception?.toFetchNoteError()
    }
    data class ExceptionFetchingNotes(val exception: Exception?): EVNoteResult(){
        val error = exception?.toFetchNoteError()
    }
}

sealed class FetchNoteError{

    data class AuthExpiredError(val error: EDAMUserException): FetchNoteError()
    data class RateLimitReachedError(val error: EDAMSystemException): FetchNoteError(){
        private val limit = error.rateLimitDuration
        val message = "please retry in $limit seconds"
    }
    data class OtherError(val error: Exception): FetchNoteError()
}

fun Exception.toFetchNoteError(): FetchNoteError {
    return if(this is EDAMUserException && this.errorCode == EDAMErrorCode.AUTH_EXPIRED){
        FetchNoteError.AuthExpiredError(this)
    }else if(this is EDAMSystemException && this.errorCode == EDAMErrorCode.RATE_LIMIT_REACHED){
        FetchNoteError.RateLimitReachedError(this)
    }else{
        FetchNoteError.OtherError(this)
    }
}

fun Long.convertToTime(): String {
    val date = Date(this)
    val format = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.JAPAN)
    return format.format(date)
}