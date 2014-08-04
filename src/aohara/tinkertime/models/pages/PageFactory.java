package aohara.tinkertime.models.pages;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Path;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
	
	public static FilePage loadFilePage(URL pageUrl) throws IOException {
		if (pageUrl.getHost().equals("www.ksp.sarbian.com")){
			try (Reader reader = new InputStreamReader(pageUrl.openStream())){
				JsonObject obj = new JsonParser().parse(reader).getAsJsonObject();
				return ModuleManagerPage.loadPage(obj);
			}
		}
		throw new IllegalArgumentException("Unsupported Host");
	}

}
