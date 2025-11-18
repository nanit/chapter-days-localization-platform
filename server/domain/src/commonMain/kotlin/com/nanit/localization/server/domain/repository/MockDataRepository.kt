package com.nanit.localization.server.domain.repository

import arrow.core.Either

interface MockDataRepository {
    suspend fun fillWithMock(): Either<Throwable, Unit>
}
