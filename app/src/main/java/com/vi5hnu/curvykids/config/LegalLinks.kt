package com.vi5hnu.curvykids.config

/**
 * Public legal pages for CurvyKids, hosted on the shared legal site (legal.laxmi.solutions).
 *
 * The production URL is used for every build variant (debug, release, …) on purpose — the legal
 * pages are environment-agnostic, so there is no per-environment host to switch on. Keeping the URLs
 * in one place means the "Grown-ups" links (and anywhere else they're needed) never drift.
 */
object LegalLinks {
    private const val BASE = "https://legal.laxmi.solutions/curvy-kids"

    const val PRIVACY_POLICY = "$BASE/privacy-policy"
    const val TERMS_OF_SERVICE = "$BASE/terms-of-service"
}
