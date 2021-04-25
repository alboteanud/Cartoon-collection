package com.craiovadata.android.mytoons.data

import android.util.Log
import com.craiovadata.android.mytoons.BuildConfig
import com.craiovadata.android.mytoons.model.Item
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber


object ResponseParser {
    private val VIDEO_ID_KEY = "id"
    private val TITLE_KEY = "t"

    fun parseResponse(responseArray: JSONArray?): List<Item> {
        val items = mutableListOf<Item>()
        if (responseArray == null) return items
        val newJsonArray = JSONArray()
        for (i in 0 until responseArray.length()) {
            try {
                val jsonObject = responseArray.getJSONObject(i)
                val item = Item()
                item.videoId = jsonObject.getString(VIDEO_ID_KEY)
                item.title = jsonObject.getString(TITLE_KEY)
                items.add(item)

                    // editing json for the GIST
                    if (item.title!!.length > 70)
                        item.title = item.title!!.substring(0, 70) + "..."
                    val jsonItem = JSONObject()
                    jsonItem.put(TITLE_KEY, item.title)
                    jsonItem.put(VIDEO_ID_KEY, item.videoId)
                    newJsonArray.put(jsonItem)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        Timber.v(newJsonArray.toString())
        return items
    }

    // used to print the simple version of the Youtube API response that we will publish to our Gist
    fun parseResponseFromYoutubeAPI(response: JSONObject?): List<Item> {
        val items = mutableListOf<Item>()
        val jsonArray = response?.getJSONArray("items") ?: return items

        val newJsonArray = JSONArray()

        for (i in 0 until jsonArray.length()) {
            try {
                val jsonObject = jsonArray.getJSONObject(i)
                val item = Item()
                item.videoId = jsonObject.getJSONObject("id").getString("videoId")
                val snippet = jsonObject.getJSONObject("snippet")
                item.title = snippet.getString("title")
//                item.imgUrl = snippet.getJSONObject("thumbnails").getJSONObject("medium").getString("url")
                items.add(item)

                val jsonItem = JSONObject()
                jsonItem.put(VIDEO_ID_KEY, item.videoId)
                jsonItem.put(TITLE_KEY, item.title)
//                jsonItem.put(IMAGE_URL_KEY, item.imgUrl)
                newJsonArray.put(jsonItem)

            } catch (e: JSONException) {
                e.printStackTrace()
            }


        }
        Timber.d("new array to add to Gist: " + newJsonArray.toString())
        return items
    }

}


// https://www.googleapis.com/youtube/v3/search?key=AIzaSyAxhu8irpahSxKIlanX1f3GFuad9zPrnU4&part=snippet&channelId=UC_x5XG1OV2P6uZZ5FSM9Ttw&q=cartoons&type=video