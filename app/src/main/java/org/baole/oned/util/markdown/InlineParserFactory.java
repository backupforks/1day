package org.baole.oned.util.markdown;

import org.commonmark.parser.InlineParserContext;

public class InlineParserFactory implements org.commonmark.parser.InlineParserFactory {
    @Override
    public org.commonmark.parser.InlineParser create(InlineParserContext inlineParserContext) {
        return new InlineParser(inlineParserContext.getCustomDelimiterProcessors());
    }
}
