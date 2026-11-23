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

package io.github.sonic_liberation.spotify_gql_client

import org.spotube.plugin_interfaces.host_apis.HttpResponse
import io.github.sonic_liberation.spotify_gql_client.gql.CredentialsFromCookieResult
import io.github.sonic_liberation.spotify_gql_client.gql.HomeFeedBaselineSectionData
import io.github.sonic_liberation.spotify_gql_client.gql.HomeUnsupportedSectionData
import io.github.sonic_liberation.spotify_gql_client.gql.SpotifyGQLBaseClient
import io.github.sonic_liberation.spotify_gql_client.gql.browse.BrowseClient
import io.github.sonic_liberation.spotify_gql_client.integration.RealHttpClientAPI
import io.github.sonic_liberation.spotify_gql_client.loader.ResourceLoader
import io.github.sonic_liberation.spotify_gql_client.mocks.MockHTTPClientAPI
import io.github.sonic_liberation.spotify_gql_client.mocks.MockSystemInfoAPI
import io.github.sonic_liberation.spotify_gql_client.mocks.getSpTFromMockCredentials
import io.github.sonic_liberation.spotify_gql_client.mocks.initMockCredentialsFromCookieResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BrowseClientTest {
    class UnitTests {

        private val httpClient = MockHTTPClientAPI()
        private val browse = BrowseClient(httpClient, MockSystemInfoAPI())
        private val loader = ResourceLoader()

        @Test
        fun `BrowseClient home Deserialization from Spotify API response`() = runTest {
            val mockResponseJsonStr = loader.readText("home_200.json")

            httpClient.mockPostResponse = HttpResponse(
                statusCode = 200,
                headers = emptyMap(),
                body = mockResponseJsonStr
            )

            val res = browse.home(
                spT = "SPT",
            )

            assertEquals(true, res != null)
        }

        @Test
        fun `BrowseClient home deserializes HomeFeedBaselineSectionData correctly`() = runTest {
            val jsonPayload = """
            {
                "data": {
                    "home": {
                        "__typename": "HomeResponsePayload",
                        "greeting": {
                            "transformedLabel": "Good morning"
                        },
                        "sectionContainer": {
                            "sections": {
                                "items": [
                                    {
                                        "uri": "spotify:section:0JQ5DAnM3wGh0gz1MXnu4d",
                                        "data": {
                                            "__typename": "HomeFeedBaselineSectionData",
                                            "title": {
                                                "transformedLabel": "Discover Weekly",
                                                "translatedBaseText": "Discover Weekly"
                                            },
                                            "subtitle": {
                                                "transformedLabel": "Your weekly mixtape",
                                                "translatedBaseText": "Your weekly mixtape"
                                            }
                                        },
                                        "sectionItems": {
                                            "items": [],
                                            "totalCount": 0
                                        }
                                    }
                                ],
                                "totalCount": 1
                            }
                        }
                    }
                }
            }
            """.trimIndent()

            httpClient.mockPostResponse = HttpResponse(
                statusCode = 200,
                headers = emptyMap(),
                body = jsonPayload
            )

            val res = browse.home(spT = "SPT")
            assertEquals(true, res != null)
            val section = res?.data?.home?.sectionContainer?.sections?.items?.firstOrNull()
            assertNotNull(section)
            assertTrue(section.data is HomeFeedBaselineSectionData)
            val data = section.data as HomeFeedBaselineSectionData
            assertEquals("Discover Weekly", data.title?.transformedLabel)
            assertEquals("Your weekly mixtape", data.subtitle?.transformedLabel)
        }

        @Test
        fun `BrowseClient home gracefully handles unknown future section types without crashing`() = runTest {
            val jsonPayload = """
            {
                "data": {
                    "home": {
                        "__typename": "HomeResponsePayload",
                        "sectionContainer": {
                            "sections": {
                                "items": [
                                    {
                                        "uri": "spotify:section:unknown123",
                                        "data": {
                                            "__typename": "HomeBrandNewFutureSectionData",
                                            "unknownField": 123
                                        }
                                    }
                                ],
                                "totalCount": 1
                            }
                        }
                    }
                }
            }
            """.trimIndent()

            httpClient.mockPostResponse = HttpResponse(
                statusCode = 200,
                headers = emptyMap(),
                body = jsonPayload
            )

            val res = browse.home(spT = "SPT")
            assertEquals(true, res != null)
            val section = res?.data?.home?.sectionContainer?.sections?.items?.firstOrNull()
            assertNotNull(section)
            assertTrue(section.data is HomeUnsupportedSectionData)
        }

        @Test
        fun `BrowseClient section playlist Deserialization from Spotify API response`() = runTest {
            val mockResponseJsonStr = loader.readText("section_playlist_200.json")

            httpClient.mockPostResponse = HttpResponse(
                statusCode = 200,
                headers = emptyMap(),
                body = mockResponseJsonStr
            )

            val res = browse.section(
                "spotify:section:dummy",
                "SPT"
            )

            assertEquals(true, res != null)
        }

        @Test
        fun `BrowseClient section album Deserialization from Spotify API response`() = runTest {
            val mockResponseJsonStr = loader.readText("section_album_200.json")

            httpClient.mockPostResponse = HttpResponse(
                statusCode = 200,
                headers = emptyMap(),
                body = mockResponseJsonStr
            )

            val res = browse.section(
                "spotify:section:dummy",
                "SPT"
            )

            assertEquals(true, res != null)
        }


        @Test
        fun `BrowseResponse releases Deserialization from Spotify API response`() = runTest {
            val mockResponseJsonStr = loader.readText("releases_200.json")

            httpClient.mockPostResponse = HttpResponse(
                statusCode = 200,
                headers = emptyMap(),
                body = mockResponseJsonStr
            )

            val res = browse.releases()

            assertEquals(true, res != null)
            assertEquals(true, res?.data?.whatsNewFeedItems?.items?.isNotEmpty() == true)
        }
    }

    class IntegrationTests {
        private val httpClient = RealHttpClientAPI()
        private val browse = BrowseClient(httpClient, MockSystemInfoAPI())

        init {
            initMockCredentialsFromCookieResult()
        }

        @Test
        fun `BrowseClient home Deserialization from Spotify API response`() = runTest {
            if (BuildKonfig.ACCESS_TOKEN == "placeholder") return@runTest
            val res = browse.home(
                spT = getSpTFromMockCredentials(),
            )
            assertEquals(true, res != null)
        }

        @Test
        fun `BrowseClient section playlist Deserialization from Spotify API response`() = runTest {
            if (BuildKonfig.ACCESS_TOKEN == "placeholder") return@runTest
            val res = browse.section(
                "spotify:section:0JQ5DAuChZYPe9iDhh2mJz",
                getSpTFromMockCredentials()
            )
            assertEquals(true, res != null)
        }

        @Test
        fun `BrowseClient section album Deserialization from Spotify API response`() = runTest {
            if (BuildKonfig.ACCESS_TOKEN == "placeholder") return@runTest
            val res = browse.section(
                "spotify:section:0JQ5DAnM3wGh0gz1MXnu3B",
                getSpTFromMockCredentials()
            )
            assertEquals(true, res != null)
        }

        @Test
        fun `BrowseResponse releases Deserialization from Spotify API response`() = runTest {
            if (BuildKonfig.ACCESS_TOKEN == "placeholder") return@runTest
            val res = browse.releases()

            assertEquals(true, res != null)
            assertEquals(true, res?.data?.whatsNewFeedItems?.items?.isNotEmpty() == true)
        }

    }
}
