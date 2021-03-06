package brunodles.animesproject

import bruno.animewatcher.explorer.CurrentEpisode
import bruno.animewatcher.explorer.EpisodeLink
import bruno.animewatcher.explorer.UrlFetcher.Companion.fetchUrl
import java.net.URL

class AnimesProjectExplorer(private val url: String) {

    private val doc = fetchUrl(url)
    private val host by lazy {
        val u = URL(url)
        "${u.protocol}://${u.host}"
    }
    private val currentEpisode = findCurrentEpisode()

    fun currentEpisode(): CurrentEpisode = currentEpisode

    private fun findCurrentEpisode(): CurrentEpisode {
        val iframe = doc.select(".video-placeholder iframe").attr("src")
        val src = fetchUrl(host + iframe).select(".video-js").attr("src")
        val title = doc.select(".serie-pagina-subheader span").text()
        return CurrentEpisode(src, title)
    }

    fun nextEpisodes(): List<EpisodeLink> {
        return doc.select(".exibir-pagina-listagem a").map {
            EpisodeLink(AnimesProjectFinder.HOST + it.attr("href"), it.text(), null)
        }.toList().subList(3, 5)
    }
}
