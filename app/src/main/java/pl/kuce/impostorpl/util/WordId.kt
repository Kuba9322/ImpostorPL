package pl.kuce.impostorpl.util

import java.security.MessageDigest
import java.util.Locale

/**
 * Returns a stable ID for a given word text.
 * - Normalizes whitespace and case
 * - Uses SHA-256 to produce a deterministic hex string
 */
fun stableWordId(raw: String): String {
    val normalized = raw.trim().lowercase(Locale.ROOT).replace("\\s+".toRegex(), " ")
    val digest = MessageDigest.getInstance("SHA-256").digest(normalized.toByteArray(Charsets.UTF_8))
    // Convert to hex (shorten if you prefer; 64 hex chars is fine)
    val hex = buildString(digest.size * 2) {
        for (b in digest) append(((b.toInt() and 0xFF) + 0x100).toString(16).substring(1))
    }
    return hex
}
