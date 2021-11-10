package net.noimply.sentence.business.viewmodel

sealed class ResultViewModel<out T : Any> {
    data class Complete<out T : Any>(val success: T) : ResultViewModel<T>()
    data class Error<out T : Any>(val exception: Exception) : ResultViewModel<T>()
    object InProgress : ResultViewModel<Nothing>()
}