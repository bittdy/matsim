/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2014 by the members listed in the COPYING,        *
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
package org.matsim.contrib.parking.PC2.simulation;

import java.util.HashSet;

import org.matsim.api.core.v01.Id;
import org.matsim.contrib.parking.PC2.infrastructure.PPRestrictedToFacilities;
import org.matsim.contrib.parking.PC2.infrastructure.PrivateParking;
import org.matsim.contrib.parking.lib.obj.LinkedListValueHashMap;
import org.matsim.contrib.parking.parkingChoice.infrastructure.Parking;
import org.matsim.core.utils.collections.QuadTree;

public class ParkingInfrastructureManager {

	// facilityId -> parkings available to users of those facilities
	private LinkedListValueHashMap<Id, PPRestrictedToFacilities> privateParkingsRestrictedToFacilities;
	
	
	
	
	
	// TODO: make private parking (attached to facility)
	// + also private parking, which is attached to activity
	// both should be checked.
	
	private QuadTree<Parking> availablePublicParkings;
	
	private HashSet<Parking> fullPublicParkings;
	
	// allso allow to filter by group the parkings
	
	// Allow to reprogramm the decision making process of the agent => provide default module for decision making and new one,
	// which could also cope with EVs.
	
	// provide interface for proper integration.
	
	
	
	// also loading of data should 
	
}
