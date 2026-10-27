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

package io.github.sonic_liberation.spotify_gql_client.gql.tracks

import org.spotube.plugin_interfaces.host_apis.HttpClientAPI
import io.github.sonic_liberation.spotify_gql_client.gql.GQLRequestExtensions
import io.github.sonic_liberation.spotify_gql_client.gql.PersistedQuery
import io.github.sonic_liberation.spotify_gql_client.gql.SpotifyGQLBaseClient
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

class TracksClient(client: HttpClientAPI): SpotifyGQLBaseClient(client) {

    suspend fun saveTracks(uris: List<String>): TrackMutationResponse? {
        val request = AddToLibraryRequest(
            variables = AddToLibraryVariables(uris = uris),
            operationName = "addToLibrary",
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "a3c1ff58e6a36fec5fe1e3a193dc95d9071d96b9ba53c5ba9c1494fb1ee73915",
                ),
            ),
        )

        return post<TrackMutationResponse, AddToLibraryRequest>(SPOTIFY_GQL_ENDPOINT, request)
    }

    suspend fun unsaveTracks(uris: List<String>): TrackMutationResponse? {
        val request = ApplyCurationsRequest(
            variables = ApplyCurationsVariables(
                input = ApplyCurationsInput(
                    curations = listOf(
                        Curation(
                            contextUri = "spotify:collection:tracks",
                            curationType = "UNCURATE",
                        ),
                    ),
                    itemUris = uris,
                ),
            ),
            operationName = "applyCurations",
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "05b739a3a73091c213385233b9d3ed8a857c2ca29d2eebadb3d04ed12e288697",
                ),
            ),
        )

        return post<TrackMutationResponse, ApplyCurationsRequest>(SPOTIFY_GQL_ENDPOINT, request)
    }

    suspend fun areTracksSaved(uris: List<String>): TrackCurationResponse? {
        val request = IsCuratedRequest(
            variables = IsCuratedVariables(uris = uris),
            operationName = "isCurated",
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "e4ed1f91a2cc5415befedb85acf8671dc1a4bf3ca1a5b945a6386101a22e28a6",
                ),
            ),
        )

        return post<TrackCurationResponse, IsCuratedRequest>(SPOTIFY_GQL_ENDPOINT, request)
    }

    suspend fun radioPlaylist(id: String): TrackRadioPlaylistResponse? {
        val url = restUrl("/inspiredby-mix/v2/seed_to_playlist/spotify:track:$id?response-format=json")

        return get<TrackRadioPlaylistResponse>(url)
    }

    suspend fun getTrack(uri: String): GetTrackResponse? {
        val request = GetTrackRequest(
            variables = GetTrackVariables(uri = uri),
            operationName = "getTrack",
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "612585ae06ba435ad26369870deaae23b5c8800a256cd8a57e08eddc25a37294",
                ),
            ),
        )

        return runCatching {
            post<GetTrackResponse, GetTrackRequest>(SPOTIFY_GQL_ENDPOINT, request)
        }.getOrNull()
    }

    suspend fun getTrackEmbed(id: String): SpotifyEmbedTrack? {
        val cleanId = id.removePrefix("spotify:track:")
        val url = "https://open.spotify.com/embed/track/$cleanId"
        val headers = mapOf(
            "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36",
            "Accept" to "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"
        )
        val response = runCatching {
            client.request(
                method = org.spotube.plugin_interfaces.host_apis.HttpMethod.Get,
                url = url,
                requestHeaders = headers,
                body = null
            )
        }.getOrNull() ?: return null

        if (response.statusCode >= 400 || response.body.isNullOrBlank()) {
            return null
        }

        return parseTrackFromEmbedHtml(response.body!!, cleanId)
    }

    fun parseTrackFromEmbedHtml(html: String, defaultId: String): SpotifyEmbedTrack? {
        val scriptStart = html.indexOf("""<script id="__NEXT_DATA__"""")
        if (scriptStart == -1) return null
        val jsonStart = html.indexOf('>', scriptStart)
        if (jsonStart == -1) return null
        val jsonEnd = html.indexOf("</script>", jsonStart)
        if (jsonEnd == -1) return null
        val jsonText = html.substring(jsonStart + 1, jsonEnd)

        val jsonElement = runCatching { json.parseToJsonElement(jsonText) }.getOrNull() ?: return null
        val entity = jsonElement.jsonObject["props"]
            ?.jsonObject?.get("pageProps")
            ?.jsonObject?.get("state")
            ?.jsonObject?.get("data")
            ?.jsonObject?.get("entity")
            ?.jsonObject ?: return null

        val trackId = entity["id"]?.jsonPrimitive?.contentOrNull ?: defaultId
        val title = entity["name"]?.jsonPrimitive?.contentOrNull
            ?: entity["title"]?.jsonPrimitive?.contentOrNull
            ?: ""
        val durationMs = entity["duration"]?.jsonPrimitive?.longOrNull ?: 0L
        val isExplicit = entity["isExplicit"]?.jsonPrimitive?.booleanOrNull ?: false
        val releaseDate = entity["releaseDate"]?.jsonObject?.get("isoString")?.jsonPrimitive?.contentOrNull

        val artists = entity["artists"]?.jsonArray?.mapNotNull { item ->
            val obj = item.jsonObject
            val name = obj["name"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
            val uri = obj["uri"]?.jsonPrimitive?.contentOrNull ?: "spotify:artist:"
            val artistId = uri.removePrefix("spotify:artist:")
            SpotifyEmbedArtist(name = name, uri = uri, id = artistId)
        }.orEmpty()

        val previewUrl = entity["audioPreview"]?.jsonObject?.get("url")?.jsonPrimitive?.contentOrNull

        val images = buildList {
            entity["visualIdentity"]?.jsonObject?.get("image")?.jsonArray?.forEach { img ->
                val obj = img.jsonObject
                val imgUrl = obj["url"]?.jsonPrimitive?.contentOrNull
                if (!imgUrl.isNullOrBlank()) {
                    val w = obj["maxWidth"]?.jsonPrimitive?.intOrNull
                    val h = obj["maxHeight"]?.jsonPrimitive?.intOrNull
                    add(SpotifyEmbedImage(url = imgUrl, width = w, height = h))
                }
            }
            if (isEmpty()) {
                entity["coverArt"]?.jsonObject?.get("sources")?.jsonArray?.forEach { src ->
                    val obj = src.jsonObject
                    val imgUrl = obj["url"]?.jsonPrimitive?.contentOrNull
                    if (!imgUrl.isNullOrBlank()) {
                        val w = obj["width"]?.jsonPrimitive?.intOrNull
                        val h = obj["height"]?.jsonPrimitive?.intOrNull
                        add(SpotifyEmbedImage(url = imgUrl, width = w, height = h))
                    }
                }
            }
        }

        return SpotifyEmbedTrack(
            id = trackId,
            title = title,
            artists = artists,
            durationMs = durationMs,
            previewUrl = previewUrl,
            images = images,
            releaseDate = releaseDate,
            isExplicit = isExplicit
        )
    }
}
