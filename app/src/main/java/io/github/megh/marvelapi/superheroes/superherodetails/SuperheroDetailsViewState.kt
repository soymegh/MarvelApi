package io.github.megh.marvelapi.superheroes.superherodetails

import io.github.megh.marvelapi.common.PlaceholderString
import io.github.megh.marvelapi.common.TextRes
import okhttp3.HttpUrl

data class SuperheroDetailsViewEntity(
    val name: String,
    val thumbnail: HttpUrl,
    val comics: PlaceholderString<Int>,
    val stories: PlaceholderString<Int>,
    val events: PlaceholderString<Int>,
    val series: PlaceholderString<Int>
)

sealed class SuperheroDetailsViewState {

    val title: String
        get() = if (this is Content) superhero.name else ""
}

object Loading : SuperheroDetailsViewState()

data class Content(
    val superhero: SuperheroDetailsViewEntity,
    val attribution: String
) : SuperheroDetailsViewState()

data class Problem(val stringId: TextRes) : SuperheroDetailsViewState()