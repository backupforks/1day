package org.baole.oned.util.markdown;

import org.commonmark.node.CustomNode;
import org.commonmark.node.Delimited;
import org.commonmark.node.Node;
import org.commonmark.node.Visitor;

public class Strikethrough extends CustomNode implements Delimited {

    private String delimiter;

    public Strikethrough() {

    }

    public Strikethrough(String delimiter) {
        this.delimiter = delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    public String getOpeningDelimiter() {
        return delimiter;
    }

    @Override
    public String getClosingDelimiter() {
        return delimiter;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
