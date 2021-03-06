package io.github.megh.marvelapi.superheroes.superheroslist

import io.github.megh.marvelapi.common.ErrorTextRes
import io.github.megh.marvelapi.common.TextRes
import okhttp3.HttpUrl

data class SuperheroViewEntity(val id: Long, val name: String, val imageUrl: HttpUrl)

sealed class SuperheroesViewState

object Loading : SuperheroesViewState()

data class Content(
    val superheroes: List<SuperheroViewEntity>,
    val copyright: String
) : SuperheroesViewState()

data class Problem(val stringId: TextRes) : SuperheroesViewState()

val Problem.isRecoverable: Boolean
    get() = stringId is ErrorTextRes
