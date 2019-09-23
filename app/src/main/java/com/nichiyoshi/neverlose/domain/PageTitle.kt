package com.nichiyoshi.neverlose.domain

import java.lang.Exception

data class Batch(val count: Int){
    val isVisible = count > 0
}

sealed class NoteCount{

    data class Success(val size: Int, val filter: EVNoteFilter): NoteCount(){
        val hasArticles = size > 0
    }
    data class Fail(val exception: Exception, val filter: EVNoteFilter): NoteCount(){
        val error = exception.toFetchNoteError()
    }
}