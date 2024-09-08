package arch

import java.sql.Connection

fun getTheIdOfTheRoad(sql: Connection, elAddress: String, enAddress: String): Long? {
    if (elAddress.isBlank() && enAddress.isBlank()) return null

    // 1. Search for the road in the database
    val roads = findRoads(sql, elAddress, enAddress)
    //println("elAddress: $elAddress, enAddress: $enAddress")
    //println("roads: ${roads.map { it.elName }}")

    return when {
        // 2a. If the road is found and is 1 road, return the id of the road
        roads.size == 1 -> roads[0].wayId

        // 2b. If the road is found and is more than 1 road, find the average latitude and longitude of each road
        //     and return the id of the road that is closest to the average latitude and longitude
        roads.size > 1 -> findClosestRoad(roads)

        // 2c. If the road is not found, return null
        else -> null
    }
}

data class Road(
    val wayId: Long,
    val elName: String,
    val enName: String,
    //val avLatitude: Double,
    // val avLongitude: Double
)

fun findRoads(sql: Connection, elAddress: String, enAddress: String): List<Road> {
    val elWords = elAddress.split(" ").filter { it.length > 1 }
    val enWords = enAddress.split(" ").filter { it.length > 1 }

    val query = buildString {
        append(
            """
            SELECT way_id, el_name, en_name
            FROM roads
            WHERE 1=0
        """
        )
        elWords.forEachIndexed { index, _ ->
            append(" OR el_name LIKE ?")
        }
        enWords.forEachIndexed { index, _ ->
            append(" OR en_name LIKE ?")
        }
        append(" LIMIT 50")
    }

    return sql.prepareStatement(query).use { stmt ->
        var paramIndex = 1
        elWords.forEach { word ->
            stmt.setString(paramIndex++, "%$word%")
        }
        enWords.forEach { word ->
            stmt.setString(paramIndex++, "%$word%")
        }
        stmt.executeQuery().use { rs ->
            val roads = mutableListOf<Road>()
            while (rs.next()) {
                roads.add(
                    Road(
                        rs.getLong("way_id"),
                        rs.getString("el_name"),
                        rs.getString("en_name"),
                        // rs.getDouble("av_latitude"),
                        // rs.getDouble("av_longitude")
                    )
                )
            }
            roads
        }
    }
}


fun findClosestRoad(roads: List<Road>): Long? {
    val avgCoordinates = roads.map { road ->
        val (avgLat, avgLon) = 0 to 0// road.avLatitude to road.avLongitude
        Triple<Long, Double, Double>(road.wayId, avgLat.toDouble(), avgLon.toDouble())
    }

    val (totalLat, totalLon) = avgCoordinates.fold(0.0 to 0.0) { (sumLat, sumLon), (_, lat, lon) ->
        (sumLat + lat) to (sumLon + lon)
    }
    val centerLat = totalLat / avgCoordinates.size
    val centerLon = totalLon / avgCoordinates.size

    return avgCoordinates.minByOrNull { (_, lat, lon) ->
        distanceSquared(lat, lon, centerLat, centerLon)
    }?.first
}


fun distanceSquared(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val dLat = lat2 - lat1
    val dLon = lon2 - lon1
    return dLat * dLat + dLon * dLon
}

/*
        """
        CREATE TABLE IF NOT EXISTS roads (
            way_id INTEGER PRIMARY KEY,
            el_name TEXT,
            en_name TEXT,
            av_latitude REAL,
            av_longitude REAL,
            suburb_id INTEGER
        )
    """

        """
        CREATE TABLE IF NOT EXISTS way_nodes (
            way_id INTEGER,
            node_id INTEGER,
            sequence INTEGER,
            FOREIGN KEY (way_id) REFERENCES ways(id),
            FOREIGN KEY (node_id) REFERENCES nodes(id)
        )
    """

        """
        CREATE TABLE IF NOT EXISTS nodes (
            id INTEGER PRIMARY KEY,
            latitude REAL,
            longitude REAL
        )
    """

 */
