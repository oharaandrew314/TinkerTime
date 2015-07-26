package io.andrewohara.tinkertime.views.modSelector;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.google.inject.Inject;

import io.andrewohara.common.views.Dialogs;
import io.andrewohara.common.views.selectorPanel.SelectorListListener;
import io.andrewohara.tinkertime.controllers.ModManager;
import io.andrewohara.tinkertime.controllers.ModManager.NoModSelectedException;
import io.andrewohara.tinkertime.models.mod.Mod;
import io.andrewohara.tinkertime.views.TinkerDialogs;

public class ModListListener implements KeyListener, SelectorListListener<Mod> {

	private final ModManager mm;
	private final Dialogs dialogs;

	@Inject
	ModListListener(ModManager mm, Dialogs dialogs){
		this.mm = mm;
		this.dialogs = dialogs;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// Do Nothing
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// Do Nothing
	}

	@Override
	public void keyTyped(KeyEvent evt) {
		try{
			switch(evt.getKeyChar()){
			case KeyEvent.VK_DELETE:
				Mod selectedMod = mm.getSelectedMod();
				if (TinkerDialogs.confirmDeleteMod(evt.getComponent(), selectedMod.getName())){
					mm.deleteMod(selectedMod);
				}
				break;
			case KeyEvent.VK_ENTER:
				mm.toggleMod(mm.getSelectedMod());
				break;
			}
		} catch (NoModSelectedException ex){
			// Do nothing
		} catch(Exception ex){
			dialogs.errorDialog(evt.getComponent(), ex);
		}
	}

	@Override
	public void elementClicked(Mod mod, int numTimes) {
		if (numTimes == 2){
			mm.toggleMod(mod);
		}
	}

	@Override
	public void elementSelected(Mod element) {
		mm.selectMod(element);
	}

}
