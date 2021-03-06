package com.jintoga.animekyabin.ui.animes

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import com.jintoga.animekyabin.BuildConfig
import com.jintoga.animekyabin.auth.AuthManager
import com.jintoga.animekyabin.repository.Repository
import com.jintoga.animekyabin.repository.model.anime.Anime
import com.jintoga.animekyabin.repository.model.auth.ClientCredentials
import com.jintoga.animekyabin.repository.model.auth.ClientCredentialsRequest
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class AnimesViewModel @Inject constructor(private val repository: Repository,
                                          private val authManager: AuthManager)
    : ViewModel() {

    val isEmpty = ObservableBoolean(false)
    val isLoadError = ObservableBoolean(false)
    val snackbarMessage = MutableLiveData<String>()
    val isLoading = ObservableBoolean(true) //make default value true so progressBar can show up at first
    val animes = ObservableArrayList<Anime>()

    fun loadAnimes(forceUpdate: Boolean,
                   showLoadingUI: Boolean) {
        if (showLoadingUI) {
            isLoading.set(true)
        }
        if (forceUpdate) {
            getLoadAnimesObservable()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            {
                                animes.clear()
                                animes.addAll(it)
                                //If items are loaded (probably from DB)
                                //then prematurely set isLoading to false to hide ProgressBar
                                //Otherwise it will still be set to false onComplete later
                                if (animes.isNotEmpty()) {
                                    isLoading.set(false)
                                }
                            },
                            {
                                isLoading.set(false)
                                isLoadError.set(true)
                                snackbarMessage.value = it.localizedMessage
                            },
                            {
                                isLoading.set(false)
                                with(animes) {
                                    isEmpty.set(animes.isEmpty())
                                }
                            }
                    )
        }
    }

    private fun getLoadAnimesObservable(): Observable<List<Anime>> {
        if (authManager.isTokenActive) return loadAnimesObservable()
        else return grantClientCredentials().flatMap { return@flatMap loadAnimesObservable() }
    }

    private fun loadAnimesObservable(): Observable<List<Anime>> {
        authManager.setTokenToInterceptor()
        return repository.getAnimes()
    }

    private fun grantClientCredentials(): Observable<ClientCredentials> =
            authManager.grantClientCredentials(ClientCredentialsRequest(
                    grantType = BuildConfig.GRANT_TYPE,
                    clientId = BuildConfig.CLIENT_ID,
                    clientSecret = BuildConfig.CLIENT_SERCRET
            ))
}