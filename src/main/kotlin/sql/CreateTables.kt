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
        CREATE TABLE locations (
            id INTEGER PRIMARY KEY NOT NULL,
            latitude REAL NOT NULL,
            longitude REAL NOT NULL
        )
    """
    )

    statement.executeUpdate(
        """
        CREATE TABLE pois (
            id TEXT PRIMARY KEY NOT NULL,
            title_el TEXT,
            title_en TEXT,
            subtitle_el TEXT,
            subtitle_en TEXT,
            category TEXT,
            frequency INTEGER NOT NULL
        )
    """
    )

    statement.executeUpdate(
        """
        CREATE TABLE suburbs (
            id TEXT PRIMARY KEY NOT NULL,
            title_el TEXT,
            title_en TEXT,
            category TEXT
        )
    """
    )

    statement.close()
    sql.commit()
    sql.autoCommit = true
    sql.close()
}
