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

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dialog_layout.view.listView
import kotlinx.android.synthetic.main.dialog_layout.view.searchView
import pl.utkala.searchablespinner.databinding.DialogLayoutBinding

class SearchableSpinnerDialog : DialogFragment(), SearchView.OnQueryTextListener,
    SearchView.OnCloseListener {
    private var mListView: RecyclerView? = null
    private var mSearchView: SearchView? = null
    private var mDismissText: String? = null
    private var mDialogTitle: String? = null
    private var mDialogBackground: Drawable? = null
    private var mCornersSize: Float? = null
    private var mSearchColorLine: Int? = null
    private var mSearchColorMag: Int? = null
    private var mSearchColorText: Int? = null
    private var mSearchColorButtonClose: Int? = null
    private var mDismissListener: DialogInterface.OnClickListener? = null
    private var mCustomAdapter: FilterableListAdapter<*, *>? = null
    var onSearchableItemClick: OnSearchableItemClick<ItemSpinner?>? = null
    private var mDialogTitleColor: Int? = null
    private var mDialogBackgroundColor: Int? = null

    companion object {
        @JvmStatic
        val CLICK_LISTENER = "click_listener"

        fun getInstance(
            dialogBackground: Drawable? = null,
            customAdapter: FilterableListAdapter<*, *>? = null,
            cornersSize: Float?
        ): SearchableSpinnerDialog {
            val dialog = SearchableSpinnerDialog()
            dialog.mDialogBackground = dialogBackground
            dialog.mCustomAdapter = customAdapter
            dialog.mCornersSize = cornersSize
            return dialog
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (savedInstanceState != null) {
            onSearchableItemClick =
                savedInstanceState.getSerializable(CLICK_LISTENER) as OnSearchableItemClick<ItemSpinner?>
        }
        val layoutInflater = LayoutInflater.from(activity)
        val rootViewBinding =
            DialogLayoutBinding.bind(layoutInflater.inflate(R.layout.dialog_layout, null))

        setView(rootViewBinding.root)

        val alertBuilder = AlertDialog.Builder(activity)
        val title =
            if (mDialogTitle.isNullOrBlank()) getString(R.string.search_dialog_title) else mDialogTitle
        rootViewBinding.textTitleCard.text = title
        mCornersSize?.let {
            rootViewBinding.root.radius = it
        }

        mSearchColorText?.let { colorText ->
            rootViewBinding.searchView.findViewById<EditText>(R.id.search_src_text)?.let {
                it.setTextColor(colorText)
                it.setHintTextColor(colorText)
            }
        }

        mSearchColorLine?.let { colorLine ->
            rootViewBinding.searchView.findViewById<View>(R.id.search_plate)?.backgroundTintList =
                ColorStateList.valueOf(colorLine)
            rootViewBinding.searchView.findViewById<SearchView.SearchAutoComplete>(R.id.search_src_text)
                ?.backgroundTintList = ColorStateList.valueOf(colorLine)

        }

        mSearchColorButtonClose?.let { colorCloseButton ->
            rootViewBinding.searchView.findViewById<ImageView>(R.id.search_close_btn)?.let {
                it.setImageDrawable(it.drawable.let {
                    it.setTint(colorCloseButton)
                    it
                })
            }
        }

        mSearchColorMag?.let { colorMag ->
            rootViewBinding.searchView.findViewById<ImageView>(R.id.search_mag_icon)?.let {
                it.setImageDrawable(it.drawable.let {
                    it.setTint(colorMag)
                    it
                })
            }
        }

        mDialogBackgroundColor?.let {
            rootViewBinding.root.setCardBackgroundColor(it)
        }
        mDialogTitleColor?.let {
            rootViewBinding.textTitleCard.setTextColor(it)
        }


//        val dismiss =
//            if (mDismissText.isNullOrBlank()) getString(R.string.search_dialog_close) else mDismissText
//        alertBuilder.setPositiveButton(dismiss, mDismissListener)

        alertBuilder.setView(rootViewBinding.root)
        return alertBuilder.create()
    }

    private var listAdapter: FilterableListAdapter<*, *>? = null

    private fun setView(rootView: View?) {
        if (rootView == null) return

        listAdapter = mCustomAdapter
        mListView = rootView.listView
        mListView?.adapter = listAdapter
        listAdapter?.clickListener = object : OnClickListener {
            override fun onClick(position: Int) {
                onSearchableItemClick?.onSearchableItemClicked(
                    (mListView?.adapter as? FilterableListAdapter<*, *>)?.differ?.currentList?.get(
                        position
                    ), position
                )
                dialog?.dismiss()
            }
        }

        mSearchView = rootView.searchView
        mSearchView?.setOnQueryTextListener(this)
        mSearchView?.setOnCloseListener(this)
        mSearchView?.clearFocus()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        mSearchView?.clearFocus()
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (query.isNullOrBlank()) {
            (mListView?.adapter as FilterableListAdapter<*, *>).setFilter(null)
        } else {
            (mListView?.adapter as FilterableListAdapter<*, *>).setFilter(query)
        }
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(CLICK_LISTENER, onSearchableItemClick)
        super.onSaveInstanceState(outState)
    }

    override fun onClose(): Boolean {
        return false
    }

    override fun onPause() {
        onQueryTextChange("")
        super.onPause()
        dismiss()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(0))
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    fun setDismissText(closeText: String?) {
        mDismissText = closeText
    }


    fun setDismissText(closeText: String?, listener: DialogInterface.OnClickListener) {
        mDismissText = closeText
        mDismissListener = listener
    }


    fun setTitle(dialogTitle: String?) {
        mDialogTitle = dialogTitle
    }

    fun setColorTitle(colorTitle: Int?) {
        mDialogTitleColor = colorTitle
    }

    fun setColorBackground(colorBackground: Int?) {
        mDialogBackgroundColor = colorBackground
    }

    fun setColorsSearchView(
        colorLine: Int?,
        colorMag: Int?,
        colorText: Int?,
        colorCloseButton: Int?
    ) {
        mSearchColorLine = colorLine
        mSearchColorMag = colorMag
        mSearchColorText = colorText
        mSearchColorButtonClose = colorCloseButton
    }
}