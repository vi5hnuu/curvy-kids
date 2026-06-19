package com.vi5hnu.curvykids.data.content

import androidx.compose.ui.graphics.Color
import com.vi5hnu.curvykids.ui.theme.Aqua
import com.vi5hnu.curvykids.ui.theme.Coral
import com.vi5hnu.curvykids.ui.theme.Green
import com.vi5hnu.curvykids.ui.theme.Sun
import com.vi5hnu.curvykids.ui.theme.TintAqua
import com.vi5hnu.curvykids.ui.theme.TintCoral
import com.vi5hnu.curvykids.ui.theme.TintGreen
import com.vi5hnu.curvykids.ui.theme.TintSun

/**
 * A short Hindi story for the Stories topic. [lines] are individual Devanagari sentences so the
 * reader can read them aloud one-by-one and highlight the line currently being spoken; [moral] is
 * the closing "सीख". Each story carries its own accent [color]/[tint] for its card and header.
 */
data class Story(
    val id: String,
    val title: String,     // Devanagari title
    val subtitle: String,  // short English hint, shown under the title on the list card
    val emoji: String,
    val color: Color,
    val tint: Color,
    val lines: List<String>,
    val moral: String,
)

/** Classic Hindi children's stories, kept short and simple for early readers/listeners. */
val STORIES: List<Story> = listOf(
    Story(
        id = "thirsty-crow",
        title = "प्यासा कौआ",
        subtitle = "The Thirsty Crow",
        emoji = "🐦",
        color = Coral,
        tint = TintCoral,
        lines = listOf(
            "एक गरम दिन था।",
            "एक कौआ बहुत प्यासा था।",
            "वह इधर-उधर पानी ढूँढने लगा।",
            "उसे एक घड़ा दिखा, जिसमें थोड़ा-सा पानी था।",
            "कौए ने एक-एक करके घड़े में कंकड़ डाले।",
            "पानी ऊपर आ गया और कौए ने पानी पी लिया।",
        ),
        moral = "मेहनत और सूझ-बूझ से हर मुश्किल हल हो जाती है।",
    ),
    Story(
        id = "lion-mouse",
        title = "शेर और चूहा",
        subtitle = "The Lion and the Mouse",
        emoji = "🦁",
        color = Sun,
        tint = TintSun,
        lines = listOf(
            "एक जंगल में एक शेर सो रहा था।",
            "एक छोटा चूहा उसके ऊपर कूदने लगा।",
            "शेर जाग गया और चूहे को पकड़ लिया।",
            "चूहे ने कहा, मुझे छोड़ दो, मैं भी तुम्हारी मदद करूँगा।",
            "शेर हँसा और उसे छोड़ दिया।",
            "एक दिन शेर जाल में फँस गया।",
            "चूहे ने जाल काट दिया और शेर को बचा लिया।",
        ),
        moral = "छोटा हो या बड़ा, हर किसी की मदद काम आती है।",
    ),
    Story(
        id = "hare-tortoise",
        title = "खरगोश और कछुआ",
        subtitle = "The Hare and the Tortoise",
        emoji = "🐢",
        color = Green,
        tint = TintGreen,
        lines = listOf(
            "एक खरगोश को अपनी तेज़ी पर घमंड था।",
            "उसने कछुए को दौड़ के लिए ललकारा।",
            "दौड़ शुरू हुई और खरगोश बहुत आगे निकल गया।",
            "खरगोश रास्ते में पेड़ के नीचे सो गया।",
            "कछुआ धीरे-धीरे चलता रहा।",
            "कछुआ पहले पहुँच गया और दौड़ जीत गया।",
        ),
        moral = "लगातार मेहनत करने वाला ही जीतता है।",
    ),
    Story(
        id = "unity-strength",
        title = "एकता में बल",
        subtitle = "Unity is Strength",
        emoji = "🌾",
        color = Aqua,
        tint = TintAqua,
        lines = listOf(
            "एक किसान के चार बेटे थे।",
            "वे हमेशा आपस में लड़ते रहते थे।",
            "किसान ने उन्हें लकड़ियों का एक गट्ठर दिया।",
            "अकेली-अकेली लकड़ी सबने आसानी से तोड़ दी।",
            "पर पूरा गट्ठर कोई नहीं तोड़ पाया।",
            "किसान ने कहा, मिलकर रहोगे तो मज़बूत रहोगे।",
        ),
        moral = "एकता में ही बल है।",
    ),
)
