package pl.utkala.searchablespinner

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

class StringHintArrayAdapter(context: Context, textViewResourceId: Int, objects: List<String>, private val hint: String? = null) : ArrayAdapter<String>(context, textViewResourceId) {
    private val items: List<String>

    init {
        items = if (hint.isNullOrBlank()) {
            objects
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
//        if (items.contains())
//        super.addAll(collection)
    }

    override fun add(`object`: String?) {
        if (!items.contains(`object`))
            super.add(`object`)
    }

    override fun clear() {
        super.clear()
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

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return super.getDropDownView(position, convertView, parent)
    }
}