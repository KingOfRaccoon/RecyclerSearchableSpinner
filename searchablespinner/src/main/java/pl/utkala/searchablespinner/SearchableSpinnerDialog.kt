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
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dialog_layout.view.listView
import kotlinx.android.synthetic.main.dialog_layout.view.searchView

class SearchableSpinnerDialog : DialogFragment(), SearchView.OnQueryTextListener,
    SearchView.OnCloseListener {
    private var items: MutableList<Any?> = arrayListOf("")
    private var mListView: RecyclerView? = null
    private var mSearchView: SearchView? = null
    private var mDismissText: String? = null
    private var mDialogTitle: String? = null
    private var mDialogBackground: Drawable? = null
    private var mDismissListener: DialogInterface.OnClickListener? = null
    private var mCustomAdapter: FilterableListAdapter<*, *>? = null
    var onSearchableItemClick: OnSearchableItemClick<Any?>? = null

    companion object {
        @JvmStatic
        val CLICK_LISTENER = "click_listener"

        fun getInstance(
            items: MutableList<Any?>,
            dialogBackground: Drawable? = null,
            customAdapter: FilterableListAdapter<*, *>? = null
        ): SearchableSpinnerDialog {
            val dialog = SearchableSpinnerDialog()
            dialog.items = items
            dialog.mDialogBackground = dialogBackground
            dialog.mCustomAdapter = customAdapter
            return dialog
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (savedInstanceState != null) {
            onSearchableItemClick =
                savedInstanceState.getSerializable(CLICK_LISTENER) as OnSearchableItemClick<Any?>
        }
        val layoutInflater = LayoutInflater.from(activity)
        val rootView = layoutInflater.inflate(R.layout.dialog_layout, null)

        setView(rootView)

        val alertBuilder = AlertDialog.Builder(activity)
        alertBuilder.setView(rootView)
        val title =
            if (mDialogTitle.isNullOrBlank()) getString(R.string.search_dialog_title) else mDialogTitle
        alertBuilder.setTitle(title)

        val dismiss =
            if (mDismissText.isNullOrBlank()) getString(R.string.search_dialog_close) else mDismissText
        alertBuilder.setPositiveButton(dismiss, mDismissListener)

        return alertBuilder.create()
    }

    private var listAdapter: FilterableListAdapter<*, *>? = null

    private fun setView(rootView: View?) {
        if (rootView == null) return

        listAdapter = mCustomAdapter
        mListView = rootView.listView
        mListView?.adapter = listAdapter
        listAdapter?.clickListener = object: OnClickListener {
            override fun onClick(position: Int) {
                println("onItemClick: $position")
                println((mListView?.adapter as? FilterableListAdapter<*, *>)?.currentList?.get(
                    position
                )?.name)
                onSearchableItemClick?.onSearchableItemClicked(
                    (mListView?.adapter as? FilterableListAdapter<*, *>)?.currentList?.get(
                        position
                    )?.name, position
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
            (mListView?.adapter as FilterableListAdapter<*, *>).filter.filter(null)
        } else {
            (mListView?.adapter as FilterableListAdapter<*, *>).filter.filter(query)
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
        if (mDialogBackground != null) {
            dialog?.window?.setBackgroundDrawable(mDialogBackground)
        }
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
}