package com.craiovadata.android.mytoons

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.craiovadata.android.mytoons.data.MyRepository

class MyViewModelFactory(var mRepository: MyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ListViewModel(mRepository) as T
    }
}