package sql

import java.io.File
import java.sql.Connection
import java.sql.DriverManager

fun createTables(dbPath: String) {
    if (File(dbPath).exists()) File(dbPath).delete()
    println("Creating tables...")
    val sql: Connection = DriverManager.getConnection("jdbc:sqlite:$dbPath")
    sql.autoCommit = false
    val statement = sql.createStatement()

    statement.executeUpdate(
        """
        CREATE TABLE way_nodes (
            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            way_id INTEGER NOT NULL,
            latitude REAL NOT NULL,
            longitude REAL NOT NULL,
            sequence INTEGER NOT NULL
        )
    """
    )

    statement.executeUpdate(
        """
        CREATE TABLE categories (
            id INTEGER PRIMARY KEY NOT NULL,
            en_name TEXT NOT NULL,
            el_name TEXT NOT NULL
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

    statement.executeUpdate(
        """
        CREATE TABLE places_single (
            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            el_name TEXT,
            en_name TEXT,
            address_number INTEGER,
            road_id INTEGER,
            latitude REAL NOT NULL,
            longitude REAL NOT NULL,
            category INTEGER NOT NULL,
            FOREIGN KEY (category) REFERENCES categories(id)
        )
    """
    )

    statement.close()
    sql.commit()
    sql.autoCommit = true
    sql.close()
}
