package com.amin.myoutube.presenters

import android.content.Context
import android.support.v17.leanback.widget.BaseCardView
import android.support.v17.leanback.widget.Presenter
import android.view.ViewGroup
import com.google.api.services.youtube.model.PlaylistItem

@Suppress("UNCHECKED_CAST")
abstract class AbstractCardPresenter<out T : BaseCardView>(private val context: Context) : Presenter() {

    fun getContext(): Context {
        return context
    }

    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        val cardView = onCreateView()
        return Presenter.ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
        val playlist = item as PlaylistItem
        onBindViewHolder(playlist, viewHolder?.view as T)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
        onUnbindViewHolder()
    }

    protected abstract fun onCreateView(): T

    abstract fun onBindViewHolder(playlist: PlaylistItem, cardView: BaseCardView)

    private fun onUnbindViewHolder() {
        // Nothing to clean up. Override if necessary.
    }
}