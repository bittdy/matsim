package org.matsim.core.router.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.matsim.core.api.experimental.network.Link;
import org.matsim.core.api.experimental.network.Node;
import org.matsim.core.basic.v01.IdImpl;
import org.matsim.core.network.LinkImpl;
import org.matsim.core.network.NetworkLayer;
import org.matsim.core.network.NodeImpl;
import org.matsim.core.utils.misc.Time;

/**
 * Converts a network to an inverted network. Inverted nodes are situated at
 * the end of the real link. Inverted link attributes are copied from toLink
 * of the real network, thus every inverted link actually starts at the
 * location of a real node.
 * 
 * @author aneumann
 * @author dgrether
 */
public class NetworkInverter {

	final private static Logger log = Logger.getLogger(NetworkInverter.class);
	
	private NetworkLayer originalNetwork;
	
	private NetworkLayer invertedNetwork = null;

	public NetworkInverter(NetworkLayer originalNet) {
		this.originalNetwork = originalNet;
	}

	public NetworkLayer getInvertedNetwork() {
		if (this.invertedNetwork == null){
			invertNetwork();
		}
		return this.invertedNetwork;
	}
	
	private void invertNetwork(){
		this.invertedNetwork = new NetworkLayer();
		int numberOfNodesGenerated = 0;
		int numberOfLinksGenerated = 0;

		for (LinkImpl link : this.originalNetwork.getLinks().values()) {
			this.invertedNetwork.createNode(link.getId(), link.getToNode().getCoord());
			numberOfNodesGenerated++;
		}

		for (NodeImpl node : this.originalNetwork.getNodes().values()) {
			for (LinkImpl inLink : node.getInLinks().values()) {
				for (LinkImpl outLink : node.getOutLinks().values()) {
					LinkImpl link = this.invertedNetwork.createLink(new IdImpl(numberOfLinksGenerated),
							this.invertedNetwork.getNode(inLink.getId()), this.invertedNetwork.getNode(outLink.getId().toString()),
							outLink.getLength(),
							outLink.getFreespeed(Time.UNDEFINED_TIME),
							outLink.getCapacity(Time.UNDEFINED_TIME),
							outLink.getNumberOfLanes(Time.UNDEFINED_TIME));
					link.setType(outLink.getType());
					numberOfLinksGenerated++;
				}
			}
		}

		log.info("Generated " + numberOfNodesGenerated + " Nodes and " + numberOfLinksGenerated + " Links");

		// Debug only
		// NetworkWriter myNetworkWriter = new NetworkWriter(wrappedNetwork,
		// "wrappedNetwork");
		// myNetworkWriter.write();
	}
	
	
	public List<Node> convertInvertedLinksToNodes(List<Link> links) {
		List<Node> ret = new ArrayList<Node>(links.size());
		for (Link l : links){
			ret.add(this.originalNetwork.getNode(l.getId()));
		}
		return ret;
	}

	public List<Link> convertInvertedNodesToLinks(List<Node> nodes) {
		List<Link> ret = new ArrayList<Link>(nodes.size());
		for (Node n : nodes){
			ret.add(this.originalNetwork.getLink(n.getId()));
		}
		return ret;
	}

}
