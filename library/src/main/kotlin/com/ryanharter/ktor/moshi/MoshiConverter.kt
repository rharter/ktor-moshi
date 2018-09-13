package com.ryanharter.ktor.moshi

import com.squareup.moshi.Moshi
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.features.ContentConverter
import io.ktor.features.ContentNegotiation
import io.ktor.features.suitableCharset
import io.ktor.http.ContentType
import io.ktor.http.content.TextContent
import io.ktor.http.withCharset
import io.ktor.request.ApplicationReceiveRequest
import io.ktor.util.pipeline.PipelineContext
import kotlinx.coroutines.experimental.io.ByteReadChannel
import kotlinx.coroutines.experimental.io.jvm.javaio.toInputStream
import okio.Okio
import okio.buffer
import okio.source

class MoshiConverter(private val moshi: Moshi = Moshi.Builder().build()) : ContentConverter {
  override suspend fun convertForReceive(context: PipelineContext<ApplicationReceiveRequest, ApplicationCall>): Any? {
    val request = context.subject
    val channel = request.value as? ByteReadChannel ?: return null
    val source = channel.toInputStream().source().buffer()
    val type = request.type
    return moshi.adapter(type.javaObjectType).fromJson(source)
  }

  override suspend fun convertForSend(context: PipelineContext<Any, ApplicationCall>, contentType: ContentType, value: Any): Any? {
    return TextContent(moshi.adapter(value.javaClass).toJson(value), contentType.withCharset(context.call.suitableCharset()))
  }
}

/**
 * Registers the supplied Moshi instance as a content converter for `application/json`
 * data.
 */
fun ContentNegotiation.Configuration.moshi(moshi: Moshi = Moshi.Builder().build()) {
  val converter = MoshiConverter(moshi)
  register(ContentType.Application.Json, converter)
}

/**
 * Creates a new Moshi instance and registers it as a content converter for
 * `application/json` data.  The supplied block is used to configure the builder.
 */
fun ContentNegotiation.Configuration.moshi(block: Moshi.Builder.() -> Unit) {
  val builder = Moshi.Builder()
  builder.apply(block)
  val converter = MoshiConverter(builder.build())
  register(ContentType.Application.Json, converter)
}
