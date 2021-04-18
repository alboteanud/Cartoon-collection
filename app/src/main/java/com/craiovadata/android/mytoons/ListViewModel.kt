package com.craiovadata.android.mytoons

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.craiovadata.android.mytoons.data.MyRepository
import com.craiovadata.android.mytoons.model.Item


class ListViewModel(repo: MyRepository) : ViewModel() {
    private val _items = repo.loadItems()
    val items: LiveData<List<Item>> = _items
}
