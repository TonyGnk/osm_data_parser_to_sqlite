package parser.way

import utils.findTagWithName
import insert.insertWays
import org.openstreetmap.osmosis.core.domain.v0_6.Way
import java.sql.Connection

data class SuburbIdWithCoordinates(
    val id: Long,
    val lat: Double,
    val lon: Double,
)

fun handleRoads(sql: Connection, way: Way) {

    val elName = way.tags.findTagWithName(listOf("name:el", "name"))
    val enName = way.tags.findTagWithName(listOf("name:en", "int_name"))

    val notEmptyNames = elName.isNotBlank() || enName.isNotBlank()

    if (notEmptyNames) {
        insertRoads(
            sql = sql,
            id = way.id,
            elName = elName,
            enName = enName,
        )
        insertWays(sql, way)
    }
}


private fun insertRoads(
    sql: Connection,
    id: Long,
    elName: String,
    enName: String,
) {
    val insertSuburb = sql.prepareStatement(
        "INSERT INTO roads (way_id, el_Name, en_Name) VALUES (?, ?, ?)"
    )

    insertSuburb.setLong(1, id)
    insertSuburb.setString(2, elName)
    insertSuburb.setString(3, enName)

    insertSuburb.addBatch()
    insertSuburb.executeBatch()
    insertSuburb.close()
}
