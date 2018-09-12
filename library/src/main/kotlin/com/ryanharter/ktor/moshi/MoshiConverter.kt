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

class MoshiConverter(private val moshi: Moshi = Moshi.Builder().build()) : ContentConverter {
  override suspend fun convertForReceive(context: PipelineContext<ApplicationReceiveRequest, ApplicationCall>): Any? {
    val request = context.subject
    val channel = request.value as? ByteReadChannel ?: return null
    val source = Okio.buffer(Okio.source(channel.toInputStream()))
    val type = request.type
    return moshi.adapter(type.javaObjectType).fromJson(source)
  }

  override suspend fun convertForSend(context: PipelineContext<Any, ApplicationCall>, contentType: ContentType, value: Any): Any? {
    return TextContent(moshi.adapter(value.javaClass).toJson(value), contentType.withCharset(context.call.suitableCharset()))
  }
}

fun ContentNegotiation.Configuration.moshi(block: Moshi.Builder.() -> Unit) {
  val builder = Moshi.Builder()
  builder.apply(block)
  val converter = MoshiConverter(builder.build())
  register(ContentType.Application.Json, converter)
}
