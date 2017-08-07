package playground.gleich.av_bus.analysis;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.pt.transitSchedule.api.Departure;
import org.matsim.pt.transitSchedule.api.TransitLine;
import org.matsim.pt.transitSchedule.api.TransitRoute;
import org.matsim.pt.transitSchedule.api.TransitScheduleReader;
import org.matsim.pt.transitSchedule.api.TransitStopFacility;
import org.matsim.vehicles.Vehicle;
import org.matsim.vehicles.VehicleType;

import playground.gleich.av_bus.FilePaths;

public class PtUmlaufEstimator {
	
	Scenario scenario;

	private final String scheduleFile = FilePaths.PATH_BASE_DIRECTORY + FilePaths.PATH_TRANSIT_SCHEDULE_BERLIN_100PCT;
	private final String vehicleFile = FilePaths.PATH_BASE_DIRECTORY + FilePaths.PATH_TRANSIT_VEHICLES_BERLIN_100PCT_45MPS;
	private static final String resultFile = FilePaths.PATH_BASE_DIRECTORY + "data/analysis/operationCost/umlaufEstimator_transitSchedule.100pct.base.csv";
	
	SortedSet<TerminusStop> terminusStops = new TreeSet<>();
	Map<Id<Vehicle>, Umlauf> vehId2umlauf = new HashMap<>();
	
	private String sep = ";";
	private BufferedWriter bw;

	private class Umlauf {
		private final Vehicle veh;
		List<DepartureServed> departures = new ArrayList<>();
		double timeSinceLastBreak = 0;

		Umlauf(Vehicle veh) {
			this.veh = veh;
		}
	}
	
	private class DepartureServed {
		private final Id<Vehicle> umlauf;
		private final Departure departure;
		private final TransitLine line;
		private final TransitRoute route;
		private final double arrivalTimeAtFirstStop;
		private final double departureTimeAtLastStop;
		/**
		 * @param departure
		 * @param line
		 * @param route
		 * @param arrivalTimeAtFirstStop
		 * @param departureTimeAtLastStop
		 */
		DepartureServed(Id<Vehicle> umlauf, Departure departure, TransitLine line, TransitRoute route, double arrivalTimeAtFirstStop,
				double departureTimeAtLastStop) {
			this.umlauf = umlauf;
			this.departure = departure;
			this.line = line;
			this.route = route;
			this.arrivalTimeAtFirstStop = arrivalTimeAtFirstStop;
			this.departureTimeAtLastStop = departureTimeAtLastStop;
		}
	}
	
	private class TerminusStop implements Comparable<TerminusStop> {
		private final Id<TransitStopFacility> id;
		Map<Id<VehicleType>, Queue<Umlauf>> vehType2queueIncomingUmlauf;
		// departure time of next outgoing departure
		private double nextPendingDepartureTime;
		/**
		 * @param id
		 */
		TerminusStop(Id<TransitStopFacility> id) {
			this.id = id;
		}

		@Override
		public int compareTo(TerminusStop o) {
			return Double.compare(this.nextPendingDepartureTime, o.nextPendingDepartureTime);
		}
		
	}
	
	public static void main(String[] args) {
		PtUmlaufEstimator analysis = new PtUmlaufEstimator();
		analysis.run();
		analysis.writeResults(resultFile);
	}


	public void run() {
		Config config = ConfigUtils.createConfig();
		config.transit().setTransitScheduleFile(scheduleFile);
		config.transit().setVehiclesFile(vehicleFile);
		scenario = ScenarioUtils.loadScenario(config);
//		scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
//		new TransitScheduleReader(scenario).readFile(scheduleFile);
		
		findTerminusStops();
		buildUmlaufe();
	}
	
	private void findTerminusStops() {
		// TODO Auto-generated method stub
		
	}


	private void buildUmlaufe() {
		// TODO Auto-generated method stub
		
	}

	public void writeResults(String file) {
		try {
			bw = IOUtils.getBufferedWriter(file);
			// write header
			bw.write("transitLineId" + sep + "maxLength" + sep + "maxTimeDriven" + sep + "sumDepartures" + sep +
					"sumDistanceDriven" + sep + "sumTimeDriven" + sep + "sumCost");
			bw.newLine();

//			for (TransitLine line: scenario.getTransitSchedule().getTransitLines().values()) {
//				double maxLength = 0;
//				double maxTimeDriven = 0;
//				int sumDepartures = 0;
//				double sumDistanceDriven = 0;
//				double sumTimeDriven = 0;
//				double sumCost = 0;
//				for (TransitRoute route: line.getRoutes().values()) {
//					if (route2length.get(route.getId()) > maxLength) {
//						maxLength = route2length.get(route.getId());
//					}
//					if (route2time.get(route.getId()) > maxTimeDriven) {
//						maxTimeDriven = route2time.get(route.getId());
//					}
//					sumDepartures += route2numDepartures.get(route.getId());
//					sumDistanceDriven += route2numDepartures.get(route.getId()) * route2length.get(route.getId());
//					sumTimeDriven += route2numDepartures.get(route.getId()) * route2time.get(route.getId());
//					sumCost += route2cost.get(route.getId());
//				}
//				bw.write(line.getId().toString() + sep + maxLength + sep + maxTimeDriven + sep + sumDepartures +
//						sep + sumDistanceDriven + sep + sumTimeDriven + sep + sumCost);
//				bw.newLine();
//			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("could not write");
		}
	}
}
