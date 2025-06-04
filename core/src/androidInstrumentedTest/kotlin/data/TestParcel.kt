package data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TestParcel(
    val firstName: String,
    val lastName: String,
    val age: Int,
    val isMarried: Boolean,
    val height: Float,
    val weight: Double,
) : Parcelable
