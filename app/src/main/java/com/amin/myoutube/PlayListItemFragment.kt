package com.amin.myoutube

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v17.leanback.widget.ArrayObjectAdapter
import android.support.v17.leanback.widget.FocusHighlight
import android.support.v17.leanback.widget.OnItemViewClickedListener
import com.amin.myoutube.page.GridFragment
import android.support.v17.leanback.widget.VerticalGridPresenter
import android.view.ContextThemeWrapper
import android.widget.Toast
import com.amin.myoutube.presenters.ImageCardViewPresenter
import com.google.api.services.youtube.model.PlaylistItem
import com.google.api.services.youtube.model.PlaylistItemListResponse
import java.lang.Exception

class PlayListItemFragment : GridFragment() {

    companion object {
        const val COLUMNS = 4
        const val ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_SMALL

        lateinit var THIS_IS: PlayListItemFragment

        fun onResult(result: List<PlaylistItem>?){
            THIS_IS.getPlayListItems(result)
        }
    }

    private lateinit var playListId: String
    private lateinit var mAdapter: ArrayObjectAdapter

    override fun setArguments(args: Bundle?) {
        playListId = args?.getString("id") ?: ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PlayListItemFragment.THIS_IS = this
        setupAdapter()
        MakeRequestTask(playListId).execute()
        mainFragmentAdapter.fragmentHost.notifyDataReady(mainFragmentAdapter)
    }

    private fun setupAdapter() {
        val presenter = VerticalGridPresenter(ZOOM_FACTOR)
        presenter.numberOfColumns = COLUMNS
        presenter.shadowEnabled = false
        setGridPresenter(presenter)

        val cardPresenter = ImageCardViewPresenter(ContextThemeWrapper(requireContext(), R.style.DefaultCardTheme))
        mAdapter = ArrayObjectAdapter(cardPresenter)
        setAdapter(mAdapter)

        setOnItemViewClickedListener(OnItemViewClickedListener { _, item, _, _ ->
            Toast.makeText(activity, "Clicked on " + (item as PlaylistItem).snippet.title, Toast.LENGTH_SHORT).show()
            val intent = Intent(activity, PlayerActivity::class.java)
            intent.putExtra("id", item.contentDetails.videoId)
            activity?.startActivity(intent)
        })
    }

    fun getPlayListItems(result: List<PlaylistItem>?) {
        if (result != null && result.isNotEmpty()) {
            //println("getPlayListItems result size :  ${result.size}")
            for (i in 0 until result.size) {
                //println("result($i) : ${result[i]}")
                if(result[i].snippet.thumbnails != null) {
                    mAdapter.add(result[i])
                }
            }
        }
    }

    class MakeRequestTask(private var playListId: String) : AsyncTask<Void, Void, List<PlaylistItem>>(){
        override fun doInBackground(vararg params: Void?): List<PlaylistItem>? {
            return try {
                getPlayListItemFromApi()
            }catch (e: Exception){
                Toast.makeText(AppController.getInstance(), e.message, Toast.LENGTH_SHORT).show()
                null
            }
        }

        private fun getPlayListItemFromApi(): List<PlaylistItem>?{
            val result: PlaylistItemListResponse = MainActivity.mService.playlistItems().list("snippet,contentDetails")
                    .setPlaylistId(playListId)
                    .setMaxResults(25)
                    .execute()

            return result.items
        }

        override fun onPostExecute(result: List<PlaylistItem>?) {
            if (result == null || result.isEmpty()) {
                Toast.makeText(AppController.getInstance(), "No network connection available.", Toast.LENGTH_SHORT).show()
            }else{
                PlayListItemFragment.onResult(result)
            }
        }
    }
}