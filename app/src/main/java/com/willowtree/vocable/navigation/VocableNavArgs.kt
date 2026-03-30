package com.willowtree.vocable.navigation

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Base64
import com.willowtree.vocable.presets.Category
import com.willowtree.vocable.presets.Phrase

/**
 * Encodes [Parcelable] navigation args into a single URL-safe string for Navigation Compose routes.
 * Same bundle keys as legacy Safe Args (`category`, `phrase`).
 */
object VocableNavArgs {
    const val CATEGORY = "category"
    const val PHRASE = "phrase"

    fun encodeParcelable(p: Parcelable): String {
        val parcel = Parcel.obtain()
        try {
            parcel.writeParcelable(p, 0)
            val bytes = parcel.marshall()
            return Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_WRAP)
        } finally {
            parcel.recycle()
        }
    }

    inline fun <reified T : Parcelable> decodeParcelable(encoded: String, classLoader: ClassLoader): T {
        val bytes = Base64.decode(encoded, Base64.URL_SAFE or Base64.NO_WRAP)
        val parcel = Parcel.obtain()
        try {
            parcel.unmarshall(bytes, 0, bytes.size)
            parcel.setDataPosition(0)
            @Suppress("DEPRECATION")
            return requireNotNull(parcel.readParcelable(classLoader) as? T) { "Failed to decode parcelable" }
        } finally {
            parcel.recycle()
        }
    }

    fun decodeCategory(encoded: String, classLoader: ClassLoader): Category =
        decodeParcelable(encoded, classLoader)

    fun decodePhrase(encoded: String, classLoader: ClassLoader): Phrase =
        decodeParcelable(encoded, classLoader)

    fun bundleCategory(category: Category): Bundle = Bundle().apply { putParcelable(CATEGORY, category) }

    fun bundlePhrase(phrase: Phrase): Bundle = Bundle().apply { putParcelable(PHRASE, phrase as Parcelable) }

    fun bundleCategoryNullable(category: Category?): Bundle =
        if (category == null) Bundle.EMPTY else bundleCategory(category)
}

