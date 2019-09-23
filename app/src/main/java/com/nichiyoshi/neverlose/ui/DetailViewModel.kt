package com.nichiyoshi.neverlose.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.evernote.client.android.EvernoteSession
import com.evernote.client.android.asyncclient.EvernoteCallback
import com.evernote.edam.type.User
import com.nichiyoshi.neverlose.domain.EvernoteUser
import com.nichiyoshi.neverlose.domain.UserResult
import com.nichiyoshi.neverlose.domain.toEvernoteUser
import com.nichiyoshi.neverlose.ui.util.SingleLiveEvent
import java.lang.Exception

class DetailViewModel : ViewModel() {

    companion object{
        private const val service = "www.evernote.com"
    }

    private val evernoteUserImmutable = SingleLiveEvent<UserResult>()
    val evernoteUserResult : LiveData<UserResult> = evernoteUserImmutable

    fun fetchUser(){

        evernoteUserImmutable.postValue(UserResult.IsLoading)

        val userStoreClient = EvernoteSession.getInstance().evernoteClientFactory.userStoreClient
        userStoreClient.getUserAsync(object : EvernoteCallback<User> {

            override fun onSuccess(result: User?) {
                result?.toEvernoteUser()?.apply {
                    evernoteUserImmutable.postValue(UserResult.Success(this))
                }?:evernoteUserImmutable.postValue(UserResult.Fail(Exception("user is null")))
            }

            override fun onException(exception: Exception?) {
                exception?.apply {
                    evernoteUserImmutable.postValue(UserResult.Fail(this))
                }?:evernoteUserImmutable.postValue(UserResult.Fail(Exception("unknown exception")))

            }
        })
    }

    fun getEvernoteUserUrlWithGUID(guid: String, user: EvernoteUser) : Uri =
        Uri.parse("https://${service}/shard/${user.shardId}/nl/${user.id}/${guid}/")

}