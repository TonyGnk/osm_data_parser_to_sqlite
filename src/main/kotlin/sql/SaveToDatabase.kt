package sql

import data.globalGlassList
import data.globalCoordinateList
import data.globalLocationsList
import data.globalRoadConnected
import data.globalRoadGlassList
import data.globalSuburbsList
import utils.capitalizeFirstLetter
import utils.setIntOrNull
import java.sql.Connection
import java.sql.DriverManager

fun saveToDatabase(dbPath: String) {
    val sql: Connection = DriverManager.getConnection("jdbc:sqlite:$dbPath")
    sql.autoCommit = false

    val saveGlassStmt = sql.prepareStatement(
        "INSERT INTO pois (id, title_el, title_en, subtitle_el, subtitle_en, category, frequency) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)"
    )
    val saveWayNodesStmt = sql.prepareStatement(
        "INSERT INTO way_nodes (way_id, latitude, longitude, sequence) VALUES (?, ?, ?, ?)"
    )
    val saveLocationsStmt = sql.prepareStatement(
        "INSERT INTO locations " +
                "(id, latitude, longitude)" +
                "VALUES (?, ?, ?)"
    )

    val saveSuburbsStmt = sql.prepareStatement(
        "INSERT INTO suburbs " +
                "(id, title_el, title_en, category)" +
                "VALUES (?, ?, ?, ?)"
    )

    //Print the size of each set
    print("Saving... ${globalRoadConnected.size} roads, ${globalLocationsList.size} places... ")


    //Find Waynodes with not unique sequence

    globalGlassList.forEach { glass ->
        saveGlassStmt.setString(1, glass.id)
        saveGlassStmt.setString(2, glass.titleEl.capitalizeFirstLetter())
        saveGlassStmt.setString(3, glass.titleEn.capitalizeFirstLetter())
        saveGlassStmt.setString(4, glass.subTitleEl.capitalizeFirstLetter())
        saveGlassStmt.setString(5, glass.subTitleEn.capitalizeFirstLetter())
        saveGlassStmt.setString(6, glass.category)
        saveGlassStmt.setIntOrNull(7, 0)
        saveGlassStmt.addBatch()
    }

    globalCoordinateList.distinctBy {
        it.id
    }.forEach { location ->
        saveLocationsStmt.setLong(1, location.id)
        saveLocationsStmt.setDouble(2, location.latitude)
        saveLocationsStmt.setDouble(3, location.longitude)
        saveLocationsStmt.addBatch()
    }

    globalRoadGlassList.forEach { roadGlass ->
        saveWayNodesStmt.setLong(1, roadGlass.wayId)
        saveWayNodesStmt.setDouble(2, roadGlass.latitude)
        saveWayNodesStmt.setDouble(3, roadGlass.longitude)
        saveWayNodesStmt.setInt(4, roadGlass.sequence)
        saveWayNodesStmt.addBatch()
    }

    globalSuburbsList.forEach { suburb ->
        saveSuburbsStmt.setString(1, suburb.id)
        saveSuburbsStmt.setString(2, suburb.titleEl.capitalizeFirstLetter())
        saveSuburbsStmt.setString(3, suburb.titleEn.capitalizeFirstLetter())
        saveSuburbsStmt.setString(4, suburb.category)
        saveSuburbsStmt.addBatch()
    }

    saveGlassStmt.executeBatch()
    saveWayNodesStmt.executeBatch()
    saveLocationsStmt.executeBatch()
    saveSuburbsStmt.executeBatch()

    saveGlassStmt.close()
    saveWayNodesStmt.close()
    saveLocationsStmt.close()
    saveSuburbsStmt.close()

    sql.commit()
    sql.autoCommit = true
    sql.createStatement().execute("VACUUM")
    sql.close()
    println(
        "\rSaving...${globalGlassList.size} glass, ${globalCoordinateList.size} locations, ${globalRoadGlassList.size} roads...OK "
    )

}
