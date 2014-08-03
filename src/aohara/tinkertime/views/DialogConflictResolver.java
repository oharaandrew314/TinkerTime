package aohara.tinkertime.views;

import java.nio.file.Path;

import javax.swing.JOptionPane;

import aohara.common.workflows.ConflictResolver;

public class DialogConflictResolver extends ConflictResolver {

	@Override
	public Resolution getResolution(Path conflictPath) {
		return (Resolution) JOptionPane.showInputDialog(
			null,
			String.format(
				"%s already exists.  What should be done?",
				conflictPath
			),
			"Module Conflict",
			JOptionPane.QUESTION_MESSAGE,
			null,
			Resolution.values(),
			Resolution.Overwrite
		);
	}

}
