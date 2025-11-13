package pl.kuce.impostorpl.loader

import android.content.Context
import pl.kuce.impostorpl.model.Word
import pl.kuce.impostorpl.util.stableWordId

/**
 * Loads words from a raw resource file where each non-empty line is a word.
 * - Trims lines
 * - Skips blank lines
 * - Generates a stable ID for each word using SHA-256 of normalized text
 */
object ResourceWordLoader {
    fun load(context: Context, resId: Int): List<Word> {
        val lines = context.resources.openRawResource(resId)
            .bufferedReader()
            .readLines()

        return lines
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { text -> Word(id = stableWordId(text), text = text) }
    }
}
