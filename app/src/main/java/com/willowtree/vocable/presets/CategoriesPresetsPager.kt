package com.willowtree.vocable.presets

import kotlin.math.ceil
import kotlin.math.min

/**
 * Category paging math for presets (replaces [CategoriesComposePagerAdapter] RecyclerView shell).
 */
class CategoriesPresetsPager(
    private val maxItemsPerPage: Int,
) {

    private val items = mutableListOf<Category>()
    private var baseId = 0L
    var numPages: Int = 0
        private set

    fun setItems(newItems: List<Category>) {
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

    fun getItemsByPosition(position: Int): List<Category> {
        if (items.isEmpty() || numPages == 0) return emptyList()
        val startPosition = (position % numPages) * maxItemsPerPage
        return items.subList(
            startPosition,
            min(items.size, startPosition + maxItemsPerPage),
        )
    }

    fun getSize(): Int = items.size

    fun getCategory(position: Int): Category {
        return if (position >= items.size) {
            items[position % items.size]
        } else {
            items[position]
        }
    }
}
