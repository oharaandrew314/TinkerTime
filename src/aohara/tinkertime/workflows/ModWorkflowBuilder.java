package aohara.tinkertime.workflows;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

import aohara.common.workflows.ConflictResolver;
import aohara.common.workflows.Workflow;
import aohara.common.workflows.WorkflowBuilder;
import aohara.common.workflows.tasks.UnzipTask;
import aohara.common.workflows.tasks.gen.GenFactory;
import aohara.common.workflows.tasks.gen.PathGen;
import aohara.common.workflows.tasks.gen.URLGen;
import aohara.tinkertime.Config;
import aohara.tinkertime.content.ArchiveInspector;
import aohara.tinkertime.controllers.ModStateManager;
import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.crawlers.CrawlerFactory;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.crawlers.ModCrawler;
import aohara.tinkertime.models.FileUpdateListener;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModStructure;
import aohara.tinkertime.models.Module;
import aohara.tinkertime.models.UpdateableFile;
import aohara.tinkertime.workflows.tasks.CacheCrawlerPageTask;
import aohara.tinkertime.workflows.tasks.CheckForUpdateTask;
import aohara.tinkertime.workflows.tasks.MarkModEnabledTask;
import aohara.tinkertime.workflows.tasks.MarkModUpdatedTask;
import aohara.tinkertime.workflows.tasks.NotfiyUpdateAvailableTask;

public class ModWorkflowBuilder {
	
	private static final CrawlerFactory factory = new CrawlerFactory();
	
	/**
	 * Notifies the listeners if an update is available for the given file
	 */
	public static void checkForUpdates(
			Workflow workflow, UpdateableFile file, FileUpdateListener... listeners
	) throws IOException, UnsupportedHostException {	
		Crawler<?> crawler = factory.getCrawler(file.getPageUrl());
		
		workflow.addTask(new CacheCrawlerPageTask(workflow, crawler));
		workflow.addTask(new CheckForUpdateTask(workflow, crawler, file.getUpdatedOn(), file.getNewestFileName()));
		workflow.addTask(new NotfiyUpdateAvailableTask(workflow, crawler, listeners));
	}
	
	/**
	 * Notifies the listener of the file's latest version available.
	 */
	public static void checkLatestVersion(
			Workflow workflow, UpdateableFile file, FileUpdateListener... listeners
	) throws UnsupportedHostException {
		Crawler<?> crawler = factory.getCrawler(file.getPageUrl());
		
		workflow.addTask(new CacheCrawlerPageTask(workflow, crawler));
		workflow.addTask(new NotfiyUpdateAvailableTask(workflow, crawler, listeners));
	}
	
	/**
	 * Downloads the file if a newer version is available.
	 */
	public static void downloadFileIfNewer(
		Workflow workflow, UpdateableFile file, PathGen dest
	) throws UnsupportedHostException, IOException{
		Crawler<?> crawler = factory.getCrawler(file.getPageUrl());
		
		workflow.addTask(new CacheCrawlerPageTask(workflow, crawler));
		workflow.addTask(new CheckForUpdateTask(workflow, crawler, file.getUpdatedOn(), file.getNewestFileName()));
		WorkflowBuilder.tempDownload(workflow, downloadLinkGen(crawler), dest);
	}
	
	/**
	 * Downloads the latest version of the file
	 */
	public static void downloadFile(Workflow workflow, Crawler<?> crawler, PathGen dest) throws IOException {
		workflow.addTask(new CacheCrawlerPageTask(workflow, crawler));
		WorkflowBuilder.tempDownload(workflow, downloadLinkGen(crawler), dest);
	}
	
	/**
	 * Downloads the latest version of the mod referenced by the URL.
	 */
	public static void downloadMod(Workflow workflow, URL pageUrl, Config config, ModStateManager sm) throws IOException, UnsupportedHostException {
		ModCrawler<?> crawler = factory.getModCrawler(pageUrl);
		downloadFile(workflow, crawler, modZipPathGen(crawler, config));
		workflow.addTask(new MarkModUpdatedTask(workflow, sm, crawler));
		WorkflowBuilder.download(workflow, modImageLinkGen(crawler), modImageCacheGen(crawler, config));
	}
	
	public static void deleteMod(Workflow workflow, Mod mod, Config config, ModStateManager sm) throws IOException {
		if (mod.isEnabled()){
			for (Module module : ArchiveInspector.inspectArchive(config, mod).getModules()){
				WorkflowBuilder.delete(workflow, GenFactory.fromPath(config.getGameDataPath().resolve(module.getName())));
			}
		}
		WorkflowBuilder.delete(workflow, modZipPathGen(mod, config));
		workflow.addTask(new MarkModUpdatedTask(workflow, sm, mod));
	}
	
	public static void disableMod(Workflow workflow, Mod mod, Config config, ModStateManager sm) throws IOException{
		for (Module module : ArchiveInspector.inspectArchive(config, mod).getModules()){
			
			if (!isDependency(module, config, sm)){
				WorkflowBuilder.delete(workflow, GenFactory.fromPath(config.getGameDataPath().resolve(module.getName())));
			}
		}
		workflow.addTask(new MarkModEnabledTask(workflow, mod, sm, false));
	}
	
	public static void enableMod(Workflow workflow, Mod mod, Config config, ModStateManager sm, ConflictResolver cr) throws IOException{
		ModStructure structure = ArchiveInspector.inspectArchive(config, mod);
		for (Module module : structure.getModules()){
			workflow.addTask(new UnzipTask(
				workflow, structure.zipPath,
				config.getGameDataPath(),
				module.getContent(),
				cr));
		}
		workflow.addTask(new MarkModEnabledTask(workflow, mod, sm, true));
	}
	
	// helpers
	
	private static boolean isDependency(Module module, Config config, ModStateManager sm) throws IOException{
		int numDependencies = 0;
		for (Mod mod : sm.getMods()){
			if (ArchiveInspector.inspectArchive(config, mod).usesModule(module)){
				numDependencies++;
			}
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
	
	private static PathGen modZipPathGen(final Mod mod, final Config config){
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
	
	private static PathGen modZipPathGen(final ModCrawler<?> crawler, final Config config) throws IOException{
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
				return getURL().toURI();
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
	
	private static PathGen modImageCacheGen(final ModCrawler<?> crawler, final Config config){
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
