package de.cleema.android.survey

import de.cleema.android.core.data.SurveysRepository
import kotlinx.coroutines.flow.first

class SurveyCountUseCase(private val repository: SurveysRepository) {
    suspend operator fun invoke(): Int =
        repository.getSurveysStream().first().fold(onSuccess = { it.count() }, onFailure = { 0 })
}
