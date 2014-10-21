package aohara.tinkertime.workflows;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

import thirdParty.ZipNode;
import aohara.common.workflows.ConflictResolver;
import aohara.common.workflows.WorkflowBuilder;
import aohara.common.workflows.tasks.UnzipTask;
import aohara.common.workflows.tasks.gen.GenFactory;
import aohara.common.workflows.tasks.gen.PathGen;
import aohara.common.workflows.tasks.gen.URLGen;
import aohara.tinkertime.TinkerConfig;
import aohara.tinkertime.controllers.ModStateManager;
import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.crawlers.CrawlerFactory;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.crawlers.ModCrawler;
import aohara.tinkertime.models.FileUpdateListener;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModStructure;
import aohara.tinkertime.models.UpdateableFile;
import aohara.tinkertime.workflows.tasks.CacheCrawlerPageTask;
import aohara.tinkertime.workflows.tasks.CheckForUpdateTask;
import aohara.tinkertime.workflows.tasks.MarkModEnabledTask;
import aohara.tinkertime.workflows.tasks.MarkModUpdatedTask;
import aohara.tinkertime.workflows.tasks.NotfiyUpdateAvailableTask;

public class ModWorkflowBuilder extends WorkflowBuilder {
	
	private final CrawlerFactory factory = new CrawlerFactory();
	
	public ModWorkflowBuilder(String workflowName) {
		super(workflowName);
	}
	
	/**
	 * Notifies the listeners if an update is available for the given file
	 */
	public void checkForUpdates(UpdateableFile file, FileUpdateListener... listeners) throws IOException, UnsupportedHostException {	
		Crawler<?> crawler = factory.getCrawler(file.getPageUrl());
		
		addTask(new CacheCrawlerPageTask(crawler));
		addTask(new CheckForUpdateTask(crawler, file.getUpdatedOn(), file.getNewestFileName()));
		addTask(new NotfiyUpdateAvailableTask(crawler, listeners));
	}
	
	/**
	 * Notifies the listener of the file's latest version available.
	 */
	public void checkLatestVersion(UpdateableFile file, FileUpdateListener... listeners) throws UnsupportedHostException {
		Crawler<?> crawler = factory.getCrawler(file.getPageUrl());
		addTask(new CacheCrawlerPageTask(crawler));
		addTask(new NotfiyUpdateAvailableTask(crawler, listeners));
	}
	
	/**
	 * Downloads the file if a newer version is available.
	 */
	public void downloadFileIfNewer(UpdateableFile file, PathGen dest) throws UnsupportedHostException, IOException{
		Crawler<?> crawler = factory.getCrawler(file.getPageUrl());
		
		addTask(new CacheCrawlerPageTask(crawler));
		addTask(new CheckForUpdateTask(crawler, file.getUpdatedOn(), file.getNewestFileName()));
		tempDownload(downloadLinkGen(crawler), dest);
	}
	
	/**
	 * Downloads the latest version of the file
	 */
	public void downloadFile(Crawler<?> crawler, PathGen dest) throws IOException {
		addTask(new CacheCrawlerPageTask(crawler));
		tempDownload(downloadLinkGen(crawler), dest);
	}
	
	/**
	 * Downloads the latest version of the mod referenced by the URL.
	 */
	public void downloadMod(URL pageUrl, TinkerConfig config, ModStateManager sm) throws IOException, UnsupportedHostException {
		ModCrawler<?> crawler = factory.getModCrawler(pageUrl);
		downloadFile(crawler, modZipPathGen(crawler, config));
		addTask(new MarkModUpdatedTask(sm, crawler));
		download(modImageLinkGen(crawler), modImageCacheGen(crawler, config));
		addTask(new MarkModUpdatedTask(sm, crawler));
	}
	
	public void deleteMod(Mod mod, TinkerConfig config, ModStateManager sm) throws IOException {
		if (mod.isEnabled()){
			for (ZipNode module : ModStructure.inspectArchive(config, mod).getModules()){
				delete(GenFactory.fromPath(config.getGameDataPath().resolve(module.getName())));
			}
		}
		delete( modZipPathGen(mod, config));
		addTask(MarkModUpdatedTask.notifyDeletion(sm, mod));
	}
	
	public void disableMod(Mod mod, TinkerConfig config, ModStateManager sm) throws IOException{
		for (ZipNode module : ModStructure.inspectArchive(config, mod).getModules()){
			
			if (!isDependency(module, config, sm)){
				delete(GenFactory.fromPath(config.getGameDataPath().resolve(module.getName())));
			}
		}
		addTask(new MarkModEnabledTask(mod, sm, false));
	}
	
	public void enableMod(Mod mod, TinkerConfig config, ModStateManager sm, ConflictResolver cr) throws IOException{
		ModStructure structure = ModStructure.inspectArchive(config, mod);
		for (ZipNode module : structure.getModules()){
			addTask(new UnzipTask(config.getGameDataPath(), module, cr));
		}
		addTask(new MarkModEnabledTask(mod, sm, true));
	}
	
	// helpers
	
	private boolean isDependency(ZipNode module, TinkerConfig config, ModStateManager sm) throws IOException{
		int numDependencies = 0;
		for (Mod mod : sm.getMods()){
			try {
				if (ModStructure.inspectArchive(config, mod).usesModule(module)){
					numDependencies++;
				}
			} catch (FileNotFoundException ex){}
		}
		return numDependencies > 1;
	}
	
	// Generators
	
	public static URLGen downloadLinkGen(final Crawler<?> crawler){
		return new URLGen(){
			@Override
			public URI getURI() throws URISyntaxException {
				return getURL().toURI();
			}

			@Override
			public URL getURL() {
				try {
					return crawler.getDownloadLink();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}
	
	private static PathGen modZipPathGen(final Mod mod, final TinkerConfig config){
		return new PathGen(){
			@Override
			public URI getURI() throws URISyntaxException {
				return getPath().toUri();
			}

			@Override
			public Path getPath() {
				return mod.getCachedZipPath(config);
			}
		};
	}
	
	private static PathGen modZipPathGen(final ModCrawler<?> crawler, final TinkerConfig config) throws IOException{
		return new PathGen(){
			@Override
			public URI getURI() throws URISyntaxException {
				return getPath().toUri();
			}

			@Override
			public Path getPath() {
				try {
					return crawler.createMod().getCachedZipPath(config);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}
	
	private static URLGen modImageLinkGen(final ModCrawler<?> crawler){
		return new URLGen(){
			@Override
			public URI getURI() throws URISyntaxException {
				URL url = getURL();
				return url != null ? url.toURI() : null;
			}

			@Override
			public URL getURL() {
				try {
					return crawler.getImageUrl();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}
	
	private static PathGen modImageCacheGen(final ModCrawler<?> crawler, final TinkerConfig config){
		return new PathGen(){
			@Override
			public URI getURI() throws URISyntaxException {
				return getPath().toUri();
			}

			@Override
			public Path getPath() {
				try {
					return crawler.createMod().getCachedImagePath(config);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}
}
