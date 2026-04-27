package com.example.fifa2026poolpredictions.auth

import android.content.Context
import android.util.Base64
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.example.fifa2026poolpredictions.BuildConfig
import com.example.fifa2026poolpredictions.data.local.TokenStore
import com.example.fifa2026poolpredictions.data.model.JwtPayload
import com.example.fifa2026poolpredictions.data.network.json
import com.example.fifa2026poolpredictions.data.repository.Fifa2026Repository
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthManager(
    private val tokenStore: TokenStore,
    private val repository: Fifa2026Repository
) {
    val authState: Flow<AuthState> = tokenStore.token.map { token ->
        if (token == null) AuthState.LoggedOut
        else parseToken(token) ?: AuthState.LoggedOut
    }

    private fun parseToken(token: String): AuthState.LoggedIn? {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return null
            val padded = parts[1].padEnd((parts[1].length + 3) / 4 * 4, '=')
            val decoded = Base64.decode(padded, Base64.URL_SAFE or Base64.NO_WRAP)
            val payload = json.decodeFromString<JwtPayload>(String(decoded))
            if (payload.id.isEmpty()) null
            else AuthState.LoggedIn(
                userId = payload.id,
                role = payload.role,
                name = payload.name,
                email = payload.email,
                image = payload.image
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun signIn(context: Context): Result<Unit> {
        val credentialManager = CredentialManager.create(context)
        val webClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID

        // Try authorized accounts first
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(webClientId)
            .setAutoSelectEnabled(true)
            .build()

        val idToken = try {
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            extractIdToken(credentialManager.getCredential(context, request))
        } catch (e: NoCredentialException) {
            // No authorized account — show full account picker
            val signInOption = GetSignInWithGoogleOption.Builder(webClientId).build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInOption)
                .build()
            try {
                extractIdToken(credentialManager.getCredential(context, request))
            } catch (e2: GetCredentialCancellationException) {
                return Result.failure(Exception("Sign-in cancelled"))
            } catch (e2: Exception) {
                return Result.failure(e2)
            }
        } catch (e: GetCredentialCancellationException) {
            return Result.failure(Exception("Sign-in cancelled"))
        } catch (e: Exception) {
            return Result.failure(e)
        } ?: return Result.failure(Exception("Could not retrieve Google ID token"))

        return repository.mobileAuth(idToken).fold(
            onSuccess = { response ->
                tokenStore.saveToken(response.token)
                Result.success(Unit)
            },
            onFailure = { Result.failure(it) }
        )
    }

    suspend fun signOut(context: Context) {
        tokenStore.clearToken()
        CredentialManager.create(context).clearCredentialState(
            androidx.credentials.ClearCredentialStateRequest()
        )
    }

    private fun extractIdToken(result: androidx.credentials.GetCredentialResponse): String? {
        val credential = result.credential
        return if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            GoogleIdTokenCredential.createFrom(credential.data).idToken
        } else null
    }
}
