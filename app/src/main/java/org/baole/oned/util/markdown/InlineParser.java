package org.baole.oned.util.markdown;

import org.commonmark.parser.delimiter.DelimiterProcessor;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class InlineParser extends org.commonmark.internal.InlineParserImpl {
    public InlineParser(List<DelimiterProcessor> processorList) {
        super(processorList);

        try {
            Field f = org.commonmark.internal.InlineParserImpl.class.getDeclaredField("delimiterProcessors");
            f.setAccessible(true);
            Map<Character, DelimiterProcessor> processors = (Map<Character, DelimiterProcessor>) f.get(this);
            processors.put('*', new AsteriskDelimiterProcessor());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
