package com.craiovadata.android.mytoons.data

import android.content.Context
import android.util.SparseArray
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import timber.log.Timber

class MyYoutubeExtractor(context: Context, val listener: (downloadUrl: String?) -> Unit) :
    YouTubeExtractor(context) {

    override fun onExtractionComplete(ytFiles: SparseArray<YtFile>?, vMeta: VideoMeta) {
        var downloadUrl: String? = null
        val itag = 22
        if (ytFiles != null && ytFiles[itag] !=null) {
            downloadUrl = ytFiles[itag].url
        }
        Timber.d(downloadUrl)
        listener(downloadUrl)

    }
}