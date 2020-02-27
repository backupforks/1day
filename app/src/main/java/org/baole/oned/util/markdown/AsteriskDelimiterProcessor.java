package org.baole.oned.util.markdown;

import org.commonmark.node.Node;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.node.Text;
import org.commonmark.parser.delimiter.DelimiterProcessor;
import org.commonmark.parser.delimiter.DelimiterRun;


public class AsteriskDelimiterProcessor implements DelimiterProcessor {

    private final char delimiterChar;

    public AsteriskDelimiterProcessor() {
        this.delimiterChar = '*';
    }

    @Override
    public char getOpeningCharacter() {
        return delimiterChar;
    }

    @Override
    public char getClosingCharacter() {
        return delimiterChar;
    }

    @Override
    public int getMinLength() {
        return 1;
    }

    @Override
    public int getDelimiterUse(DelimiterRun opener, DelimiterRun closer) {
        // "multiple of 3" rule for internal delimiter runs
        if ((opener.canClose() || closer.canOpen()) && (opener.originalLength() + closer.originalLength()) % 3 == 0) {
            return 0;
        }
        // calculate actual number of delimiters used from this closer
        if (opener.length() >= 2 && closer.length() >= 2) {
            return 2;
        } else {
            return 1;
        }
    }

    @Override
    public void process(Text opener, Text closer, int delimiterUse) {
        Node emphasis = new StrongEmphasis();

        Node tmp = opener.getNext();
        while (tmp != null && tmp != closer) {
            Node next = tmp.getNext();
            emphasis.appendChild(tmp);
            tmp = next;
        }

        opener.insertAfter(emphasis);
    }
}
