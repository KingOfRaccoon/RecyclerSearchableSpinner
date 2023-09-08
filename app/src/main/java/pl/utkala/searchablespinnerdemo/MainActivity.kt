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

package pl.utkala.searchablespinnerdemo

import android.os.Bundle
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.searchableSpinner
import pl.utkala.searchablespinner.ItemSpinner
import pl.utkala.searchablespinner.OnSearchableItemClick
import pl.utkala.searchablespinner.StringHintArrayAdapter

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = StartWithArrayAdapter()

        val users = listOf(
            "John Doe",
            "Ellen Cunningham",
            "Carmen Walker",
            "Mike Walker",
            "Edgar Bourn",
            "Richard Robson",
            "Ralph Poe",
            "Max Smith"
        )
        searchableSpinner.showHint = true
        searchableSpinner.adapter = StringHintArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf(),
            "Select Item"
        ).apply {
            clear()
            println("count")
            println(users.size)
            addAll(users + users)
            println(count)
            notifyDataSetChanged()
        }

        searchableSpinner.onSearchableItemClick = object : OnSearchableItemClick<ItemSpinner?> {
            override fun onSearchableItemClicked(item: ItemSpinner?, position: Int) {
                println(item)
                println(item?.javaClass)
                println("position: $position")
                if (position > 0) {
                    searchableSpinner.setSelection(position, true)
                } else {
                    searchableSpinner.setSelection(Spinner.INVALID_POSITION)
                }
            }
        }

        searchableSpinner.setCustomDialogAdapter(adapter)
        adapter.differ.submitList(users.mapIndexed { index, s ->
            SimpleItem(s.split(" ").first(), s, index)
        } + users.mapIndexed { index, s ->
            TestItem(s.split(" ").first(), "$s test", index)
        })
    }
}