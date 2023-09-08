package pl.utkala.searchablespinnerdemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.Filter
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import pl.utkala.searchablespinner.FilterableListAdapter
import pl.utkala.searchablespinner.ItemSpinner

class StartWithArrayAdapter :
    FilterableListAdapter<ItemSpinner, StartWithArrayAdapter.StartViewHolder>(itemCallback) {

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val result = FilterResults()
                if (!constraint.isNullOrBlank()) {
                    synchronized(this) {
                        val filteredItems = ArrayList<ItemSpinner>()
                        for (i in (differ.currentList.indices)) {
                            if (differ.currentList[i].filter.startsWith(
                                    constraint,
                                    ignoreCase = true
                                )
                            )
                                filteredItems.add(differ.currentList[i])
                        }
                        result.count = filteredItems.size
                        result.values = filteredItems
                    }
                } else {
                    synchronized(this) {
                        result.values = differ.currentList
                        result.count = differ.currentList.size
                    }
                }
                return result
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results?.values != null) {
                    submitList(results.values as List<SimpleItem>)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StartViewHolder {
        return StartViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_spinner_dropdown_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: StartViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class StartViewHolder(view: View) : ViewHolder(view) {
        fun bind(item: ItemSpinner) {
            itemView.findViewById<CheckedTextView>(android.R.id.text1).text = item.name
            itemView.setOnClickListener {
                clickListener?.onClick(
                    if (differ.currentList.indexOf(item) != -1) differ.currentList.indexOf(item) else
                        absoluteAdapterPosition
                )
            }
        }
    }

    companion object {
        val itemCallback = object : DiffUtil.ItemCallback<ItemSpinner>() {
            override fun areItemsTheSame(oldItem: ItemSpinner, newItem: ItemSpinner): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ItemSpinner, newItem: ItemSpinner): Boolean {
                return oldItem == newItem
            }
        }
    }
}