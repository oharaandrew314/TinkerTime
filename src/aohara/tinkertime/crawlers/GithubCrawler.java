package aohara.tinkertime.crawlers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import aohara.tinkertime.crawlers.pageLoaders.PageLoader;

/**
 * Crawler for gathering Mod Data from a Github Project.
 * 
 * @author Andrew O'Hara
 */
public class GithubCrawler extends Crawler<JsonElement> {
	
	private static final String RELEASES = "releases";

	public GithubCrawler(URL url, PageLoader<JsonElement> pageLoader) {
		super(url, pageLoader);
	}

    public URL getApiUrl(){
        try {
            return new URL("https://api.github.com/repos" + getPageUrl().getPath());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

	@Override
	public JsonElement getPage(URL url) throws IOException {
		/* Groom the Github Releases URL before getting it */
		
		List<String> paths = Arrays.asList(url.getPath().split("/"));
		if (paths.contains(RELEASES)){
			// If path contains releases, but isn't last element, strip url
			if (!paths.get(paths.size() - 1).equals(RELEASES)){
				url = new URL(url.toString().split(RELEASES)[0]);
			}
		}
		
		// If path does not contain releases, try appending it to end 
		else {
			url = new URL(url.toString() + "/releases");
		}
		
		return super.getPage(url);
	}
	
	private JsonObject getLatestReleaseElement() throws IOException {
        JsonElement doc = getPage(getApiUrl());
        for(int index = 0; index < doc.getAsJsonArray().size(); index++) {
            JsonObject jsonObject = (JsonObject) doc.getAsJsonArray().get(index);
            if(jsonObject.get("zipball_url") != null)
                return jsonObject;
        }
		return null;
	}

	@Override
	protected Collection<Asset> getNewestAssets() throws IOException {
		// Get List of Asset Elements
        JsonObject assetsElement = getLatestReleaseElement();

		// Get File Names from Elements
		Collection<Asset> assets = new LinkedList<>();

        JsonArray jsonArray = assetsElement.get("assets").getAsJsonArray();

        for(int index = 0; index < jsonArray.size(); index++) {
            JsonObject jsonObject = jsonArray.get(index).getAsJsonObject();
            assets.add(new Asset(
                    jsonObject.get("name").getAsString(),
                    new URL( jsonObject.get("browser_download_url").getAsString())
            ));
        }


		return assets;
	}

	@Override
	public Date getUpdatedOn() throws IOException {
        JsonObject assetsElement = getLatestReleaseElement();
		String dateStr = assetsElement.get("published_at").getAsString();

		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
		} catch (ParseException e) {
			throw new IOException(e);
		}
	}

	@Override
	public URL getImageUrl() throws IOException {
		return null;
	}

	@Override
	public String getName() throws IOException {
        JsonObject assetsElement = getLatestReleaseElement();
        Pattern pattern = Pattern.compile("https://api.github.com/repos/[A-Za-z-0-9]*/([A-Za-z-0-9]*)/.*");

        Matcher matcher = pattern.matcher(assetsElement.get("url").getAsString());

        if(matcher.matches()) {
            return matcher.group(1);
        }

        return assetsElement.get("name").getAsString();
	}

	@Override
	public String getCreator() throws IOException {
        JsonObject assetsElement = getLatestReleaseElement();
        return assetsElement.get("author").getAsJsonObject().get("login").getAsString();
	}

	@Override
	public String getSupportedVersion() throws IOException {
		return null;  // Not Supported by Github
	}

	@Override
	public String generateId() {
		try { // Chop off releases from path
			URI uri = getApiUrl().toURI();
			if (getApiUrl().getPath().endsWith("releases")){
				uri = uri.getPath().endsWith("/") ? uri.resolve("..") : uri.resolve(".");
			}
			String[] names = uri.getPath().split("/");
			return names[names.length - 1];
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}	
	}
}
