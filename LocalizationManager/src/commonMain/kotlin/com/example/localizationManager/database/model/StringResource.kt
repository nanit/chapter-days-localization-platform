package com.example.localizationManager.database.model

/**
 * Sealed class representing different types of string resources
 */
sealed class StringResource {
    abstract val key: String
    abstract val locale: String

    /**
     * Simple string value resource
     */
    data class Value(
        override val key: String,
        val value: String,
        override val locale: String = "en",
        val description: String? = null
    ) : StringResource()

    /**
     * String array resource containing multiple items
     */
    data class Array(
        override val key: String,
        val items: List<String>,
        override val locale: String = "en",
        val description: String? = null
    ) : StringResource()

    /**
     * String plural resource with different forms based on quantity
     */
    data class Plural(
        override val key: String,
        val quantities: Map<PluralQuantity, String>,
        override val locale: String = "en",
        val description: String? = null
    ) : StringResource() {

        /**
         * Get the appropriate string for a given count
         */
        fun getForQuantity(count: Int): String {
            return when {
                count == 0 && quantities.containsKey(PluralQuantity.ZERO) ->
                    quantities[PluralQuantity.ZERO]!!
                count == 1 && quantities.containsKey(PluralQuantity.ONE) ->
                    quantities[PluralQuantity.ONE]!!
                count == 2 && quantities.containsKey(PluralQuantity.TWO) ->
                    quantities[PluralQuantity.TWO]!!
                quantities.containsKey(PluralQuantity.OTHER) ->
                    quantities[PluralQuantity.OTHER]!!
                else -> quantities.values.firstOrNull() ?: ""
            }
        }
    }
}

/**
 * Enum representing plural quantity types as per ICU standards
 */
enum class PluralQuantity(val value: String) {
    ZERO("zero"),
    ONE("one"),
    TWO("two"),
    FEW("few"),
    MANY("many"),
    OTHER("other");

    companion object {
        fun fromString(value: String): PluralQuantity {
            return entries.firstOrNull { it.value == value } ?: OTHER
        }
    }
}
