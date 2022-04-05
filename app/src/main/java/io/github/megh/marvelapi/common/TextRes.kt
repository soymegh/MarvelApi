package io.github.megh.marvelapi.common

import androidx.annotation.StringRes
import io.github.megh.marvelapi.R

sealed class TextRes

data class IdTextRes(@StringRes val id: Int) : TextRes()

data class ErrorTextRes(
    @StringRes val id: Int,
    @StringRes val retryTextId: Int = R.string.error_retry_text
) : TextRes()