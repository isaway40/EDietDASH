package com.example.e_dietdash.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Edit(
    var nama: String? = null,
    var weight: String? = null
): Parcelable
