/*
 * Copyright 2018 Mateusz Utkala (DonMat)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.utkala.searchablespinner

import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity

class SearchableSpinner : androidx.appcompat.widget.AppCompatSpinner, View.OnTouchListener,
    OnSearchableItemClick<ItemSpinner?> {

    private lateinit var searchDialog: SearchableSpinnerDialog
    private val mContext: Context
    private var mDialogTitle: String? = null
    private var mCloseText: String? = null
    private var mItems: MutableList<Any?> = mutableListOf(null)
    private var mDialogTitleColor: Int? = null
    private var mDialogBackgroundColor: Int? = null
    private var mDialogBackground: Drawable? = null
    private var mCornersSize: Float? = null
    private var mSearchColorLine: Int? = null
    private var mSearchColorMag: Int? = null
    private var mSearchColorText: Int? = null
    private var mSearchColorButtonClose: Int? = null
    private var mCustomDialogAdapter: FilterableListAdapter<*, *>? = null
    var onSearchableItemClick: OnSearchableItemClick<ItemSpinner?>? = null
    var showHint: Boolean = false

    constructor(context: Context) : super(context) {
        this.mContext = context
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.mContext = context
        setAttributes(context, attrs)
        init()
    }

    constructor (context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.mContext = context
        setAttributes(context, attrs)
        init()
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (searchDialog.isAdded) return true

        if (event?.action == MotionEvent.ACTION_UP) {
            if (adapter != null) {
                mItems.clear()
                for (i in (if (showHint) 1 else 0) until (adapter.count)) {
                    mItems.add(adapter.getItem(i))
                }
                val fm = scanForActivity(mContext)?.supportFragmentManager
                if (!searchDialog.isVisible && fm != null)
                    searchDialog.show(fm, "search")
            }
        }
        return true
    }

    override fun onSearchableItemClicked(item: ItemSpinner?, position: Int) {
        val itemPosition = position + (if (showHint) 1 else 0)
        if (onSearchableItemClick != null) {
            onSearchableItemClick?.onSearchableItemClicked(item, itemPosition)
        } else {
            setSelection(itemPosition)
        }
    }

    fun setDialogTitle(title: String?) {
        mDialogTitle = title
        searchDialog.setTitle(title)
    }

    fun setDismissText(dismiss: String?) {
        mCloseText = dismiss
        searchDialog.setDismissText(dismiss)
    }

    fun setDismissText(dismiss: String?, onDismissListener: DialogInterface.OnClickListener) {
        mCloseText = dismiss
        searchDialog.setDismissText(dismiss, onDismissListener)
    }

    private fun init() {
        searchDialog = SearchableSpinnerDialog.getInstance(mDialogBackground, mCustomDialogAdapter, mCornersSize)
        searchDialog.setColorsSearchView(mSearchColorLine, mSearchColorMag, mSearchColorText, mSearchColorButtonClose)
        searchDialog.setTitle(mDialogTitle)
        searchDialog.setDismissText(mCloseText)
        searchDialog.setColorTitle(mDialogTitleColor)
        searchDialog.setColorBackground(mDialogBackgroundColor)
        searchDialog.onSearchableItemClick = this

        setOnTouchListener(this)
    }

    private fun setAttributes(context: Context, attrs: AttributeSet) {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.SearchableSpinner)

        for (i in 0 until attributes.indexCount) {
            when (val attr = attributes.getIndex(i)) {
                R.styleable.SearchableSpinner_searchLineColor -> mSearchColorLine = attributes.getColor(attr, Color.BLACK)
                R.styleable.SearchableSpinner_searchMagColor -> mSearchColorMag = attributes.getColor(attr, Color.BLACK)
                R.styleable.SearchableSpinner_searchTextColor -> mSearchColorText = attributes.getColor(attr, Color.BLACK)
                R.styleable.SearchableSpinner_searchButtonCloseColor -> mSearchColorButtonClose = attributes.getColor(attr, Color.BLACK)

                R.styleable.SearchableSpinner_cornersSize -> mCornersSize =
                    attributes.getDimension(
                        attr, context.resources.displayMetrics.density * 12
                    )

                R.styleable.SearchableSpinner_dialogBackgroundColor -> mDialogBackgroundColor =
                    attributes.getColor(attr, Color.WHITE)

                R.styleable.SearchableSpinner_dialogTitleColor -> mDialogTitleColor =
                    attributes.getColor(attr, Color.BLACK)

                R.styleable.SearchableSpinner_closeText -> mCloseText = attributes.getString(attr)
                R.styleable.SearchableSpinner_dialogTitle -> mDialogTitle =
                    attributes.getString(attr)

                R.styleable.SearchableSpinner_showHint -> showHint =
                    attributes.getBoolean(attr, false)
            }
        }
        attributes.recycle()
    }

    private fun scanForActivity(context: Context?): AppCompatActivity? {
        return when (context) {
            is AppCompatActivity -> context
            is ContextWrapper -> scanForActivity(context.baseContext)
            else -> null
        }

    }

    /**
     * Searchable spinner works only with ArrayAdapter. Spinner Adapter **lost state after rotate**
     *
     *  @param adapter ArrayAdapter<Any?>
     */
    fun setAdapter(adapter: ArrayAdapter<Any?>) {
        super.setAdapter(adapter)
    }

    fun setDialogBackground(background: Drawable) {
        mDialogBackground = background
        if (searchDialog.isVisible) searchDialog.dismiss()
        init()
    }

    fun <T : FilterableListAdapter<*, *>> setCustomDialogAdapter(adapter: T) {
        mCustomDialogAdapter = adapter
        if (searchDialog.isVisible) searchDialog.dismiss()
        init()
    }
}
