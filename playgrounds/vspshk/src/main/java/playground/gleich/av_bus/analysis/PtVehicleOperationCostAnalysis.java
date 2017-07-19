package playground.gleich.av_bus.analysis;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.matsim.vehicles.VehicleReaderV1;

import playground.gleich.av_bus.FilePaths;

public class PtVehicleOperationCostAnalysis {
	
	Scenario scenario;

	private final String networkFile = FilePaths.PATH_BASE_DIRECTORY + FilePaths.PATH_NETWORK_BERLIN_100PCT_ACCESS_LOOPS;
	private final String scheduleFile = FilePaths.PATH_BASE_DIRECTORY + FilePaths.PATH_TRANSIT_SCHEDULE_BERLIN_100PCT_WITHOUT_BUSES_IN_STUDY_AREA;
	private final String resultFile = FilePaths.PATH_BASE_DIRECTORY + "transitSchedule.100pct.withoutBusesInArea";
	
	private final double costPerM = 0.0023;
	
	Map<Id<TransitRoute>, Double> route2length = new HashMap<>();
	Map<Id<TransitRoute>, Integer> route2numDepartures = new HashMap<>();
	Map<Id<TransitRoute>, Double> route2cost = new HashMap<>();
	
	private String sep = ";";
	private BufferedWriter bw;

	public static void main(String[] args) {
		new PtVehicleOperationCostAnalysis().run();
	}

	/**
	 * 
	 */
	private void run() {
		scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
		new MatsimNetworkReader(scenario.getNetwork()).readFile(networkFile);
		new TransitScheduleReader(scenario).readFile(scheduleFile);
		
		try {
			initializeWriter();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("could not initialize writer");
		}
		
		for (TransitLine line: scenario.getTransitSchedule().getTransitLines().values()) {
			for (TransitRoute route: line.getRoutes().values()) {
				processTransitRoute(route);
			}
		}
		
		try {
			bw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new RuntimeException("could close writer");
		}
	}
	
	private void initializeWriter() throws IOException {
		bw = IOUtils.getBufferedWriter(resultFile);
		// write header
		bw.write("transitLineId" + sep + "transitRouteId" + sep + "length" + sep + "numDepartures" + sep +
				"cost");
		bw.newLine();
	}
	
	private void processTransitRoute(TransitRoute route) {
		double length = calculateLength(route);
		int numDepartures = route.getDepartures().size();
		double cost = length * numDepartures * costPerM;
		route2length.put(route.getId(), length);
		route2numDepartures.put(route.getId(), numDepartures);
		route2cost.put(route.getId(), cost);
		try {
			bw.write(length + sep + numDepartures + sep + cost);
			bw.newLine();
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new RuntimeException("could not write");
		}
	}

	private double calculateLength(TransitRoute route) {
		double length = 0;
		for (Id<Link> link: route.getRoute().getLinkIds()) {
			length += scenario.getNetwork().getLinks().get(link).getLength();
		}
		return length;
	}

}
