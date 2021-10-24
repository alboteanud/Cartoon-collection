package com.craiovadata.android.mytoons.data

import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.*
import com.craiovadata.android.mytoons.model.Item
import timber.log.Timber


class NetworkDataSource constructor(private val queue: RequestQueue) {
    // LiveData storing the latest downloaded entries
    val downloadedItems = MutableLiveData<List<Item>>()

    fun downloadItems() {
        val url = "https://gist.githubusercontent.com/alboteanud/2ca7929f4c90f74dcd84e1db2a52a5b6/raw/toons_items.json"
        val jsonObjectRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                Timber.d("response: " + response.toString())
                val items = ResponseParser.parseResponse(response)
                downloadedItems.apply {
                    value = items
                    Timber.d("items: " + items.size)
                }
            }, {
                // TODO: Handle error
            }
            // use this to get a simplified response to post to the Gist
//        val url = "https://www.googleapis.com/youtube/v3/search?key=???&q=tom_jerry&type=video&safeSearch=strict&maxResults=50&part=snippet"
//        val jsonObjectRequest =  JsonObjectRequest(
//        val items = ResponseParser.parseResponseFromYoutubeAPI(response)
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
