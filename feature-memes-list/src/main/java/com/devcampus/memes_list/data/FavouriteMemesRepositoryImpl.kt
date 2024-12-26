package com.devcampus.memes_list.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.devcampus.memes_list.domain.FavouriteMemesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class FavouriteMemesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : FavouriteMemesRepository {

    private val favouritesKey = stringPreferencesKey("favourites")

    override fun getFavouriteMemes(): Flow<List<String>> {
        return dataStore.data.map { prefs ->
            prefs[favouritesKey]?.let {
                Json.decodeFromString<List<String>>(it)
            } ?: emptyList()
        }
    }

    override suspend fun setFavourite(memeFilePath: String, isFavourite: Boolean) {
        dataStore.edit { prefs ->

            val favourites = prefs[favouritesKey]?.let { Json.decodeFromString<List<String>>(it) }
                ?: emptyList()

            with (favourites.toMutableSet()) {
                if (isFavourite) {
                    add(memeFilePath)
                } else {
                    remove(memeFilePath)
                }

                prefs[favouritesKey] = Json.encodeToString(this)
            }
        }
    }
}
