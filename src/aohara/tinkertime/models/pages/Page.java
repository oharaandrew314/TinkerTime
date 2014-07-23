package aohara.tinkertime.models.pages;

import java.net.URL;

public abstract class Page {
	
	protected final URL pageUrl;
	
	protected Page(URL pageUrl){
		this.pageUrl = pageUrl;
	}
	
	public URL getPageUrl(){
		return pageUrl;
	}
}
