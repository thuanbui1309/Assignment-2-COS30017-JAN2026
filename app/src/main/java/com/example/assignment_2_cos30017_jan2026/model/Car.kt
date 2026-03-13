package com.example.assignment_2_cos30017_jan2026.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Car(
    val name: String,
    val model: String,
    val year: Int,
    val rating: Int,
    val kilometres: Int,
    val dailyCost: Int,
    val imageResId: Int,
    var isFavourite: Boolean = false,
    var isAvailable: Boolean = true
) : Parcelable
