package de.cleema.android.shared

import de.cleema.android.core.data.UserRepository
import de.cleema.android.core.data.user
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CurrentUserUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(): de.cleema.android.core.models.User =
        userRepository.getUserStream().map { it?.user }.filterNotNull().first()
}
