import java.sql.Connection
import java.sql.DriverManager

fun createTables(dbFilePath: String) {
    val sql: Connection = DriverManager.getConnection("jdbc:sqlite:$dbFilePath")
    sql.autoCommit = false
    val statement = sql.createStatement()

    statement.executeUpdate(
        """
        CREATE TABLE way_nodes (
            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            way_id INTEGER NOT NULL,
            latitude INTEGER NOT NULL,
            longitude INTEGER NOT NULL,
            sequence INTEGER NOT NULL
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
            latitude INTEGER NOT NULL,
            longitude INTEGER NOT NULL,
            category INTEGER NOT NULL
        )
    """
    )

    statement.executeUpdate(
        """
        CREATE TABLE places_multi (
            way_id INTEGER PRIMARY KEY NOT NULL,
            el_name TEXT,
            en_name TEXT,
            address_number INTEGER,
            road_id INTEGER,
            category INTEGER NOT NULL
        )
    """
    )

    statement.close()
    sql.commit()
    sql.autoCommit = true
    sql.close()
}


val globalRoadParts: MutableList<RoadPart> = mutableListOf()
val globalPlaceSingles: MutableList<PlaceSingle> = mutableListOf()
val globalPlacesMulti: MutableList<PlaceMulti> = mutableListOf()
val globalRoadConnected: MutableList<RoadPart> = mutableListOf()
val nodes: MutableMap<Long, Pair<Double, Double>> = mutableMapOf()


data class RoadPart(
    val wayId: Long,
    val elName: String?,
    val enName: String?,
    val wayNodes: Set<WayNode>,
)

data class PlaceSingle(
    val elName: String?,
    val enName: String?,
    val elAddress: String?,
    val enAddress: String?,
    val addressNumber: Int?,
    var wayId: Long? = null,
    val nodeId: Long,
    val latitude: Double,
    val longitude: Double,
    val category: Int,
)

data class PlaceMulti(
    val wayId: Long,
    val elName: String?,
    val enName: String?,
    val elAddress: String?,
    val enAddress: String?,
    val addressNumber: Int?,
    var roadWayId: Long? = null,
    val wayNodes: Set<WayNode>,
    val category: Int,
)

data class WayNode(
    val wayId: Long,
    val nodeId: Long,
    val sequence: Int,
    var latitude: Double,
    var longitude: Double,
)
