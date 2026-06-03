package com.taufiqsejati.kotix.checkout.model

import java.io.Serializable

data class Checkout (
    var kursi: String ?="",
    var harga: String ?=""
): Serializable
