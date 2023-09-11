package pl.utkala.searchablespinner

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatCheckedTextView

class StringHintArrayAdapter(
    context: Context,
    textViewResourceId: Int,
    objects: List<String>,
    private val hint: String? = null,
    private val colorHint: Int? = null
) : ArrayAdapter<String>(context, textViewResourceId) {
    private val items: MutableList<String>

    init {
        items = if (hint.isNullOrBlank()) {
            objects.toMutableList()
        } else {
            val hintedList = objects.toMutableList()
            hintedList.add(0, hint)
            hintedList
        }
        addAll(items)
    }

    override fun addAll(collection: Collection<String>) {
        collection.forEach {
            add(it)
        }
    }


    override fun add(`object`: String?) {
        if (!items.contains(`object`)) {
            items.add(`object`.orEmpty())
            super.add(`object`)
        }
    }

    override fun clear() {
        super.clear()
        items.clear()
        if (hint != null)
            add(hint)
    }

    override fun isEnabled(position: Int): Boolean {
        return if (hint.isNullOrBlank()) {
            super.isEnabled(position)
        } else {
            position != 0
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        return super.getView(position, convertView, parent).also {
            if (position == 0 && !hint.isNullOrBlank() && colorHint != null)
                (it as? AppCompatCheckedTextView)?.setTextColor(colorHint)
        }
    }


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return super.getDropDownView(position, convertView, parent)
    }
}