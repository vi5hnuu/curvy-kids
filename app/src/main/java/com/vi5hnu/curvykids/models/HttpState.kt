package com.vi5hnu.curvykids.models

data class HttpState(
    val loading: Boolean? = null,
    val error: String? = null,
    val success: Boolean? = null
) {
    companion object {
        fun loading() = HttpState(loading = true)
        fun error(error: String) = HttpState(error = error)
        fun success() = HttpState(success = true)
    }
}