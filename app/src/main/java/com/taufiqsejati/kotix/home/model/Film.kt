package com.taufiqsejati.kotix.home.model

import java.io.Serializable

// Cukup import java.io.Serializable bawaan

data class Film(
    var desc: String? = "",
    var director: String? = "",
    var genre: String? = "",
    var judul: String? = "",
    var poster: String? = "",
    var rating: String? = "",
) : Serializable // <-- Ganti ke Serializable
