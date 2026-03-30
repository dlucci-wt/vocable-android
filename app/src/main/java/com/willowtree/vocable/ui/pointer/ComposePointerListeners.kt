package com.willowtree.vocable.ui.pointer

import com.willowtree.vocable.customviews.PointerListener
import com.willowtree.vocable.utils.SpokenText
import com.willowtree.vocable.utils.VocableSharedPreferences
import com.willowtree.vocable.utils.VocableTextToSpeech
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

/** [PointerListener] backed by lambdas (e.g. Compose). */
class LambdaPointerListener(
    private val onEnter: () -> Unit,
    private val onExit: () -> Unit,
) : PointerListener {
    override fun onPointerEnter() = onEnter()
    override fun onPointerExit() = onExit()
}

/**
 * Head-pointer dwell + action (no TTS; fires on dwell completion).
 */
class ImageDwellPointerListener(
    private val backgroundScope: CoroutineScope,
    private val getDwellMs: () -> Long,
    private val onFire: () -> Unit,
) : PointerListener {
    private var job: Job? = null

    override fun onPointerEnter() {
        job = backgroundScope.launch {
            delay(getDwellMs())
            withContext(Dispatchers.Main) { onFire() }
        }
    }

    override fun onPointerExit() {
        job?.cancel()
        job = null
    }
}

/**
 * Category row: dwell then select; no TTS; skips when already selected.
 */
class CategoryDwellPointerListener(
    private val backgroundScope: CoroutineScope,
    private val getDwellMs: () -> Long,
    private val isSelected: () -> Boolean,
    private val onSelect: () -> Unit,
) : PointerListener {
    private var job: Job? = null

    override fun onPointerEnter() {
        if (isSelected()) return
        job = backgroundScope.launch {
            delay(getDwellMs())
            withContext(Dispatchers.Main) { onSelect() }
        }
    }

    override fun onPointerExit() {
        job?.cancel()
        job = null
    }
}

/**
 * Phrase / text button: dwell then TTS + action.
 */
class TextDwellPointerListener(
    private val backgroundScope: CoroutineScope,
    private val getDwellMs: () -> Long,
    private val getSpeakText: () -> CharSequence?,
    private val locale: Locale,
    private val onFire: () -> Unit,
) : PointerListener {
    private var job: Job? = null

    override fun onPointerEnter() {
        job = backgroundScope.launch {
            delay(getDwellMs())
            withContext(Dispatchers.Main) {
                getSpeakText()?.takeIf { it.isNotBlank() }?.let {
                    VocableTextToSpeech.speak(locale, it.toString())
                    SpokenText.postValue(it.toString())
                }
                onFire()
            }
        }
    }

    override fun onPointerExit() {
        job?.cancel()
        job = null
    }
}

fun dwellMsFromPrefs(prefs: VocableSharedPreferences): Long = prefs.getDwellTime()
