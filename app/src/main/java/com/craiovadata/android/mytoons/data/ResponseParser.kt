package com.craiovadata.android.mytoons.data

import com.craiovadata.android.mytoons.model.Item
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber


object ResponseParser {

    fun parseResponse(responseArray: JSONArray?): List<Item> {
        val items = mutableListOf<Item>()
        if (responseArray == null) return items
        for (i in 0 until responseArray.length()) {
            try {
                val jsonObject = responseArray.getJSONObject(i)
                val item = Item()
                item.videoId = jsonObject.getString("id")
                item.title = jsonObject.getString("title")
                item.thumbUrl = jsonObject.getString("url")
                items.add(item)
            } catch (e: JSONException){
                e.printStackTrace()
            }
        }
        return items
    }

    fun parseResponse_toSimplify(response: JSONObject?): List<Item> {
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
                item.thumbUrl = snippet.getJSONObject("thumbnails").getJSONObject("medium").getString("url")

                items.add(item)

                val jsonItem = JSONObject()
                jsonItem.put("id", jsonObject.getJSONObject("id").getString("videoId"))
                jsonItem.put("title", snippet.getString("title"))
                jsonItem.put("url", snippet.getJSONObject("thumbnails").getJSONObject("medium").getString("url"))
                newJsonArray.put(jsonItem)

            } catch (e: JSONException){
                e.printStackTrace()
            }


        }
        Timber.d("new array to add to Gist: " + newJsonArray.toString())
        return items
    }

}


// https://www.googleapis.com/youtube/v3/search?key=AIzaSyAxhu8irpahSxKIlanX1f3GFuad9zPrnU4&part=snippet&channelId=UC_x5XG1OV2P6uZZ5FSM9Ttw&q=cartoons&type=video