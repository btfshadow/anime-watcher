package brunodles.animacurse

import bruno.animewatcher.explorer.AnimeExplorer
import com.greghaskins.spectrum.Spectrum
import com.greghaskins.spectrum.Spectrum.describe
import com.greghaskins.spectrum.Spectrum.it
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.runner.RunWith
import java.util.*

@RunWith(Spectrum::class)
class AnimaCurseFactoryTest {

    companion object {
        val VALID_URLS = Arrays.asList("https://animacurse.moe/?p=713", "animacurse.moe?p=123",
                "http://animacurse.moe?p=321")
    }

    init {

        describe("The AnimaCurse Factory") {
            val factory = AnimaCurseFactory

            VALID_URLS.forEach { url ->
                it("should be able to decode de url \"$url\"") {
                    assertTrue(factory.isEpisode(url))
                }
            }

            it("should not decode the category url") {
                assertFalse(factory.isEpisode("animacurse.moe/?cat=123"))
            }

            it("should return a Explorer") {
                assertTrue(factory.episode("https://animacurse.moe/?p=713") is AnimeExplorer)
            }
        }
    }

}