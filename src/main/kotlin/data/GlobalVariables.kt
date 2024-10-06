package data

import org.openstreetmap.osmosis.core.domain.v0_6.Node
import org.openstreetmap.osmosis.core.domain.v0_6.Way
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap

val globalRoadParts: MutableList<RoadPart> = mutableListOf()
val globalRoadConnected: MutableList<RoadPart> = mutableListOf()
val fullNodesMap: MutableMap<Long, Node> = ConcurrentHashMap<Long, Node>()
val fullWays: MutableList<Way> = Collections.synchronizedList(mutableListOf<Way>())
val globalLocationsList: MutableList<Location> = mutableListOf()
