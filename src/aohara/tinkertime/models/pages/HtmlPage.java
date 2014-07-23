package aohara.tinkertime.models.pages;

import java.net.URL;

import org.jsoup.nodes.Element;

public class HtmlPage extends Page {
	
	protected Element doc;
	
	public HtmlPage(URL pageUrl, Element element){
		super(pageUrl);
		this.doc = element;
	}
}
