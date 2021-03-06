package io.github.megh.marvelapi.superheroes.superheroslist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coil.compose.LocalImageLoader
import coil.compose.rememberImagePainter
import io.github.megh.marvelapi.R
import io.github.megh.marvelapi.superheroes.domain.SuperheroId
import io.github.megh.marvelapi.superheroes.ui.common.CopyrightView
import io.github.megh.marvelapi.superheroes.ui.common.SuperheroProblem
import io.github.megh.marvelapi.superheroes.ui.common.SuperherosLoading
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow

@Composable
fun SuperheroesScreen(
    stateFlow: Flow<SuperheroesViewState>,
    initialValue: SuperheroesViewState,
    actions: Channel<SuperheroesAction>
) {
    val state by stateFlow.collectAsState(initialValue)
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) })
        }
    ) {
        when (val s = state) {
            Loading -> SuperherosLoading()
            is Content -> Content(s) { actions.trySend(LoadDetails(it)) }
            is Problem -> SuperheroProblem(s.stringId) { actions.trySend(Refresh) }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Content(content: Content, loadDetails: (SuperheroId) -> Unit) {
    Column(Modifier.testTag("SuperheroesContent")) {
        LazyVerticalGrid(cells = GridCells.Adaptive(175.dp), Modifier.weight(1f)) {
            items(content.superheroes) {
                SuperHeroItem(it, loadDetails)
            }
        }
        CopyrightView(text = content.copyright)
    }
}

@Composable
fun SuperHeroItem(entity: SuperheroViewEntity, onClick: (SuperheroId) -> Unit) {
    val imageLoader = LocalImageLoader.current

    Box(
        modifier = Modifier
            .clickable { onClick(entity.id) }
            .aspectRatio(1f)
            .semantics(mergeDescendants = true) {},
    ) {
        Image(
            painter = rememberImagePainter(entity.imageUrl, imageLoader),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.White.copy(alpha = 0.7f)),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = entity.name,
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}
