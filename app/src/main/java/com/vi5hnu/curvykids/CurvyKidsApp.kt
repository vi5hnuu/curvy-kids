package com.vi5hnu.curvykids

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.SvgDecoder

/**
 * Application entry point. Provides the singleton Coil [ImageLoader] used by every [coil.compose.AsyncImage]
 * in the app, wired with the SVG decoder so the kids-asset `.svg` badges (in `assets/kids/…`) render.
 *
 * Registering the loader here (instead of building one per call site) keeps a shared memory/disk
 * cache, so a badge that appears on many screens is decoded once.
 */
class CurvyKidsApp : Application(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader =
        ImageLoader.Builder(this)
            .components { add(SvgDecoder.Factory()) }
            .build()
}
