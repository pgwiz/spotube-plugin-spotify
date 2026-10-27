/*
 * Copyright (C) 2026 Sonic Liberation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.sonic_liberation.spotify_gql_client.integration

import org.spotube.plugin_interfaces.host_apis.HttpClientAPI
import org.spotube.plugin_interfaces.host_apis.HttpResponse
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.takeFrom

class RealHttpClientAPI: HttpClientAPI {
    val httpClient = HttpClient(CIO)

    override suspend fun request(
        method: org.spotube.plugin_interfaces.host_apis.HttpMethod,
        url: String,
        requestHeaders: Map<String, String>?,
        body: String?
    ): HttpResponse {
        val res = httpClient.request {
            this.method = when (method) {
                org.spotube.plugin_interfaces.host_apis.HttpMethod.Get -> io.ktor.http.HttpMethod.Get
                org.spotube.plugin_interfaces.host_apis.HttpMethod.Post -> io.ktor.http.HttpMethod.Post
                org.spotube.plugin_interfaces.host_apis.HttpMethod.Put -> io.ktor.http.HttpMethod.Put
                org.spotube.plugin_interfaces.host_apis.HttpMethod.Delete -> io.ktor.http.HttpMethod.Delete
                org.spotube.plugin_interfaces.host_apis.HttpMethod.Patch -> io.ktor.http.HttpMethod.Patch
                org.spotube.plugin_interfaces.host_apis.HttpMethod.Head -> io.ktor.http.HttpMethod.Head
                org.spotube.plugin_interfaces.host_apis.HttpMethod.Options -> io.ktor.http.HttpMethod.Options
            }
            this.url {
                takeFrom(url)
            }
            requestHeaders?.forEach { (key, value) ->
                headers.append(key, value)
            }
            body?.let { setBody(it) }
        }

        return HttpResponse(
            statusCode = res.status.value,
            headers = res.headers.entries().associate { it.key to it.value.joinToString(",") },
            body = res.bodyAsText()
        )
    }
}