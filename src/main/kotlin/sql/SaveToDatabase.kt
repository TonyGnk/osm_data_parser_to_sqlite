package sql

import data.globalLocationsList
import data.globalRoadConnected
import data.locationSubCategories
import utils.setIntOrNull
import utils.setLongOrNull
import utils.setStringOrNull
import java.sql.Connection
import java.sql.DriverManager

fun saveToDatabase(dbPath: String) {
    val sql: Connection = DriverManager.getConnection("jdbc:sqlite:$dbPath")
    sql.autoCommit = false

    val saveToRoadsStmt = sql.prepareStatement(
        "INSERT INTO roads (way_id, el_Name, en_Name) VALUES (?, ?, ?)"
    )
    val saveToWayNodesStmt = sql.prepareStatement(
        "INSERT INTO way_nodes (way_id, latitude, longitude, sequence) VALUES (?, ?, ?, ?)"
    )
    val saveToPlacesStmt = sql.prepareStatement(
        "INSERT INTO places_single " +
                "(el_name, en_name, address_number, road_id, latitude, longitude, category) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)"
    )
    val saveToCategoriesStmt = sql.prepareStatement(
        "INSERT INTO categories (id, en_name, el_name) VALUES (?, ?, ?)"
    )

    //Print the size of each set
    print("Saving... ${globalRoadConnected.size} roads, ${globalLocationsList.size} places... ")


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

    globalLocationsList.forEach { place ->
        saveToPlacesStmt.setStringOrNull(1, place.elName)
        saveToPlacesStmt.setStringOrNull(2, place.enName)
        saveToPlacesStmt.setIntOrNull(3, place.addressNumber)
        saveToPlacesStmt.setLongOrNull(4, place.wayId)
        saveToPlacesStmt.setDouble(5, place.latitude)
        saveToPlacesStmt.setDouble(6, place.longitude)
        saveToPlacesStmt.setInt(7, place.category)
        saveToPlacesStmt.addBatch()
    }

    locationSubCategories.forEach { (name, id) ->
        saveToCategoriesStmt.setInt(1, id)
        saveToCategoriesStmt.setString(2, name)
        saveToCategoriesStmt.setString(3, name)
        saveToCategoriesStmt.addBatch()
    }

    saveToRoadsStmt.executeBatch()
    saveToWayNodesStmt.executeBatch()
    saveToPlacesStmt.executeBatch()
    saveToCategoriesStmt.executeBatch()

    saveToRoadsStmt.close()
    saveToWayNodesStmt.close()
    saveToPlacesStmt.close()
    saveToCategoriesStmt.close()

    sql.commit()
    sql.autoCommit = true
    sql.createStatement().execute("VACUUM")
    sql.close()
    println("\rSaving...${globalRoadConnected.size} roads, ${globalLocationsList.size} places...OK ")
}
