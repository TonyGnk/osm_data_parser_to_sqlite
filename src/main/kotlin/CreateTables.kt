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
            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
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
            entity_id INTEGER PRIMARY KEY NOT NULL,
            el_name TEXT,
            en_name TEXT,
            el_address TEXT,
            en_address TEXT,
            address_number INTEGER,
            category TEXT,
            is_singe_point INTEGER NOT NULL
        )
    """
    )

    statement.close()
    sql.commit()
    sql.autoCommit = true
    sql.close()
}
