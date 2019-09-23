package com.nichiyoshi.neverlose.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import com.nichiyoshi.neverlose.R
import com.nichiyoshi.neverlose.domain.Batch
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.tab_component_layout.view.*
import java.lang.Exception

class CustomTabLayout
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : TabLayout(context , attrs, defStyleAttr), NeverLoseTabInterface {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override lateinit var titles: List<String>

    override fun setupWithViewPager(viewPager: ViewPager?) {
        super.setupWithViewPager(viewPager)
        addViews()
    }

    private fun addViews(){

        if(titles.size != tabCount) throw Exception("the size of titles mut be equal to tabCount")

        for(i in 0 until tabCount){
            val tab = getTabAt(i)
            val tabView = inflater.inflate(R.layout.tab_component_layout, null)
            tabView.label.text = titles[i]
            tab?.customView = tabView
        }
    }

    override fun updateBatch(batch: Batch, index: Int) {
        getTabAt(index)?.customView?.batch?.isVisible = batch.isVisible
    }
}

interface NeverLoseTabInterface{

    val titles: List<String>

    fun updateBatch(batch: Batch, index: Int)

}