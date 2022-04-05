package io.github.megh.marvelapi.superheroes.superheroslist

import io.github.megh.marvelapi.common.presentation.JetpackViewModel
import io.github.megh.marvelapi.superheroes.domain.SuperheroId

sealed class SuperheroesAction
object Refresh : SuperheroesAction()
data class LoadDetails(val id: SuperheroId) : SuperheroesAction()

sealed class SuperheroesEffect
data class NavigateToDetails(val superheroId: SuperheroId) : SuperheroesEffect()

typealias SuperheroesViewModel = JetpackViewModel<SuperheroesViewState, SuperheroesEffect>
