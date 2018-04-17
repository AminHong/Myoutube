package com.amin.myoutube.presenters

import android.content.Context
import android.support.v17.leanback.widget.BaseCardView
import android.support.v17.leanback.widget.ImageCardView
import com.amin.myoutube.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.api.services.youtube.model.PlaylistItem

class ImageCardViewPresenter(getContext: Context): AbstractCardPresenter<ImageCardView>(getContext) {
    override fun onCreateView(): ImageCardView {
        val imageCardView = ImageCardView(getContext())
        return imageCardView
    }

    override fun onBindViewHolder(playlist: PlaylistItem, cardView: BaseCardView) {
        if(playlist.snippet.thumbnails == null){
            return
        }

        (cardView as ImageCardView).tag = playlist
        cardView.titleText = playlist.snippet.title
        cardView.contentText = playlist.snippet.description

        val options = RequestOptions()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.lb_background)
                .error(R.drawable.lb_background)

        Glide.with(getContext())
                .load(playlist.snippet.thumbnails.high.url)
                .apply(options)
                .into(cardView.mainImageView)
    }
}