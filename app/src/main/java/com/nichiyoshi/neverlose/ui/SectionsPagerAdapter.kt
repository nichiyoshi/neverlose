package com.nichiyoshi.neverlose.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.nichiyoshi.neverlose.domain.EVNoteFilter

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val filters: List<EVNoteFilter>, fm: FragmentManager)
    : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        val filter = filters.first { it.index == position }
        return ArticleFragment.newInstance(filter)
    }

    override fun getCount(): Int = filters.size
}