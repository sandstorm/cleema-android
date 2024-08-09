package de.cleema.android.shared

import de.cleema.android.core.data.UserRepository
import de.cleema.android.core.data.user
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RegionForCurrentUserUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(): de.cleema.android.core.models.Region =
        userRepository.getUserStream().map { it?.user }.filterNotNull().first().region
}
