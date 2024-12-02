package com.devcampus.memes_list.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.devcampus.memes_list.domain.SortModeRepository
import com.devcampus.memes_list.domain.model.SortMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class SortModeRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
): SortModeRepository {

    private val sortModeKey = stringPreferencesKey("sort_mode")

    override fun getSortMode(): Flow<SortMode> {
        return dataStore.data.map { prefs ->
            SortMode.valueOf(prefs[sortModeKey] ?: SortMode.NEWEST_FIRST.name)
        }
    }

    override suspend fun setSortMode(sortMode: SortMode) {
        dataStore.edit { prefs ->
            prefs[sortModeKey] = sortMode.name
        }
    }
}
