package com.nichiyoshi.neverlose.domain

import com.evernote.edam.type.User
import java.lang.Exception

sealed class UserResult{

    data class Success(val user: EvernoteUser): UserResult()

    object IsLoading: UserResult()

    data class Fail(val error: Exception): UserResult()

}

data class EvernoteUser(val id: Int, val shardId: String)

fun User.toEvernoteUser(): EvernoteUser = EvernoteUser(this.id, this.shardId)