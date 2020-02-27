package org.baole.oned.util.markdown;

import org.baole.oned.util.markdown.Strikethrough;
import org.commonmark.node.Node;
import org.commonmark.renderer.NodeRenderer;

import java.util.Collections;
import java.util.Set;

abstract class StrikethroughNodeRenderer implements NodeRenderer {

    @Override
    public Set<Class<? extends Node>> getNodeTypes() {
        return Collections.<Class<? extends Node>>singleton(Strikethrough.class);
    }
}