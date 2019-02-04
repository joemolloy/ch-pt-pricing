package ch.ethz.matsim.ch_pt_utils.cost.sbb.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class InterchangeReader {
	public Collection<Long> read(File path) throws NumberFormatException, IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
		Set<Long> interchangeIds = new HashSet<>();

		String line = null;
		while ((line = reader.readLine()) != null) {
			interchangeIds.add(Long.parseLong(line));
		}

		reader.close();
		return interchangeIds;
	}
}
