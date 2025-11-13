package pl.kuce.impostorpl.storage

import java.util.concurrent.ConcurrentHashMap
import pl.kuce.impostorpl.model.Word

/**
 * Simple in-memory WordStore.
 * - Thread-safe via ConcurrentHashMap.
 * - Suitable for tests and early wiring.
 * - Not persisted across app restarts.
 */
class InMemoryWordStore(
    initial: List<Word> = emptyList()
) : WordStore {

    private val map = ConcurrentHashMap<String, Word>().apply {
        initial.forEach { put(it.id, it) }
    }

    override fun getAllWords(): Map<String, Word> {
        // Return a copy to avoid external mutation.
        return HashMap(map)
    }

    override fun upsert(words: List<Word>) {
        if (words.isEmpty()) return
        words.forEach { w ->
            map[w.id] = w
        }
    }

    override fun removeById(ids: Set<String>) {
        if (ids.isEmpty()) return
        ids.forEach { id -> map.remove(id) }
    }
}
