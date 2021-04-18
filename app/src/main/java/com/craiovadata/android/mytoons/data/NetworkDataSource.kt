package com.craiovadata.android.mytoons.data

import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.*
import com.craiovadata.android.mytoons.model.Item
import com.craiovadata.android.mytoons.model.ResponseParser
import timber.log.Timber


class NetworkDataSource constructor(private val queue: RequestQueue) {
    // LiveData storing the latest downloaded entries
    val downloadedItems = MutableLiveData<List<Item>>()

    fun downloadItems() {

        val url =
            "https://gist.githubusercontent.com/alboteanud/2ca7929f4c90f74dcd84e1db2a52a5b6/raw/toons_items.json"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                Timber.d(response.toString())
                val items = ResponseParser.parseResponse(response)
//                downloadedItems.value = items
//                downloadedItems.postValue(items)
                downloadedItems.apply {
                    value = items
//                 values =   ResponseParser.ITEMS
                    Timber.d("items: " + items.size)
                }

            },
            { error ->
                // TODO: Handle error
                Timber.e(error.message)
            }
        )

        queue.add(jsonObjectRequest)
    }

    companion object {
        private var networkDataSource: NetworkDataSource? = null

        fun getInstance(queue: RequestQueue): NetworkDataSource {
            if (networkDataSource == null) {
                networkDataSource = NetworkDataSource(queue)
            }
            return networkDataSource!!
        }
    }
}