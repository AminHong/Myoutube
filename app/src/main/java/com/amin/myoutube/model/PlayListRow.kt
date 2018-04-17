package com.amin.myoutube.model

import com.google.api.services.youtube.model.Playlist

class PlayListRow {
    companion object {
        const val TYPE_DEFAULT = 0
        const val TYPE_SECTION_HEADER = 1
        const val TYPE_DIVIDER = 2
    }

    val mType = TYPE_DEFAULT
    // Used to determine whether the row shall use shadows when displaying its cards or not.
    val mShadow = true
    var mTitle: String? = null
    var mPlayLists: List<Playlist>? = null

    fun getType(): Int {
        return mType
    }

    fun getTitle(): String? {
        return mTitle
    }

    fun useShadow(): Boolean {
        return mShadow
    }

    fun getPlayLists(): List<Playlist>? {
        return mPlayLists
    }
}