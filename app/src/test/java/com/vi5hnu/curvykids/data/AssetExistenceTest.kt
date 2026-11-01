package com.vi5hnu.curvykids.data

import com.vi5hnu.curvykids.data.content.ANIMALS
import com.vi5hnu.curvykids.data.content.BODY_PARTS
import com.vi5hnu.curvykids.data.content.COUNT_ITEMS
import com.vi5hnu.curvykids.data.content.EMOTIONS
import com.vi5hnu.curvykids.data.content.FRUITS_VEGGIES
import com.vi5hnu.curvykids.data.content.GAME_PICTURES
import com.vi5hnu.curvykids.data.content.HINDI_CONSONANTS
import com.vi5hnu.curvykids.data.content.HINDI_VOWELS
import com.vi5hnu.curvykids.data.content.AssetPath
import com.vi5hnu.curvykids.data.content.KidAssets
import com.vi5hnu.curvykids.data.content.MONTHS
import com.vi5hnu.curvykids.data.content.OPPOSITES
import com.vi5hnu.curvykids.data.content.PHONICS_DATA
import com.vi5hnu.curvykids.data.content.PICTURE_SPECS
import com.vi5hnu.curvykids.data.content.Phonics
import com.vi5hnu.curvykids.data.content.RHYME_GROUPS
import com.vi5hnu.curvykids.data.content.SEASONS_LIST
import com.vi5hnu.curvykids.data.content.STORIES
import com.vi5hnu.curvykids.data.content.VEHICLES
import com.vi5hnu.curvykids.data.content.WORD_LIST
import com.vi5hnu.curvykids.data.content.Level
import com.vi5hnu.curvykids.data.content.CRAYON_BOX
import com.vi5hnu.curvykids.data.content.TOPIC_ART
import com.vi5hnu.curvykids.data.content.TopicArt
import com.vi5hnu.curvykids.data.content.World
import com.vi5hnu.curvykids.ui.kit.tools.ToolArt
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Every illustration the app references must actually ship.
 *
 * The content lists carry several hundred asset paths, and a wrong one is invisible at build
 * time — it surfaces as a blank tile on one topic, at runtime, on a device. Checking the paths
 * against the real `assets/` tree turns that into a failing test instead.
 */
class AssetExistenceTest {

    /** Unit tests run with the module directory as the working directory. */
    private val assetsRoot = File("src/main/assets")

    private fun assertAllExist(label: String, paths: Collection<AssetPath>) {
        assertTrue("$label referenced no assets — the test is not checking anything", paths.isNotEmpty())
        val missing = paths.distinct().filterNot { File(assetsRoot, it.path).isFile }
        assertTrue("$label references ${missing.size} missing asset(s):\n" + missing.joinToString("\n") { it.path },
            missing.isEmpty())
    }

    @Test
    fun `activity content assets all exist`() {
        assertAllExist("COUNT_ITEMS", COUNT_ITEMS)
        assertAllExist("GAME_PICTURES", GAME_PICTURES)
        assertAllExist("WORD_LIST", WORD_LIST.map { it.svg })
        assertAllExist("ANIMALS", ANIMALS.map { it.svg })
        assertAllExist("BODY_PARTS", BODY_PARTS.map { it.svg })
        assertAllExist("MONTHS", MONTHS.map { it.svg })
        assertAllExist("EMOTIONS", EMOTIONS.map { it.svg })
        assertAllExist("VEHICLES", VEHICLES.map { it.svg })
        assertAllExist("FRUITS_VEGGIES", FRUITS_VEGGIES.map { it.svg })
        assertAllExist("SEASONS", SEASONS_LIST.map { it.svg } + SEASONS_LIST.flatMap { it.weatherSvgs })
        assertAllExist("OPPOSITES", OPPOSITES.flatMap { listOf(it.svg1, it.svg2) })
        assertAllExist("PHONICS_DATA", PHONICS_DATA.map { it.svg })
        assertAllExist("RHYME_GROUPS", RHYME_GROUPS.map { it.svg } + RHYME_GROUPS.flatMap { g -> g.rhymes.map { it.second } })
        assertAllExist("HINDI", (HINDI_VOWELS + HINDI_CONSONANTS).map { it.svg })
    }

    @Test
    fun `picture topic assets all exist`() {
        assertAllExist("PICTURE_SPECS", PICTURE_SPECS.values.flatMap { spec -> spec.items.map { it.svg } })
    }

    @Test
    fun `story assets all exist`() {
        assertAllExist("STORIES", STORIES.map { it.svg })
    }

    @Test
    fun `phonics has a picture for every latin character the app teaches`() {
        val characters = Level.entries
            .filter { it.script == com.vi5hnu.curvykids.data.content.Script.LATIN }
            .flatMap { it.characters }
        val assets = characters.map { ch ->
            requireNotNull(Phonics.assetFor(ch)) { "No phonics picture for '$ch'" }
        }
        assertAllExist("Phonics", assets)
    }

    @Test
    fun `every catalog category directory exists`() {
        val missing = KidAssets.ALL_CATEGORIES.filterNot { File(assetsRoot, "art/$it").isDirectory }
        assertTrue("Catalog lists categories with no directory: $missing", missing.isEmpty())
    }

    @Test
    fun `tile pictures and drawing tool pictures all exist`() {
        val tiles = (World.entries.map { it.art } + TOPIC_ART.values)
            .filterIsInstance<TopicArt.LetterPicture>().map { it.asset }
        assertAllExist("Letter tiles", tiles)
        assertAllExist("Crayons", CRAYON_BOX.map { it.art })
        assertAllExist("Tools", ToolArt.entries.map { it.asset })
    }
}
