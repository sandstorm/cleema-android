package de.cleema.apps.cleema

/**
 * This is shared between :app and :benchmarks module to provide configurations type safety.
 */
@Suppress("unused")
enum class CleemaBuildType(val applicationIdSuffix: String? = null) {
    DEBUG(".debug"),
    RELEASE
}
