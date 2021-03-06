package io.github.megh.marvelapi.superheroes.superherodetails

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
import io.github.megh.marvelapi.superheroes.domain.SuperheroId
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow

class SuperheroDetailsFragment : Fragment(R.layout.fragment_compose) {

    private val superheroId: Long by lazy(LazyThreadSafetyMode.NONE) {
        val id = requireArguments().getLong(EXTRA_SUPERHERO_ID, -1)
        check(id != -1L) { "Please use newBundle() for creating the arguments" }
        id
    }

    private val viewModel: SuperheroDetailsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val composeView = view as ComposeView
        composeView.setViewCompositionStrategy(DisposeOnViewTreeLifecycleDestroyed)

        val module = object : SuperheroDetailsModule,
            AppModule by requireActivity().appModule(),
            ViewModelAlgebra<SuperheroDetailsViewState, SuperheroDetailsEffect> by viewModel {}

        val actions = Channel<SuperheroDetailsAction>(Channel.UNLIMITED)

        with(module) {
            composeView.setContent {
                CompositionLocalProvider(LocalImageLoader provides this) {
                    SuperheroDetailsScreen(
                        stateFlow = viewState,
                        initialState = Loading,
                        superheroId = superheroId,
                        actions = actions
                    )
                }
            }

            lifecycleScope.launchWhenStarted {
                program(superheroId, actions.receiveAsFlow())
            }
        }

        handleEffects()
    }

    private fun handleEffects() {
        lifecycleScope.launchWhenStarted {
            viewModel.effects.map { effect ->
                when (effect) {
                    NavigateUp -> findNavController().navigateUp()
                }
            }.collect()
        }
    }

    companion object {

        private const val EXTRA_SUPERHERO_ID = "EXTRA_SUPERHERO_ID"

        fun newBundle(superheroId: SuperheroId): Bundle =
            Bundle().apply {
                putLong(EXTRA_SUPERHERO_ID, superheroId)
            }
    }
}