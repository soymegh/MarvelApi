package io.github.megh.marvelapi.common

import io.github.megh.marvelapi.superheroes.SuperheroException
import io.github.megh.marvelapi.superheroes.Unrecoverable
import timber.log.Timber

class CrashReportingTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (t != null && t is SuperheroException) {
            @Suppress("ControlFlowWithEmptyBody") // No Crash Reporting library for the sample
            if (t.error is Unrecoverable) {
                // Log a non-fatal problem to some crash-reporting service
                // This is limited to errors of type Unrecoverable because those are the ones that
                // indicate possible bugs
            }
        }
    }
}