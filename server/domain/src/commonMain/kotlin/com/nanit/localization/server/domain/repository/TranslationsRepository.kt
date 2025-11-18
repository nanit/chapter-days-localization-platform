package com.nanit.localization.server.domain.repository

import arrow.core.Either
import com.nanit.localization.server.domain.model.IncomingTranslation
import com.nanit.localization.server.domain.model.StringResource
import com.nanit.localization.server.domain.model.UpdateTranslationModel

interface TranslationsRepository {
    suspend fun insert(vararg value: StringResource.Value): Either<Throwable, Unit>
    suspend fun insert(model: IncomingTranslation): Either<Throwable, List<StringResource.Value>>
    suspend fun getAllValuesBy(locale: String): Either<Throwable, List<StringResource.Value>>
    suspend fun updateStringValue(model: UpdateTranslationModel): Either<Throwable, Unit>
}
