package com.nanit.localization.server.domain.repository

import arrow.core.Either
import com.nanit.localization.server.domain.model.StringResource

interface TranslationsRepository {
    suspend fun insert(vararg value: StringResource.Value): Either<Throwable, Unit>
}
