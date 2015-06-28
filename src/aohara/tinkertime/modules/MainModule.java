package aohara.tinkertime.modules;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.jsoup.nodes.Document;

import aohara.common.OS;
import aohara.tinkertime.TinkerConfig;
import aohara.tinkertime.controllers.launcher.GameExecStrategy;
import aohara.tinkertime.controllers.launcher.LinuxExecStrategy;
import aohara.tinkertime.controllers.launcher.OsxExecStrategy;
import aohara.tinkertime.controllers.launcher.WindowsExecStrategy;
import aohara.tinkertime.crawlers.pageLoaders.JsonLoader;
import aohara.tinkertime.crawlers.pageLoaders.PageLoader;
import aohara.tinkertime.crawlers.pageLoaders.WebpageLoader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

public class MainModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(new TypeLiteral<PageLoader<Document>>(){}).to(WebpageLoader.class);
		bind(new TypeLiteral<PageLoader<JsonElement>>(){}).to(JsonLoader.class);
		bind(GameExecStrategy.class).to(getExecStrategyType());
	}

	private Class<? extends GameExecStrategy> getExecStrategyType(){
		switch(OS.getOs()){
		case Windows: return WindowsExecStrategy.class;
		case Linux: return LinuxExecStrategy.class;
		case Osx: return OsxExecStrategy.class;
		default: throw new IllegalStateException();
		}
	}
	
	@Provides
	Gson provideGson(){
		return new GsonBuilder().setPrettyPrinting().create();
	}
	
	@Provides
	Executor provideExecutor(){
		return Executors.newSingleThreadExecutor();
	}
	
	@Provides
	ThreadPoolExecutor provideThreadedExecutor(){
		return (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
	}
	
	@Provides
	TinkerConfig getTinkerConfig(){
		return TinkerConfig.create();
	}
}
