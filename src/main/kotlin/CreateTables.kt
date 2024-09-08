import java.sql.Connection
import java.sql.DriverManager

fun createTables(dbFilePath: String) {
    val sql: Connection = DriverManager.getConnection("jdbc:sqlite:$dbFilePath")
    sql.autoCommit = false
    val statement = sql.createStatement()

    statement.executeUpdate(
        """
        CREATE TABLE nodes (
            id INTEGER PRIMARY KEY NOT NULL,
            latitude INTEGER NOT NULL,
            longitude INTEGER NOT NULL
        )
    """
    )

    statement.executeUpdate(
        """
        CREATE TABLE way_nodes (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            way_id INTEGER NOT NULL,
            node_id INTEGER NOT NULL,
            sequence INTEGER NOT NULL,
            FOREIGN KEY (node_id) REFERENCES nodes(id)
        )
    """
    )

    statement.executeUpdate(
        """
        CREATE TABLE roads (
            way_id INTEGER PRIMARY KEY NOT NULL,
            el_name TEXT,
            en_name TEXT
        )
    """
    )


    // Create places table
    statement.executeUpdate(
        """
        CREATE TABLE places (
            entity_id INTEGER NOT NULL,
            el_name TEXT,
            en_name TEXT,
            way_id INTEGER,
            address_number TEXT,
            category TEXT NOT NULL,
            is_singe_point BOOLEAN NOT NULL
        )
    """
    )

    statement.close()
    sql.commit()
    sql.autoCommit = true
    sql.close()
}
