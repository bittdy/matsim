package playground.gleich.av_bus.analysis;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.pt.transitSchedule.api.TransitLine;
import org.matsim.pt.transitSchedule.api.TransitRoute;
import org.matsim.pt.transitSchedule.api.TransitScheduleReader;

import playground.gleich.av_bus.FilePaths;

public class PtVehicleOperationCostAnalysis {
	
	Scenario scenario;

	private final String networkFile = FilePaths.PATH_BASE_DIRECTORY + FilePaths.PATH_NETWORK_BERLIN_100PCT_ACCESS_LOOPS;
	private final String scheduleFile = FilePaths.PATH_BASE_DIRECTORY + FilePaths.PATH_TRANSIT_SCHEDULE_BERLIN_100PCT;
	private static final String resultFileRoutes = FilePaths.PATH_BASE_DIRECTORY + "data/analysis/operationCost/operationCost_transitSchedule.100pct.base.perRoute.csv";
	private static final String resultFileLines = FilePaths.PATH_BASE_DIRECTORY + "data/analysis/operationCost/operationCost_transitSchedule.100pct.base.perLine.csv";
	
	private final double costPerM = 0.0023;
	
	Map<Id<TransitRoute>, Id<TransitLine>> route2line = new HashMap<>();
	Map<Id<TransitRoute>, Double> route2length = new HashMap<>();
	Map<Id<TransitRoute>, Integer> route2numDepartures = new HashMap<>();
	Map<Id<TransitRoute>, Double> route2cost = new HashMap<>();
	
	private String sep = ";";
	private BufferedWriter bw;

	public static void main(String[] args) {
		PtVehicleOperationCostAnalysis analysis = new PtVehicleOperationCostAnalysis();
		analysis.run();
		analysis.writeResultsPerTransitRoute(resultFileRoutes);
		analysis.writeResultsPerTransitLine(resultFileLines);
	}


	public void run() {
		scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
		new MatsimNetworkReader(scenario.getNetwork()).readFile(networkFile);
		new TransitScheduleReader(scenario).readFile(scheduleFile);
		
		for (TransitLine line: scenario.getTransitSchedule().getTransitLines().values()) {
				processTransitLine(line);
		}
	}
	
	private void processTransitLine(TransitLine line) {
		for (TransitRoute route: line.getRoutes().values()) {
			double length = calculateLength(route);
			int numDepartures = route.getDepartures().size();
			double cost = length * numDepartures * costPerM;
			route2line.put(route.getId(), line.getId());
			route2length.put(route.getId(), length);
			route2numDepartures.put(route.getId(), numDepartures);
			route2cost.put(route.getId(), cost);
		}
	}

	/** 
	 * Sums up the whole link length no matter where exactly on this link the TransitStop is located.
	 * However, at last on the way back, the transit vehicle has to drive the rest of the link.
	 * So the length per TransitRoute might be slightly inexact, however as long as every vehicle travels back
	 * on another TransitRoute to the same start TransitStop, the total length travelled should be the same
	 * as if the exact position of the stop on the link would have been taken into account.
	 * 
	 * NetworkRoute.getDistance() and NetworkRoute.getTravelCost() produce only Nans.
	 */
	private double calculateLength(TransitRoute route) {
		/* Start and end link ids are missing in route.getRoute().getLinkIds()! Add manually */
		double length = scenario.getNetwork().getLinks().get(route.getRoute().getStartLinkId()).getLength();
		for (Id<Link> link: route.getRoute().getLinkIds()) {
			length += scenario.getNetwork().getLinks().get(link).getLength();
		}
		length += scenario.getNetwork().getLinks().get(route.getRoute().getEndLinkId()).getLength();
		return length;
	}
	
	public void writeResultsPerTransitRoute(String file) {
		try {
			bw = IOUtils.getBufferedWriter(file);
			// write header
			bw.write("transitLineId" + sep + "transitRouteId" + sep + "length" + sep + "numDepartures" + sep +
					"cost");
			bw.newLine();
			for (TransitLine line: scenario.getTransitSchedule().getTransitLines().values()) {
				for (TransitRoute route: line.getRoutes().values()) {
					bw.write(route2line.get(route.getId()).toString() + sep + route.getId().toString() + sep +
							route2length.get(route.getId()) + sep + route2numDepartures.get(route.getId()) +
							sep + route2cost.get(route.getId()));
					bw.newLine();
				}
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("could not write");
		}
	}
	
	public void writeResultsPerTransitLine(String file) {
		try {
			bw = IOUtils.getBufferedWriter(file);
			// write header
			bw.write("transitLineId" + sep + "maxLength" + sep + "sumDepartures" + sep +
					"sumDistanceDriven" + sep + "sumCost");
			bw.newLine();

			for (TransitLine line: scenario.getTransitSchedule().getTransitLines().values()) {
				double maxLength = 0;
				int sumDepartures = 0;
				double sumDistanceDriven = 0;
				double sumCost = 0;
				for (TransitRoute route: line.getRoutes().values()) {
					if (route2length.get(route.getId()) > maxLength) {
						maxLength = route2length.get(route.getId());
					}
					sumDepartures += route2numDepartures.get(route.getId());
					sumDistanceDriven += route2numDepartures.get(route.getId()) * route2length.get(route.getId());
					sumCost += route2cost.get(route.getId());
				}
				bw.write(line.getId().toString() + sep + maxLength + sep + sumDepartures +
						sep + sumDistanceDriven + sep + sumCost);
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("could not write");
		}
	}
}
