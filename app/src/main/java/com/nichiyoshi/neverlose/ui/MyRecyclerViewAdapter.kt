package com.nichiyoshi.neverlose.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.nichiyoshi.neverlose.R
import com.nichiyoshi.neverlose.domain.EVNoteResult
import com.nichiyoshi.neverlose.domain.convertToTime
import kotlin.collections.ArrayList

class MyAdapter(private val onClickListener: OnClickArticleListener): RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    private var articles: ArrayList<EVNoteResult.Success> = arrayListOf()

    class MyViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(view){
        val title: TextView = view.findViewById(R.id.title)
        val uddated: TextView = view.findViewById(R.id.updated_date)
        val webview: WebView = view.findViewById(R.id.webview)
        val button : AppCompatButton = view.findViewById(R.id.button)
        val parent = view
    }

    interface OnClickArticleListener{

        fun onClick(Success: EVNoteResult.Success)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycle_item, parent, false) as ViewGroup
        return MyViewHolder(layout)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.title.text = articles[position].title
        holder.uddated.text = articles[position].updated.convertToTime()
        holder.webview.loadData(articles[position].content, "text/html", "UTF-8")
        holder.parent.setOnClickListener {
            onClickListener.onClick(articles[position])
        }
        holder.button.setOnClickListener {
            onClickListener.onClick(articles[position])
        }
    }

    override fun getItemCount() = articles.size

    fun setData(Success: EVNoteResult.Success){
        articles.add(Success)
        articles.sort()
        notifyDataSetChanged()
    }

    fun clearData(){
        articles.clear()
    }
}