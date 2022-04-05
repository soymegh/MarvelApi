package io.github.megh.marvelapi.superheroes.superheroslist

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.compose.LocalImageLoader
import io.github.megh.marvelapi.AppModule
import io.github.megh.marvelapi.R
import io.github.megh.marvelapi.appModule
import io.github.megh.marvelapi.common.presentation.ViewModelAlgebra
import io.github.megh.marvelapi.superheroes.superherodetails.SuperheroDetailsFragment
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow

class SuperheroesFragment : Fragment(R.layout.fragment_compose) {

    private val viewModel: SuperheroesViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val composeView = view as ComposeView
        composeView.setViewCompositionStrategy(DisposeOnViewTreeLifecycleDestroyed)

        val module: SuperheroesModule = object : SuperheroesModule,
            AppModule by requireActivity().appModule(),
            ViewModelAlgebra<SuperheroesViewState, SuperheroesEffect> by viewModel {}

        val actions = Channel<SuperheroesAction>(Channel.UNLIMITED)

        with(module) {
            composeView.setContent {
                CompositionLocalProvider(LocalImageLoader provides this) {
                    SuperheroesScreen(
                        stateFlow = viewState.onEach { afterBind(it) },
                        initialValue = Loading,
                        actions = actions
                    )
                }
            }

            lifecycleScope.launchWhenStarted {
                program(actions.receiveAsFlow())
            }
        }

        handleEffects()
    }

    private fun handleEffects() {
        lifecycleScope.launchWhenStarted {
            viewModel.effects.map { effect ->
                when (effect) {
                    is NavigateToDetails -> findNavController().navigate(
                        R.id.action_details,
                        SuperheroDetailsFragment.newBundle(effect.superheroId)
                    )
                }
            }.collect()
        }
    }
}