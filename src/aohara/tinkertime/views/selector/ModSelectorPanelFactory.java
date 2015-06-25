package aohara.tinkertime.views.selector;

import java.awt.Dimension;

import aohara.common.views.selectorPanel.SelectorPanelBuilder;
import aohara.tinkertime.controllers.ModManager;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.views.ModListCellRenderer;
import aohara.tinkertime.views.ModView;
import aohara.tinkertime.views.menus.MenuFactory;

import com.google.inject.Inject;

public class ModSelectorPanelFactory {
	
	private final ModListCellRenderer renderer;
	private final MenuFactory menuFactory;
	private final ModView modView;
	private final ModManager mm;
	
	@Inject
	ModSelectorPanelFactory(ModListCellRenderer renderer, MenuFactory menuFactory, ModView modView, ModManager mm){
		this.renderer = renderer;
		this.menuFactory = menuFactory;
		this.modView = modView;
		this.mm = mm;
	}
	
	public ModSelectorPanelController create(Dimension panelSize, double dividerRatio){
		SelectorPanelBuilder<Mod> spBuilder = new SelectorPanelBuilder<>(panelSize, dividerRatio);
		spBuilder.setListCellRenderer(renderer);
		spBuilder.setContextMenu(menuFactory.createPopupMenu());
		
		ModListListener listListener = new ModListListener(mm);
		spBuilder.addKeyListener(listListener);
		spBuilder.addSelectionListener(listListener);
		
		return new ModSelectorPanelController(spBuilder.createSelectorPanel(modView), mm);
	}
}
