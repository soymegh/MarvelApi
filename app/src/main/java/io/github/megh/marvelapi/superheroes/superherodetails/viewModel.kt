package io.github.megh.marvelapi.superheroes.superherodetails

import io.github.megh.marvelapi.common.presentation.JetpackViewModel
import io.github.megh.marvelapi.superheroes.domain.SuperheroId

sealed class SuperheroDetailsAction
data class Refresh(val superheroId: SuperheroId) : SuperheroDetailsAction()
object Up : SuperheroDetailsAction()

sealed class SuperheroDetailsEffect
object NavigateUp : SuperheroDetailsEffect()

typealias SuperheroDetailsViewModel = JetpackViewModel<SuperheroDetailsViewState, SuperheroDetailsEffect>