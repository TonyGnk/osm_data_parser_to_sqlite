package parser.way

import utils.findTagWithName
import org.openstreetmap.osmosis.core.domain.v0_6.Way
import java.sql.Connection

fun insertRoadOrMultiPointPlace(
    insertPlaceStmt: java.sql.PreparedStatement,
    insertRoadStmt: java.sql.PreparedStatement,
    insertWayNodeStmt: java.sql.PreparedStatement,
    way: Way,
) {
    if (way.tags.isEmpty()) return

    val category = way.tags.findTagWithName(listOf("highway"))
    val isRoad = category.isNotBlank()

    when (isRoad) {
        true -> handleRoads(
            insertRoadStmt = insertRoadStmt,
            insertWayNodeStmt = insertWayNodeStmt,
            way = way
        )

        false -> handleMultiPoint(
            insertPlaceStmt = insertPlaceStmt,
            insertWayNodeStmt = insertWayNodeStmt,
            way = way
        )
    }
}
