package aohara.tinkertime.models.pages;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import aohara.tinkertime.controllers.ModManager.CannotAddModException;

public class PageFactory {
	
	public static ModPage loadModPage(Path pagePath, URL pageUrl) throws CannotAddModException{
		
		if (pageUrl.getHost().equals("www.curse.com")){
			return new CurseModPage(loadPage(pagePath), pageUrl);
		}
		throw new IllegalArgumentException("Unsupported Host");
	}
	
	public static Element loadPage(Path path) throws CannotAddModException {
		try {
			return Jsoup.parse(path.toFile(), "UTF-8");
		} catch (IOException e) {
			throw new CannotAddModException();
		}
	}

}
