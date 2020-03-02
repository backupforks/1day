package org.baole.oned.util.markdown

import org.commonmark.internal.InlineParserImpl
import org.commonmark.parser.delimiter.DelimiterProcessor

class InlineParser(processorList: List<DelimiterProcessor?>?) : InlineParserImpl(processorList) {
    init {
        try {
            val f = InlineParserImpl::class.java.getDeclaredField("delimiterProcessors")
            f.isAccessible = true
            val processors = f[this] as MutableMap<Char, DelimiterProcessor>
            processors['*'] = AsteriskDelimiterProcessor()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}