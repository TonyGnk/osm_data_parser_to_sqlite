package actions

import java.sql.DriverManager

fun cleanEmptyNodesAction(dbPath: String) {
    //println("Cleaning way nodes")
    //cleanEmptyWayNodes(dbPath)

    println("Cleaning single nodes")
    cleanEmptySingleNodes(dbPath)
}

private fun cleanEmptyWayNodes(dbPath: String) {
    DriverManager.getConnection("jdbc:sqlite:$dbPath").use { connection ->
        connection.autoCommit = false

        val deleteEmptyNodes = """
            DELETE FROM way_nodes
            WHERE way_id NOT IN (
                SELECT DISTINCT entity_id FROM places WHERE is_singe_point = 0
            )
        """.trimIndent()

        val deletedNodes = connection.createStatement().use { stmt ->
            stmt.executeUpdate(deleteEmptyNodes)
        }

        println("Deleted $deletedNodes way nodes")
        connection.commit()
        connection.autoCommit = true

        // Perform VACUUM after the transaction is complete and autoCommit is true
        connection.createStatement().use { stmt ->
            stmt.execute("VACUUM")
        }
    }
}

private fun cleanEmptySingleNodes(dbPath: String) {
    DriverManager.getConnection("jdbc:sqlite:$dbPath").use { connection ->
        connection.autoCommit = false

        val deleteEmptyNodes = """
            DELETE FROM nodes
            WHERE id NOT IN (
                SELECT DISTINCT node_id FROM way_nodes
                UNION
                SELECT DISTINCT node_id FROM suburbs
                UNION
                SELECT DISTINCT entity_id FROM places WHERE is_singe_point = 1
            )
        """.trimIndent()

        val deletedNodes = connection.createStatement().use { stmt ->
            stmt.executeUpdate(deleteEmptyNodes)
        }

        println("Deleted $deletedNodes nodes")
        connection.commit()
        connection.autoCommit = true

        // Perform VACUUM after the transaction is complete and autoCommit is true
        connection.createStatement().use { stmt ->
            stmt.execute("VACUUM")
        }
    }
}
