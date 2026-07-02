package com.rollcall.android.network

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import okio.Source
import okio.buffer
import okio.source

class ProgressRequestBody(
    private val inputBytes: ByteArray,
    private val contentType: String,
    private val progressCallback: (uploaded: Long, total: Long) -> Unit
) : RequestBody() {
    override fun contentType(): MediaType? {
        return MediaType.parse(contentType)
    }

    override fun contentLength(): Long = inputBytes.size.toLong()

    override fun writeTo(sink: BufferedSink) {
        val total = contentLength()
        var uploaded: Long = 0
        val source: Source = inputBytes.inputStream().source()
        val buf = source.buffer()
        val bufferSize = 8 * 1024L
        var read: Long
        while (true) {
            read = buf.read(sink.buffer(), bufferSize)
            if (read == -1L) break
            sink.flush()
            uploaded += read
            progressCallback(uploaded, total)
        }
        source.close()
    }
}
