package org.baole.oned.util

import android.os.Build
import android.text.Html
import android.text.Spanned
import org.baole.oned.util.markdown.InlineParserFactory
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

object TextUtil {
    val parser = Parser.builder()
            .inlineParserFactory(InlineParserFactory())
            .build()
    val renderer = HtmlRenderer.builder().build()

    fun markdown2text(markdown: String): Spanned {
        val html = renderer.render(parser.parse(markdown))
        return if (Build.VERSION.SDK_INT >= 24) {
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(html)
        }
    }
}