package pl.kuce.impostorpl.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import pl.kuce.impostorpl.deck.DeckState

// DataStore delegate must be top-level and use a literal name.
private val Context.deckDataStore by preferencesDataStore(name = "deck_store")

class PersistentDeckStore(
    private val context: Context
) : DeckStore {

    private val KEY_STATE = stringPreferencesKey("deck_state_json")

    // Json instance configured for persistence.
    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    override fun load(): DeckState? = runBlocking(Dispatchers.IO) {
        val prefs = context.deckDataStore.data.first()
        val payload = prefs[KEY_STATE] ?: return@runBlocking null
        return@runBlocking try {
            // Use explicit serializer to avoid reified generic issues.
            json.decodeFromString(DeckState.serializer(), payload)
        } catch (_: Throwable) {
            null
        }
    }

    override fun save(state: DeckState) {
        runBlocking(Dispatchers.IO) {
            // Use explicit serializer to avoid reified generic issues.
            val payload = json.encodeToString(DeckState.serializer(), state)
            context.deckDataStore.edit { prefs ->
                prefs[KEY_STATE] = payload
            }
        }
    }
}
