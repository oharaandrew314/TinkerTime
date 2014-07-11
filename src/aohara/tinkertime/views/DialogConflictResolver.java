package aohara.tinkertime.views;

import javax.swing.JOptionPane;

import aohara.tinkertime.config.Config;
import aohara.tinkertime.controllers.ModStateManager;
import aohara.tinkertime.controllers.files.ConflictResolver;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModStructure.Module;

public class DialogConflictResolver extends ConflictResolver {

	public DialogConflictResolver(Config config, ModStateManager sm) {
		super(config, sm);
	}

	@Override
	public Resolution getResolution(Module module, Mod mod) {
		return (Resolution) JOptionPane.showInputDialog(
			null,
			String.format(
				"The %s module alrerady exists while enabling %s.\n"
				+ "What should be done?", module, mod),
			"Module Conflict",
			JOptionPane.QUESTION_MESSAGE,
			null,
			Resolution.values(),
			Resolution.Overwrite
		);
	}

}
