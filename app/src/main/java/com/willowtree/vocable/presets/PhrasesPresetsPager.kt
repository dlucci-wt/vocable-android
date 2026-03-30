package com.willowtree.vocable.presets

import kotlin.math.ceil
import kotlin.math.min

/**
 * Phrase paging math for presets (replaces [PhrasesComposePagerAdapter] RecyclerView shell).
 */
class PhrasesPresetsPager {

    companion object {
        const val MAX_NUMBER_PAD_PHRASES = 12
    }

    private val items = mutableListOf<PhraseGridItem>()
    private var baseId = 0L
    var numPages: Int = 0
        private set
    private var maxItemsPerPage: Int = 1
    private var pageKind: PresetsPhrasesPageKind = PresetsPhrasesPageKind.Standard

    fun setItems(
        newItems: List<PhraseGridItem>,
        maxItemsPerPage: Int,
        pageKind: PresetsPhrasesPageKind,
    ) {
        this.maxItemsPerPage = maxItemsPerPage
        this.pageKind = pageKind
        baseId = System.currentTimeMillis()
        items.clear()
        items.addAll(newItems)

        numPages = if (newItems.isEmpty() || maxItemsPerPage >= newItems.size) {
            1
        } else {
            ceil(newItems.size / maxItemsPerPage.toDouble()).toInt()
        }
    }

    fun getItemId(position: Int): Long = baseId - position

    fun getItemsByPosition(position: Int): List<PhraseGridItem> {
        if (items.isEmpty() || numPages == 0) return emptyList()
        val startPosition = (position % numPages) * maxItemsPerPage
        return items.subList(
            startPosition,
            min(items.size, startPosition + maxItemsPerPage),
        )
    }

    fun currentPageKind(): PresetsPhrasesPageKind = pageKind
}
