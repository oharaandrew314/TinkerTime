package io.andrewohara.tinkertime.views.modSelector;

import io.andrewohara.common.views.selectorPanel.SelectorPanelBuilder;
import io.andrewohara.tinkertime.controllers.ModManager;
import io.andrewohara.tinkertime.models.Mod;
import io.andrewohara.tinkertime.views.menus.MenuFactory;
import io.andrewohara.tinkertime.views.modView.ModView;

import java.awt.Dimension;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ModSelectorPanelFactory {

	private static final Dimension PANEL_SIZE = new Dimension(800, 600);
	private static final double DIVIDER_RATIO = 0.35;

	private final ModListCellRenderer renderer;
	private final MenuFactory menuFactory;
	private final ModView modView;
	private final ModManager mm;
	private final ModListListener listListener;

	private ModSelectorPanelController cached;

	@Inject
	ModSelectorPanelFactory(ModListCellRenderer renderer, MenuFactory menuFactory, ModView modView, ModManager mm, ModListListener listListener){
		this.renderer = renderer;
		this.menuFactory = menuFactory;
		this.modView = modView;
		this.mm = mm;
		this.listListener = listListener;
	}

	public ModSelectorPanelController get(){
		if (cached == null){
			SelectorPanelBuilder<Mod> spBuilder = new SelectorPanelBuilder<>(PANEL_SIZE, DIVIDER_RATIO);
			spBuilder.setListCellRenderer(renderer);
			spBuilder.setContextMenu(menuFactory.createPopupMenu());

			spBuilder.addKeyListener(listListener);
			spBuilder.addSelectionListener(listListener);

			cached = new ModSelectorPanelController(spBuilder.createSelectorPanel(modView), mm);
		}
		return cached;
	}
}
