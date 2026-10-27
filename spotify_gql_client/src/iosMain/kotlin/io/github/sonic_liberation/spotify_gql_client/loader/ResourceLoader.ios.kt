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

package io.github.sonic_liberation.spotify_gql_client.loader

import platform.Foundation.NSBundle
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.stringWithContentsOfFile

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class ResourceLoader actual constructor() {
    actual fun readText(path: String): String {
        val cleanPath = path.removePrefix("/")
        val resourceName = cleanPath.substringBeforeLast(".")
        val resourceType = cleanPath.substringAfterLast(".", "")
        val bundlePath = NSBundle.mainBundle.pathForResource(resourceName, ofType = resourceType)
            ?: throw IllegalArgumentException("Resource not found at: $path")
        return NSString.stringWithContentsOfFile(bundlePath, encoding = NSUTF8StringEncoding, error = null)
            ?: throw IllegalArgumentException("Could not read resource at: $path")
    }
}