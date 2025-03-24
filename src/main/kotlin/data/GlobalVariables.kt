package data

import org.openstreetmap.osmosis.core.domain.v0_6.Node
import org.openstreetmap.osmosis.core.domain.v0_6.Way
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap

/**
 * A mutable list of road parts.
 * This list contains all the road parts extracted from the OSM data.
 */
val globalRoadParts: MutableList<RoadPart> = mutableListOf()

/**
 * A mutable list of connected road parts.
 * This list contains road parts that have been connected into continuous road segments.
 */
val globalRoadConnected: MutableList<RoadPart> = mutableListOf()

/**
 * A concurrent map of nodes.
 * This map contains all the nodes extracted from the OSM data, with their IDs as keys.
 */
val fullNodesMap: MutableMap<Long, Node> = ConcurrentHashMap<Long, Node>()

/**
 * A synchronized list of ways.
 * This list contains all the ways extracted from the OSM data.
 */
val fullWays: MutableList<Way> = Collections.synchronizedList(mutableListOf<Way>())

/**
 * A mutable list of locations.
 * This list contains all the locations extracted from the OSM data.
 */
val globalLocationsList: MutableList<Location> = mutableListOf()

/**
 * A mutable list of glass objects.
 * This list contains all the glass objects created from the OSM data.
 */
val globalGlassList: MutableList<Glass> = mutableListOf()

/**
 * A mutable list of coordinates.
 * This list contains all the coordinates extracted from the OSM data.
 */
val globalCoordinateList: MutableList<Coordinate> = mutableListOf()

/**
 * A mutable list of road glass objects.
 * This list contains all the road glass objects created from the OSM data.
 */
val globalRoadGlassList: MutableList<RoadGlass> = mutableListOf()

/**
 * A mutable list of suburbs.
 * This list contains all the suburbs extracted from the OSM data.
 */
val globalSuburbsList: MutableList<Suburb> = mutableListOf()
