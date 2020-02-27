package org.baole.oned.util.markdown;

import org.commonmark.node.Emphasis;
import org.commonmark.node.Paragraph;
import org.commonmark.node.SoftLineBreak;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.renderer.html.CoreHtmlNodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlWriter;

public class HtmlNodeRender extends CoreHtmlNodeRenderer {
    private final HtmlWriter html;

    public HtmlNodeRender(HtmlNodeRendererContext context) {
        super(context);
        this.html = context.getWriter();

    }

    @Override
    public void visit(Paragraph paragraph) {
        visitChildren(paragraph);
    }

    @Override
    public void visit(StrongEmphasis strongEmphasis) {
        html.tag("b");
        visitChildren(strongEmphasis);
        html.tag("/b");
    }


    @Override
    public void visit(Emphasis emphasis) {
        html.tag("i");
        visitChildren(emphasis);
        html.tag("/i");
    }

    @Override
    public void visit(SoftLineBreak softLineBreak) {
        html.tag("br/");
        html.line();
    }
}
