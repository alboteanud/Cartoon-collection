package com.craiovadata.android.mytoons.model

import com.craiovadata.android.mytoons.R


class Item {
    var title: String? = null
    var videoId: String? = null

    fun getThumbnailUrl(): String {
        // https:\/\/i.ytimg.com\/vi\/TCcmcWdmXCg\/mqdefault.jpg
        return "https://i.ytimg.com/vi/$videoId/mqdefault.jpg"
    }
}