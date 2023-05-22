@file:Suppress("unused")

package com.ryanharter.ktor.moshi

import com.squareup.moshi.Moshi
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import io.ktor.http.withCharset
import io.ktor.serialization.ContentConverter
import io.ktor.server.plugins.contentnegotiation.ContentNegotiationConfig
import io.ktor.util.reflect.TypeInfo
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.buffer
import okio.source
import java.nio.charset.Charset

class MoshiConverter(private val moshi: Moshi = Moshi.Builder().build()) : ContentConverter {
    override suspend fun deserialize(charset: Charset, typeInfo: TypeInfo, content: ByteReadChannel): Any? {
        return withContext(Dispatchers.IO) {
            moshi.adapter(typeInfo.type.javaObjectType).fromJson(content.toInputStream().source().buffer())
        }
    }

    override suspend fun serializeNullable(
        contentType: ContentType,
        charset: Charset,
        typeInfo: TypeInfo,
        value: Any?,
    ) = TextContent(
        moshi.adapter(
            value?.javaClass
                ?: Any::class.java,
        ).nullSafe().toJson(value),
        contentType.withCharset(charset),
    )
}

/**
 * Registers the supplied Moshi instance as a content converter for `application/json`
 * data.
 */
fun ContentNegotiationConfig.moshi(moshi: Moshi = Moshi.Builder().build()) {
    val converter = MoshiConverter(moshi)
    register(ContentType.Application.Json, converter)
}

/**
 * Creates a new Moshi instance and registers it as a content converter for
 * `application/json` data.  The supplied block is used to configure the builder.
 */
fun ContentNegotiationConfig.moshi(block: Moshi.Builder.() -> Unit) {
    val builder = Moshi.Builder()
    builder.apply(block)
    val converter = MoshiConverter(builder.build())
    register(ContentType.Application.Json, converter)
}
