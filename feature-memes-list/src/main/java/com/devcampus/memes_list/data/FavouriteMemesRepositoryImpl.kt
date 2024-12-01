package com.devcampus.memes_list.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.devcampus.memes_list.domain.FavouriteMemesRepository
import com.devcampus.memes_list.domain.model.MemeFile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "exchange_rates")

@Singleton
internal class FavouriteMemesRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context
) : FavouriteMemesRepository {

    private val dataStore: DataStore<Preferences> = context.dataStore

    private val favouritesKey = stringPreferencesKey("favourites")

    override fun getFavouriteMemes(): Flow<Set<MemeFile>> {
        return dataStore.data.map { prefs ->
            val json = prefs[favouritesKey]
            json?.let {
                Json.decodeFromString<Set<MemeFile>>(it)
            } ?: emptySet()
        }
    }

    override suspend fun setFavourite(memeFile: MemeFile, isFavourite: Boolean) {
        dataStore.edit { prefs ->

            val favourites = prefs[favouritesKey]?.let { Json.decodeFromString<Set<MemeFile>>(it) }
                ?: emptySet()

            with (favourites.toMutableSet()) {
                if (isFavourite) {
                    add(memeFile)
                } else {
                    remove(memeFile)
                }

                prefs[favouritesKey] = Json.encodeToString(this.toSet())
            }
        }
    }
}
