package com.nanit.localization.server.data.database

import arrow.core.Either
import com.nanit.localization.server.domain.model.IncomingTranslation
import com.nanit.localization.server.domain.model.PluralQuantity
import com.nanit.localization.server.domain.model.StringResource
import com.nanit.localization.server.domain.model.UpdateTranslationModel
import com.nanit.localization.server.domain.repository.TranslationsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

/**
 * Repository for managing localization string resources in the database
 */
class LocalizationRepositoryV2(
    private val queries: LocalizationDatabaseQueries
) : TranslationsRepository {

    // ==================== String Value Operations ====================

    /**
     * Insert a new string value resource
     */
    suspend fun insertStringValue(resource: StringResource.Value) =
        withContext(Dispatchers.Default) {
            val creation = Clock.System.now().toEpochMilliseconds()
            queries.insertStringValue(
                key = resource.key,
                value_ = resource.value,
                locale = resource.locale,
                description = resource.description,
                created_at = creation,
                updated_at = creation
            )
        }

    override suspend fun getAllValuesBy(
        locale: String
    ): Either<Throwable, List<StringResource.Value>> = withContext(Dispatchers.Default) {
        Either
            .catch { queries.getAllStringValues(locale).executeAsList() }
            .map(List<StringValue>::asModel)
    }

    /**
     * Update an existing string value resource
     */
    override suspend fun updateStringValue(
        model: UpdateTranslationModel,
    ): Either<Throwable, Unit> = withContext(Dispatchers.Default) {
        val (locale, key, value) = model
        Either.catch {
            queries.updateStringValue(
                locale = locale,
                key = key,
                value_ = value,
                updated_at = Clock.System.now().toEpochMilliseconds()
            )
        }
    }

    /**
     * Get a string value by key and locale
     */
    suspend fun getStringValue(key: String, locale: String = "en"): StringResource.Value? =
        withContext(Dispatchers.Default) {
            queries.getStringValue(key, locale).executeAsOneOrNull()?.let {
                StringResource.Value(
                    key = it.key,
                    value = it.value_,
                    locale = it.locale,
                    description = it.description
                )
            }
        }

    /**
     * Delete a string value
     */
    suspend fun deleteStringValue(key: String, locale: String = "en") =
        withContext(Dispatchers.Default) {
            queries.deleteStringValue(key, locale)
        }

    // ==================== String Array Operations ====================

    /**
     * Insert a new string array resource
     */
    suspend fun insertStringArray(resource: StringResource.Array) =
        withContext(Dispatchers.Default) {
            queries.transaction {
                val creation = Clock.System.now().toEpochMilliseconds()
                queries.insertStringArray(
                    key = resource.key,
                    locale = resource.locale,
                    description = resource.description,
                    created_at = creation,
                    updated_at = creation
                )

                val arrayId = queries.getStringArray(resource.key, resource.locale)
                    .executeAsOne().id

                resource.items.forEachIndexed { index, item ->
                    queries.insertStringArrayItem(
                        array_id = arrayId,
                        value_ = item,
                        position = index.toLong()
                    )
                }
            }
        }

    /**
     * Get a string array by key and locale
     */
    suspend fun getStringArray(key: String, locale: String = "en"): StringResource.Array? =
        withContext(Dispatchers.Default) {
            val array = queries.getStringArray(key, locale).executeAsOneOrNull()
                ?: return@withContext null

            val items = queries.getStringArrayItems(array.id).executeAsList()
                .sortedBy { it.position }
                .map { it.value_ }

            StringResource.Array(
                key = array.key,
                items = items,
                locale = array.locale,
                description = array.description
            )
        }

    /**
     * Delete a string array
     */
    suspend fun deleteStringArray(key: String, locale: String = "en") =
        withContext(Dispatchers.Default) {
            queries.deleteStringArray(key, locale)
        }

    // ==================== String Plural Operations ====================

    /**
     * Insert a new string plural resource
     */
    suspend fun insertStringPlural(resource: StringResource.Plural) =
        withContext(Dispatchers.Default) {
            queries.transaction {
                val creation = Clock.System.now().toEpochMilliseconds()
                queries.insertStringPlural(
                    key = resource.key,
                    locale = resource.locale,
                    description = resource.description,
                    created_at = creation,
                    updated_at = creation
                )

                val pluralId = queries.getStringPlural(resource.key, resource.locale)
                    .executeAsOne().id

                resource.quantities.forEach { (quantity, value) ->
                    queries.insertStringPluralQuantity(
                        plural_id = pluralId,
                        quantity = quantity.value,
                        value_ = value
                    )
                }
            }
        }

    /**
     * Get a string plural by key and locale
     */
    suspend fun getStringPlural(key: String, locale: String = "en"): StringResource.Plural? =
        withContext(Dispatchers.Default) {
            val plural = queries.getStringPlural(key, locale).executeAsOneOrNull()
                ?: return@withContext null

            val quantities = queries.getStringPluralQuantities(plural.id).executeAsList()
                .associate { PluralQuantity.fromString(it.quantity) to it.value_ }

            StringResource.Plural(
                key = plural.key,
                quantities = quantities,
                locale = plural.locale,
                description = plural.description
            )
        }

    /**
     * Delete a string plural
     */
    suspend fun deleteStringPlural(key: String, locale: String = "en") =
        withContext(Dispatchers.Default) {
            queries.deleteStringPlural(key, locale)
        }

    override suspend fun insert(vararg value: StringResource.Value): Either<Throwable, Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun insert(
        model: IncomingTranslation
    ): Either<Throwable, List<StringResource.Value>> = withContext(Dispatchers.Default) {
        Either.catch {
            val creation = Clock.System.now().toEpochMilliseconds()
            model.values
                .asSequence()
                .map { translation ->
                    StringResource.Value(
                        key = model.key,
                        value = translation.value,
                        locale = translation.locale,
                        description = model.description
                    )
                }
                .onEach { resource ->
                    with(resource) {
                        queries.insertOrReplaceStringValue(
                            key = key,
                            value_ = value,
                            locale = locale,
                            description = description,
                            created_at = creation,
                            updated_at = creation
                        )
                    }
                }
                .toList()
        }
    }
}

fun List<StringValue>.asModel(): List<StringResource.Value> = map(StringValue::asModel)

fun StringValue.asModel(): StringResource.Value = StringResource.Value(
    key = key,
    value = value_,
    locale = locale,
    description = description
)
