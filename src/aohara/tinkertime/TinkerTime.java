package aohara.tinkertime;

import java.io.IOException;

import aohara.tinkertime.controllers.ModManager;
import aohara.tinkertime.controllers.ModManager.ModException;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModPage;

public class TinkerTime {

	public static void main(String[] args) {
		String url = "http://www.curse.com/ksp-mods/kerbal/220221-mechjeb";
			
		ModPage page = null;
		try {
			page = new ModPage(url);
			System.out.println(page.getName());
			System.out.println(page.getUpdatedOn());
			System.out.println(page.getCreator());
			System.out.println(page.getNewestFile());
			System.out.println(page.getDownloadLink());
			System.out.println(page.getImageUrl());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ModManager manager = new ModManager();
		Mod mod = new Mod(page);
		try {
			manager.downloadMod(mod);
		} catch (IOException | ModException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			for (String file : manager.getFiles(mod)){
				System.out.println(file);
			}
		} catch (ModException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			manager.enableMod(mod);
			manager.disableMod(mod);
		} catch (IOException | ModException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
