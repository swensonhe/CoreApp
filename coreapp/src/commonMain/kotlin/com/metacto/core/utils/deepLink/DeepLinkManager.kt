package com.metacto.core.utils.deepLink

import com.metacto.core.utils.extensions.matchWithWildcard
import com.metacto.strapikmm.util.Logger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest

internal class DeepLinkManager(
    private val appLogger: Logger,
    private val parsers: Map<String, DeepLinkParser>
) : IDeepLinkManager {
    private val deepLinksFlow = MutableSharedFlow<DeepLink>(replay = 1)

    override fun emitDynamicLink(link: String) {
        validAndParseDeepLink(link)
    }

    override fun emitDeepLink(link: String) {
        validAndParseDeepLink(link)
    }

    private fun validAndParseDeepLink(url: String) {
        try {
            // Match the path with wildcards in parsers
            val deepLinkParser = parsers.keys
                .firstOrNull { url.matchWithWildcard(it) }
                ?.let { parsers[it] }
                ?: return

            // Parse the deep link
            val deepLink = deepLinkParser.parse(url)

            // Emit it if possible
            if (deepLink != null) {
                deepLinksFlow.tryEmit(deepLink)
            }
        } catch (e: Exception) {
            appLogger.log("Failed to parse deep link: $url - Error: ${e.message}")
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun observeDeepLinks(callback: (DeepLink) -> Unit) {
        deepLinksFlow.collectLatest {
            callback.invoke(it)
            deepLinksFlow.resetReplayCache()
        }
    }
}