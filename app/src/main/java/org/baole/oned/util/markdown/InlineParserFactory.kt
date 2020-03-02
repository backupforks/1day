package org.baole.oned.util.markdown

import org.commonmark.parser.InlineParserContext
import org.commonmark.parser.InlineParserFactory

class InlineParserFactory : InlineParserFactory {
    override fun create(inlineParserContext: InlineParserContext): org.commonmark.parser.InlineParser {
        return InlineParser(inlineParserContext.customDelimiterProcessors)
    }
}