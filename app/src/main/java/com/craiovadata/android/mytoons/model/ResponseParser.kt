package com.craiovadata.android.mytoons.model

import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 * TODO: Replace all uses of this class before publishing your app.
 */
object ResponseParser {

    /**
     * An array of sample (dummy) items.
     */
    val ITEMS: MutableList<Item> = ArrayList()

    /**
     * A map of sample (dummy) items, by ID.
     */
    val ITEM_MAP: MutableMap<String, Item> = HashMap()

    private val COUNT = 25

    init {
        // Add some sample items.
        for (i in 1..COUNT) {
            addItem(createDummyItem(i))
        }
    }

    private fun addItem(item: Item) {
        ITEMS.add(item)
        ITEM_MAP.put(item.etag, item)
    }

    private fun createDummyItem(position: Int): Item {
//        return Item(position.toString(), "Item " + position, makeDetails(position))
        return Item()
    }

    private fun makeDetails(position: Int): String {
        val builder = StringBuilder()
        builder.append("Details about Item: ").append(position)
        for (i in 0..position - 1) {
            builder.append("\nMore details information here.")
        }
        return builder.toString()
    }

    fun parseResponse(response: JSONObject?): List<Item> {
        val items = mutableListOf<Item>()
        val jsonArray = response?.getJSONArray("items") ?: return items
        for (i in 0 until jsonArray.length()) {
            try {
                val jsonObject = jsonArray.getJSONObject(i)
                val item = Item()
                item.etag = jsonObject.getString("etag")
                item.videoId = jsonObject.getJSONObject("id").getString("videoId")
                val snippet = jsonObject.getJSONObject("snippet")
                item.title = snippet.getString("title")
                item.description = snippet.getString("description")
                item.thumbUrl = snippet.getJSONObject("thumbnails").getJSONObject("default").getString("url")

                items.add(item)
            } catch (e: JSONException){
                e.printStackTrace()
            }

        }
        return items
    }

}


// https://www.googleapis.com/youtube/v3/search?key=AIzaSyAxhu8irpahSxKIlanX1f3GFuad9zPrnU4&part=snippet&channelId=UC_x5XG1OV2P6uZZ5FSM9Ttw&q=cartoons&type=video