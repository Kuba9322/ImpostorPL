package pl.kuce.impostorpl.storage

import pl.kuce.impostorpl.model.Word

/**
 * Persistent store for words.
 * This abstracts where words come from (resources, remote updates, local DB).
 *
 * Contract:
 * - IDs are stable for a given logical word.
 * - New words arrive with new IDs.
 * - Text changes keep the same ID.
 */
interface WordStore {
    /** Returns the full map of all known words: id -> Word. */
    fun getAllWords(): Map<String, Word>

    /**
     * Inserts or updates the given words.
     * - New IDs are added.
     * - Existing IDs update the text (keeping history intact).
     */
    fun upsert(words: List<Word>)

    /**
     * Removes words by ID.
     * - If an ID is still present in the deck's remaining tail, it should be pruned there by orchestrator.
     * - Already-used IDs can remain as historical facts.
     */
    fun removeById(ids: Set<String>)
}
