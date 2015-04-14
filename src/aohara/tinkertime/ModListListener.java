package aohara.tinkertime;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import aohara.common.config.views.Dialogs;
import aohara.common.selectorPanel.SelectorListListener;
import aohara.tinkertime.ModManager.NoModSelectedException;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.views.TinkerDialogs;

class ModListListener implements KeyListener, SelectorListListener<Mod> {
	
	private final ModManager mm;
	
	ModListListener(ModManager mm){
		this.mm = mm;
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
				if (TinkerDialogs.confirmDeleteMod(evt.getComponent(), selectedMod.name)){
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
			Dialogs.errorDialog(evt.getComponent(), ex);
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
