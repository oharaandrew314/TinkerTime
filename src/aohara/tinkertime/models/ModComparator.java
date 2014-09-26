package aohara.tinkertime.models;

import java.util.Comparator;

public class ModComparator implements Comparator<Mod>{

	@Override
	public int compare(Mod o1, Mod o2) {
		return o1.getName().compareTo(o2.getName());
	}

}
