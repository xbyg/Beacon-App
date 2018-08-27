package com.xbyg.beacon.service.request

import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

abstract class GZIPHtmlRequest<T>(val charset: Charset) : Request<T>() {
    // Unknown issue:
    // It is necessary to compress the bytes array before decompressing these bytes, otherwise 'Java java.io.IOException: Not in GZIP format' would occur
    protected fun decompress(htmlBytes: ByteArray): String = GZIPInputStream(compress(htmlBytes).inputStream()).reader(charset).readText()

    private fun compress(htmlBytes: ByteArray): ByteArray = ByteArrayOutputStream().apply {
        GZIPOutputStream(this).apply {
            write(htmlBytes)
            close()
        }
    }.toByteArray()
}