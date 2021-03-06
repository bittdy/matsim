/* *********************************************************************** *
 * project: org.matsim.*
 * F2FFrequencyTask.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2011 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */
package org.matsim.contrib.socnetgen.sna.graph.social.analysis;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.matsim.contrib.socnetgen.sna.graph.Graph;
import org.matsim.contrib.socnetgen.sna.graph.analysis.AnalyzerTask;

import java.io.IOException;
import java.util.Map;

/**
 * @author illenberger
 *
 */
public class F2FFrequencyTask extends AnalyzerTask {

	public static final String KEY = "f2ffreq";
	
	@Override
	public void analyze(Graph graph, Map<String, DescriptiveStatistics> results) {
		DescriptiveStatistics stats = F2FFrequency.getInstance().statistics(graph.getEdges());
		
		printStats(stats, KEY);
		try {
			writeHistograms(stats, KEY, 100, 50);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
