package playground.mas.dispatcher;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.router.Dijkstra;
import org.matsim.core.router.costcalculators.OnlyTimeDependentTravelDisutility;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import playground.mas.MASConfigGroup;
import playground.mas.routing.MASAVTravelDisutility;
import playground.mas.routing.MASCordonTravelDisutility;
import playground.sebhoerl.avtaxi.config.AVDispatcherConfig;
import playground.sebhoerl.avtaxi.data.AVOperator;
import playground.sebhoerl.avtaxi.dispatcher.AVDispatcher;
import playground.sebhoerl.avtaxi.dispatcher.single_heuristic.SingleHeuristicDispatcher;
import playground.sebhoerl.avtaxi.dispatcher.utils.SingleRideAppender;
import playground.sebhoerl.avtaxi.framework.AVConfigGroup;
import playground.sebhoerl.avtaxi.framework.AVModule;
import playground.sebhoerl.avtaxi.routing.AVParallelRouterFactory;
import playground.sebhoerl.plcpc.ParallelLeastCostPathCalculator;
import playground.sebhoerl.plcpc.ParallelLeastCostPathCalculatorFactory;

import java.util.Collection;

@Singleton
public class MASSoloDispatcherFactory implements AVDispatcher.AVDispatcherFactory {
    @Inject private Network network;
    @Inject private EventsManager eventsManager;

    @Inject @Named(AVModule.AV_MODE)
    private TravelTime travelTime;

    @Inject @Named("av_solo") ParallelLeastCostPathCalculator router;

    @Override
    public AVDispatcher createDispatcher(AVDispatcherConfig config) {
        return new SingleHeuristicDispatcher(
                config.getParent().getId(),
                eventsManager,
                network,
                new SingleRideAppender(config, router, travelTime)
        );
    }
}
