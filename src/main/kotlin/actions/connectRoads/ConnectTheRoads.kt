package actions.connectRoads

import java.sql.Connection
import java.sql.DriverManager

/*
        CREATE TABLE way_nodes (
            way_id INTEGER NOT NULL,
            node_id INTEGER NOT NULL,
            sequence INTEGER NOT NULL,
            PRIMARY KEY (way_id, node_id),
            FOREIGN KEY (node_id) REFERENCES nodes(id)
        )

        CREATE TABLE roads (
            way_id INTEGER PRIMARY KEY NOT NULL,
            el_name TEXT,
            en_name TEXT
        )
 */


fun connectTheRoads(dbPath: String) {
    println("Connect roads")
    val sql: Connection = DriverManager.getConnection("jdbc:sqlite:$dbPath")
    sql.autoCommit = false
    // Take all the roads in a huge mutable list
    val roads = mutableListOf<RoadToBeConnected>()
    sql.createStatement().use { statement ->
        val resultSet = statement.executeQuery("SELECT * FROM roads")
        while (resultSet.next()) {
            roads.add(
                RoadToBeConnected(
                    resultSet.getString("el_name"),
                    resultSet.getString("en_name"),
                    resultSet.getLong("way_id"),
                )
            )
        }
    }

    // For every road, take the nodeIds
    sql.prepareStatement("SELECT node_id FROM way_nodes WHERE way_id = ? ORDER BY sequence")
        .use { stmt ->
            for (road in roads) {
                stmt.setLong(1, road.wayId)
                val resultSet = stmt.executeQuery()
                while (resultSet.next()) {
                    road.nodeIds.add(resultSet.getLong("node_id"))
                }
            }
        }

    // Delete everything you take from the original database
    sql.autoCommit = false
    try {
        sql.createStatement().use { statement ->
            statement.executeUpdate("DELETE FROM way_nodes")
            statement.executeUpdate("DELETE FROM roads")
        }
        sql.commit()
    } catch (e: Exception) {
        sql.rollback()
        throw e
    }

    while (roads.isNotEmpty()) {
        val road = roads.first()
        val elName = road.elName
        val enName = road.enName

        val roadsWithSameNames: List<RoadToBeConnected> = roads.filter {
            it.elName == elName || it.enName == enName
        }
        roads.removeAll(roadsWithSameNames)


        val sets = connectASet(roadsWithSameNames)

        //Each set insert it to the table
        sets.forEach { set ->
            //Insert the road
            val insertRoad = sql.prepareStatement(
                "INSERT INTO roads (way_id, el_name, en_name) VALUES (?, ?, ?)"
            )
            insertRoad.setLong(1, set.wayIds.first())
            insertRoad.setString(2, set.elName)
            insertRoad.setString(3, set.enName)

            insertRoad.addBatch()
            insertRoad.executeBatch()
            insertRoad.close()

            //Insert the way_nodes
            val insertWayNodes = sql.prepareStatement(
                "INSERT INTO way_nodes (way_id, node_id, sequence) VALUES (?, ?, ?)"
            )
            set.nodeIds.forEachIndexed { index, nodeId ->
                insertWayNodes.setLong(1, set.wayIds.first())
                insertWayNodes.setLong(2, nodeId)
                insertWayNodes.setInt(3, index)
                insertWayNodes.addBatch()
            }
            insertWayNodes.executeBatch()
            insertWayNodes.close()

            //Update all the previous references to the new way_id.
            set.wayIds.drop(1).forEach { wayId ->
                val updateRoads = sql.prepareStatement(
                    "UPDATE places SET way_id = ? WHERE way_id = ?"
                )
                updateRoads.setLong(1, set.wayIds.first())
                updateRoads.setLong(2, wayId)
                updateRoads.addBatch()
                updateRoads.executeBatch()
                updateRoads.close()
            }
        }

    }

    sql.commit()  // Commit the transaction after all inserts
    sql.autoCommit = true  // Set autoCommit to true
    sql.createStatement().execute("VACUUM")
    sql.close()
}


data class RoadToBeConnected(
    val elName: String,
    val enName: String,
    val wayId: Long,
    val nodeIds: MutableList<Long> = mutableListOf()
)
