package io.github.megh.marvelapi.superheroes.data

import io.github.megh.marvelapi.superheroes.NetworkError
import io.github.megh.marvelapi.superheroes.ServerError
import io.github.megh.marvelapi.superheroes.SuperheroException
import io.github.megh.marvelapi.superheroes.Unrecoverable
import io.github.megh.marvelapi.superheroes.domain.Superhero
import io.github.megh.marvelapi.superheroes.domain.SuperheroDetails
import io.github.megh.marvelapi.superheroes.domain.SuperheroId
import io.github.megh.marvelapi.superheroes.domain.Superheroes
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException

suspend fun SuperheroesService.superheroes(): Superheroes {
    val superheroesDto = runRefineError { getSuperheroes() }
    val superheroes = superheroesDto.data.results.map { it.toDomain() }
    return Superheroes(superheroes, superheroesDto.attributionText)
}

suspend fun SuperheroesService.superheroDetails(id: SuperheroId): SuperheroDetails {
    val superheroDto = runRefineError { getSuperheroDetails(id) }
    val superhero = superheroDto.data.results.first().toDomain()
    return SuperheroDetails(superhero, superheroDto.attributionText)
}

private fun SuperheroDto.toDomain(): Superhero = Superhero.create(
    id = id,
    name = name,
    thumbnailPath = thumbnail.path,
    thumbnailExt = thumbnail.extension,
    comicsCount = comics.available,
    storiesCount = stories.available,
    eventsCount = events.available,
    seriesCount = series.available
)

private suspend fun <A> runRefineError(f: suspend () -> A): A =
    try {
        f()
    } catch (e: CancellationException) {
        throw e
    } catch (e: HttpException) {
        throw when (e.code()) {
            in 500..599 -> SuperheroException(
                ServerError(e.code(), e.message())
            )
            else -> SuperheroException(Unrecoverable(e))
        }
    } catch (e: IOException) {
        throw SuperheroException(NetworkError(e))
    } catch (e: Throwable) {
        throw SuperheroException(Unrecoverable(e))
    }