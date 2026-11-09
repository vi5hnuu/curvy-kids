package com.vi5hnu.curvykids.billing

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID

private val Context.proStore by preferencesDataStore("curvy_pro")

/**
 * The device's memory of Pro, so a child who opens the app offline still has what was bought.
 * Play's own answer always wins when it can be asked: a refund clears this on the next launch.
 */
class ProStore(context: Context) {

    private val store = context.applicationContext.proStore

    val owned = store.data.map { it[OWNED] ?: false }

    suspend fun setOwned(owned: Boolean) {
        store.edit { it[OWNED] = owned }
    }

    /**
     * A random id for this install, sent with purchases (as Play's obfuscated account id) so Play
     * can spot one account buying on many devices in a short time. Says nothing about the child.
     */
    suspend fun accountId(): String {
        store.data.first()[ACCOUNT_ID]?.let { return it }
        val id = UUID.randomUUID().toString()
        store.edit { it[ACCOUNT_ID] = id }
        return id
    }

    private companion object {
        val OWNED = booleanPreferencesKey("owned")
        val ACCOUNT_ID = stringPreferencesKey("account_id")
    }
}
