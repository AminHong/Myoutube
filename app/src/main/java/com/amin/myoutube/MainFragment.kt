package com.amin.myoutube

import android.os.Bundle
import android.support.v17.leanback.app.BrowseSupportFragment
import android.support.v17.leanback.widget.*
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import com.google.api.services.youtube.model.Playlist
import android.view.ContextThemeWrapper
import com.amin.myoutube.model.PlayListRow
import com.amin.myoutube.presenters.ImageCardViewPresenter

class MainFragment : BrowseSupportFragment(){

    private lateinit var mRowsAdapter: ArrayObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUIElements()
    }

    private fun setupUIElements() {
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        brandColor = ContextCompat.getColor(AppController.appController, R.color.colorPrimary)
    }

    fun setupMenuElements(result: List<Playlist>?){
        mRowsAdapter = ArrayObjectAdapter(ListRowPresenter())

        if (result != null && result.isNotEmpty()) {
            for(i in 0 until result.size){
                val playlist = result[i]
                val headerItem = HeaderItem(playlist.snippet.title)
                headerItem.contentDescription = playlist.id
                headerItem.description = playlist.snippet.description
                mRowsAdapter.add(PageRow(headerItem))
            }
        }

        adapter = mRowsAdapter
        mainFragmentRegistry.registerFragment(PageRow::class.java, PageRowFragmentFactory())
    }

    private fun createCardRow(playListRow: PlayListRow): ListRow {
        val presenterSelector = ImageCardViewPresenter(ContextThemeWrapper(requireContext(), R.style.DefaultCardTheme))
        val listRowAdapter = ArrayObjectAdapter(presenterSelector)
        for (playList in playListRow.getPlayLists()!!) {
            listRowAdapter.add(playList)
        }
        return ListRow(listRowAdapter)
    }

    class PageRowFragmentFactory : BrowseSupportFragment.FragmentFactory<Fragment>() {
        override fun createFragment(row: Any?): Fragment {
            val fragment = PlayListItemFragment()
            val bundle = Bundle()
            bundle.putString("id", (row as PageRow).headerItem.contentDescription.toString())
            fragment.arguments = bundle
            return fragment
        }
    }
}
