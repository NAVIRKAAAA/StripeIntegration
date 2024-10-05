package com.app.stripeintegration.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class CustomerIdManager(
    context: Context
) {

    private val dataStore = context.dataStore

    fun get(): Flow<String?> {
        return dataStore.data.map { it[CUSTOMER_ID_KEY] }
    }

    suspend fun set(id: String) {
        dataStore.edit { it[CUSTOMER_ID_KEY] = id }
    }

    companion object {
        private val CUSTOMER_ID_KEY = stringPreferencesKey("customer_id_key")
    }

}