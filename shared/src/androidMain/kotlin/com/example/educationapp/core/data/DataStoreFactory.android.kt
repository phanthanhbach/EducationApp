package com.example.educationapp.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

/**
 * Android implementation of DataStore creation.
 * Sử dụng createWithPath để đồng bộ với Path của KMP.
 */
fun createAndroidDataStore(context: Context): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { context.filesDir.resolve(DATASTORE_FILE_NAME).absolutePath.toPath() }
    )

