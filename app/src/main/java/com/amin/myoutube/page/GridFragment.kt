package com.amin.myoutube.page

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v17.leanback.app.BrowseSupportFragment
import android.support.v17.leanback.widget.*
import android.support.v4.app.Fragment
import android.support.v17.leanback.widget.OnItemViewSelectedListener
import android.support.v17.leanback.widget.OnItemViewClickedListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amin.myoutube.R
import android.support.v17.leanback.transition.TransitionHelper

open class GridFragment : Fragment(), BrowseSupportFragment.MainFragmentAdapterProvider {

    private var mAdapter: ObjectAdapter? = null
    private var mGridPresenter: VerticalGridPresenter? = null
    private var mGridViewHolder: VerticalGridPresenter.ViewHolder? = null
    private var mOnItemViewSelectedListener: OnItemViewSelectedListener? = null
    private var mOnItemViewClickedListener: OnItemViewClickedListener? = null

    private lateinit var mSceneAfterEntranceTransition: Any
    private var mSelectedPosition = -1

    private val mMainFragmentAdapter = object: BrowseSupportFragment.MainFragmentAdapter<GridFragment>(this){
        override fun setEntranceTransitionState(state: Boolean) {
            GridFragment.setEntranceTransitionState(state)
        }
    }

    companion object {
        lateinit var THIS_IS: GridFragment
        fun setEntranceTransitionState(afterTransition: Boolean) {
            THIS_IS.setEntranceTransitionState(afterTransition)
        }
    }

    /**
     * Sets the grid presenter.
     */
    fun setGridPresenter(gridPresenter: VerticalGridPresenter?) {
        if (gridPresenter == null) {
            throw IllegalArgumentException("Grid presenter may not be null")
        }

        mGridPresenter = gridPresenter
        mGridPresenter!!.onItemViewSelectedListener = mViewSelectedListener

        if (mOnItemViewClickedListener != null) {
            mGridPresenter!!.onItemViewClickedListener = mOnItemViewClickedListener
        }
    }

    /**
     * Returns the grid presenter.
     */
    fun getGridPresenter(): VerticalGridPresenter? {
        return mGridPresenter
    }

    /**
     * Sets the object adapter for the fragment.
     */
    fun setAdapter(adapter: ObjectAdapter) {
        mAdapter = adapter
        updateAdapter()
    }

    /**
     * Returns the object adapter.
     */
    fun getAdapter(): ObjectAdapter? {
        return mAdapter
    }

    private val mViewSelectedListener = OnItemViewSelectedListener { itemViewHolder, item, rowViewHolder, row ->
        if(mGridViewHolder != null && mGridViewHolder?.gridView != null){
            val position = mGridViewHolder?.gridView?.selectedPosition ?: 0
            gridOnItemSelected(position)
        }

        if (mOnItemViewSelectedListener != null) {
            mOnItemViewSelectedListener!!.onItemSelected(itemViewHolder, item, rowViewHolder, row)
        }
    }

    private val mChildLaidOutListener = OnChildLaidOutListener { _, _, position, _ ->
        if (position == 0) {
            showOrHideTitle()
        }
    }

    /**
     * Sets an item selection listener.
     */
    fun setOnItemViewSelectedListener(listener: OnItemViewSelectedListener) {
        mOnItemViewSelectedListener = listener
    }

    private fun gridOnItemSelected(position: Int) {
        if (position != mSelectedPosition) {
            mSelectedPosition = position
            showOrHideTitle()
        }
    }

    private fun showOrHideTitle() {
        if (mGridViewHolder?.gridView?.findViewHolderForAdapterPosition(mSelectedPosition) == null) {
            return
        }

        if (mGridViewHolder?.gridView?.hasPreviousViewInSameRow(mSelectedPosition) != true) {
            mMainFragmentAdapter.fragmentHost.showTitleView(true)
        } else {
            mMainFragmentAdapter.fragmentHost.showTitleView(false)
        }
    }

    /**
     * Sets an item clicked listener.
     */
    fun setOnItemViewClickedListener(listener: OnItemViewClickedListener) {
        mOnItemViewClickedListener = listener

        if (mGridPresenter != null) {
            mGridPresenter!!.onItemViewClickedListener = mOnItemViewClickedListener
        }
    }

    /**
     * Returns the item clicked listener.
     */
    fun getOnItemViewClickedListener(): OnItemViewClickedListener? {
        return mOnItemViewClickedListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        GridFragment.THIS_IS = this
        return inflater.inflate(R.layout.grid_fragment, container, false)
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gridDock = view.findViewById(R.id.browse_grid_dock) as ViewGroup
        if(mGridPresenter != null) {
            mGridViewHolder = mGridPresenter?.onCreateViewHolder(gridDock)

            if(mGridViewHolder != null) {
                gridDock.addView(mGridViewHolder?.view)
                mGridViewHolder?.gridView?.setOnChildLaidOutListener(mChildLaidOutListener)
            }
        }

        mSceneAfterEntranceTransition = TransitionHelper.createScene(gridDock) {
            setEntranceTransitionState(true)
        }

        mainFragmentAdapter.fragmentHost.notifyViewCreated(mMainFragmentAdapter)
        updateAdapter()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mGridViewHolder = null
    }

    override fun getMainFragmentAdapter(): BrowseSupportFragment.MainFragmentAdapter<*> {
        return mMainFragmentAdapter
    }

    /**
     * Sets the selected item position.
     */
    fun setSelectedPosition(position: Int) {
        mSelectedPosition = position

        if (mGridViewHolder != null && mGridViewHolder?.gridView?.adapter != null) {
            mGridViewHolder?.gridView?.setSelectedPositionSmooth(position)
        }
    }

    private fun updateAdapter() {
        if (mGridViewHolder != null) {
            mGridPresenter?.onBindViewHolder(mGridViewHolder, mAdapter)

            if (mSelectedPosition != -1) {
                mGridViewHolder?.gridView?.selectedPosition = mSelectedPosition
            }
        }
    }

    private fun setEntranceTransitionState(afterTransition: Boolean) {
        if(mGridPresenter != null) {
            mGridPresenter?.setEntranceTransitionState(mGridViewHolder, afterTransition)
        }
    }
}