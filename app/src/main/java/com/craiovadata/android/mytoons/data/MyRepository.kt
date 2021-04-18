package com.craiovadata.android.mytoons.data

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.android.volley.toolbox.Volley
import com.craiovadata.android.mytoons.model.Item
import timber.log.Timber
import java.util.concurrent.Executors

class MyRepository constructor(
    private var networkDataSource: NetworkDataSource,
    private var appExecutor: AppExecutor
) {

    fun loadItems(): MutableLiveData<List<Item>> {
        fetchLatestItems()
        return networkDataSource.downloadedItems
    }

    private fun fetchLatestItems() {
        if (isFetchNewsNeeded()) {
            Timber.d("Fetching the latest news")
            appExecutor.execute(networkDataSource::downloadItems)
        }
    }

    private fun isFetchNewsNeeded(): Boolean {
        // TODO check timestamp - latest news added to DB
        return true
    }

    companion object {
        private var myRepository: MyRepository? = null

        // static method to create instance of Repository class
        fun getInstance(context: Context): MyRepository {
            if (myRepository == null) {
                val requestQueue = Volley.newRequestQueue(context.applicationContext)
                val networkDataSource = NetworkDataSource.getInstance(requestQueue)
                val singleThreadExecutor = Executors.newSingleThreadExecutor()
                val appExecutor = AppExecutor(singleThreadExecutor)
                myRepository = MyRepository(networkDataSource, appExecutor)
            }
            return myRepository!!
        }
    }

}