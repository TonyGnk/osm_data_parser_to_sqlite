//package actions.findClosest
//
//import actions.connectRoads.RoadToBeConnected
//import org.openstreetmap.osmosis.core.domain.v0_6.Way
//import java.sql.Connection
//import java.sql.DriverManager
//
//data class SuburbIdWithCoordinates(
//    val id: Long,
//    val lat: Double,
//    val lon: Double,
//)
//
//fun GetTheIdOfTheClosestSuburbOut(dbPath: String) {
//    println("Get Closest")
//    val sql: Connection = DriverManager.getConnection("jdbc:sqlite:$dbPath")
//    sql.autoCommit = false
//    // 1. Take all the roads in a huge mutable list
//    val roads = mutableListOf<RoadToBeConnected>()
//    sql.createStatement().use { statement ->
//        val resultSet = statement.executeQuery("SELECT * FROM connected_roads")
//        while (resultSet.next()) {
//            roads.add(
//                RoadToBeConnected(
//                    resultSet.getString("el_name"),
//                    resultSet.getString("en_name"),
//                    resultSet.getLong("way_id"),
//                )
//            )
//        }
//    }
//}
//
//private data class ClosestRoad(
//    val wayId: Long,
//    val elName: String,
//    val enName: String,
//    val nodeIds: MutableList<Long> = mutableListOf(),
//)
//
//private fun getTheIdOfTheClosestSuburb(
//    sql: Connection, way: Way
//): SuburbIdWithCoordinates {
//    val wayNodes = way.wayNodes
//
//    // Get the latitude and longitude for each node in the way
//    val nodeCoordinates = wayNodes.map { wayNode ->
//        sql.prepareStatement("SELECT latitude, longitude FROM nodes WHERE id = ?").use { stmt ->
//            stmt.setLong(1, wayNode.nodeId)
//            val result = stmt.executeQuery()
//            if (result.next()) {
//                Pair(result.getDouble("latitude"), result.getDouble("longitude"))
//            } else {
//                throw NoSuchElementException("Node not found for id: ${wayNode.nodeId}")
//            }
//        }
//    }
//
//    // Calculate average latitude and longitude
//    val averageLatitude = nodeCoordinates.map { it.first }.average()
//    val averageLongitude = nodeCoordinates.map { it.second }.average()
//
//    val query = """
//        SELECT s.node_id, n.latitude, n.longitude
//        FROM suburbs s
//        JOIN nodes n ON s.node_id = n.id
//        ORDER BY (n.latitude - ?)*(n.latitude - ?) + (n.longitude - ?)*(n.longitude - ?) ASC
//        LIMIT 1
//    """
//
//    sql.prepareStatement(query).use { stmt ->
//        stmt.setDouble(1, averageLatitude)
//        stmt.setDouble(2, averageLatitude)
//        stmt.setDouble(3, averageLongitude)
//        stmt.setDouble(4, averageLongitude)
//
//        val result = stmt.executeQuery()
//        if (result.next()) {
//            return SuburbIdWithCoordinates(
//                id = result.getLong("node_id"),
//                lat = averageLatitude,
//                lon = averageLongitude
//            )
//        }
//    }
//
//    throw NoSuchElementException("Suburb database probably empty")
//}
//
//
///*
//    """
//        CREATE TABLE IF NOT EXISTS nodes (
//            id INTEGER PRIMARY KEY,
//            latitude REAL,
//            longitude REAL
//        )
//    """
//
//    """
//        CREATE TABLE IF NOT EXISTS way_nodes (
//            way_id INTEGER,
//            node_id INTEGER,
//            sequence INTEGER,
//            FOREIGN KEY (way_id) REFERENCES ways(id),
//            FOREIGN KEY (node_id) REFERENCES nodes(id)
//        )
//    """
//
//    """
//        CREATE TABLE IF NOT EXISTS suburbs (
//            node_id INTEGER,
//            elName TEXT,
//            enName TEXT,
//            category TEXT
//        )
//    """
// */
