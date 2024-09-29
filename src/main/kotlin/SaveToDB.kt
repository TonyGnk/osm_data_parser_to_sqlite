import utils.setIntOrNull
import utils.setLongOrNull
import utils.setStringOrNull
import java.sql.Connection
import java.sql.DriverManager

fun saveToDB(dbPath: String) {
    print("Parsing...")
    val sql: Connection = DriverManager.getConnection("jdbc:sqlite:$dbPath")
    sql.autoCommit = false

    val saveToRoadsStmt = sql.prepareStatement(
        "INSERT INTO roads (way_id, el_Name, en_Name) VALUES (?, ?, ?)"
    )
    val saveToWayNodesStmt = sql.prepareStatement(
        "INSERT INTO way_nodes (way_id, latitude, longitude, sequence) VALUES (?, ?, ?, ?)"
    )
    val saveToPlacesSingleStmt = sql.prepareStatement(
        "INSERT INTO places_single " +
                "(el_name, en_name, address_number, road_id, latitude, longitude, category) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)"
    )
    val saveToPlacesMultiStmt = sql.prepareStatement(
        "INSERT INTO places_multi " +
                "(way_id, el_name, en_name, address_number, road_id, category) " +
                "VALUES (?, ?, ?, ?, ?, ?)"
    )

    globalRoadConnected.forEach { road ->
        saveToRoadsStmt.setLong(1, road.wayId)
        saveToRoadsStmt.setStringOrNull(2, road.elName)
        saveToRoadsStmt.setStringOrNull(3, road.enName)
        saveToRoadsStmt.addBatch()

        road.wayNodes.forEach { node ->
            saveToWayNodesStmt.setLong(1, road.wayId)
            saveToWayNodesStmt.setDouble(2, node.latitude)
            saveToWayNodesStmt.setDouble(3, node.longitude)
            saveToWayNodesStmt.setInt(4, node.sequence)
            saveToWayNodesStmt.addBatch()
        }
    }

    globalPlaceSingles.forEach { place ->
        saveToPlacesSingleStmt.setStringOrNull(1, place.elName)
        saveToPlacesSingleStmt.setStringOrNull(2, place.enName)
        saveToPlacesSingleStmt.setIntOrNull(3, place.addressNumber)
        saveToPlacesSingleStmt.setLongOrNull(4, place.wayId)
        saveToPlacesSingleStmt.setDouble(5, place.latitude)
        saveToPlacesSingleStmt.setDouble(6, place.longitude)
        saveToPlacesSingleStmt.setInt(7, place.category)
        saveToPlacesSingleStmt.addBatch()
    }

    globalPlacesMulti.forEach { place ->
        saveToPlacesMultiStmt.setLong(1, place.wayId)
        saveToPlacesMultiStmt.setStringOrNull(2, place.elName)
        saveToPlacesMultiStmt.setStringOrNull(3, place.enName)
        saveToPlacesMultiStmt.setIntOrNull(4, place.addressNumber)
        saveToPlacesMultiStmt.setLongOrNull(5, place.roadWayId)
        saveToPlacesMultiStmt.setInt(6, place.category)
        saveToPlacesMultiStmt.addBatch()
    }

    saveToRoadsStmt.executeBatch()
    saveToWayNodesStmt.executeBatch()
    saveToPlacesSingleStmt.executeBatch()
    saveToPlacesMultiStmt.executeBatch()

    saveToRoadsStmt.close()
    saveToWayNodesStmt.close()
    saveToPlacesSingleStmt.close()
    saveToPlacesMultiStmt.close()

    sql.commit()
    sql.autoCommit = true
    sql.createStatement().execute("VACUUM")
    sql.close()
    println("Done!")

    globalRoadParts.clear()
    globalPlaceSingles.clear()
    globalPlacesMulti.clear()
    globalRoadConnected.clear()
}
