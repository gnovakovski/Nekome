package com.chesire.nekome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.chesire.nekome.account.UserRepository
import com.chesire.nekome.core.IOContext
import com.chesire.nekome.kitsu.AuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * [ViewModel] scoped to the [Activity].
 */
class ActivityViewModel @Inject constructor(
    private val authProvider: AuthProvider,
    private val logoutHandler: LogoutHandler,
    @IOContext private val ioContext: CoroutineContext,
    userRepository: UserRepository
) : ViewModel() {

    /**
     * The currently logged in user.
     */
    val user = userRepository.user.asLiveData()

    /**
     * Checks if the user is currently logged in.
     */
    val userLoggedIn: Boolean
        get() = authProvider.accessToken.isNotEmpty()

    /**
     * Logs the user out and returns the user back to entering the login details. [callback] is
     * executed after the [LogoutHandler] has finished clearing its data.
     */
    fun logout(callback: () -> Unit) = viewModelScope.launch {
        withContext(ioContext) {
            logoutHandler.executeLogout()
        }

        Timber.w("Logout complete, firing callback")
        callback()
    }
}
