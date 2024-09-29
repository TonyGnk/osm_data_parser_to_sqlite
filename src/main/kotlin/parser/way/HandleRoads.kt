package parser.way

import org.openstreetmap.osmosis.core.domain.v0_6.Way
import parser.insertWays
import parser.places.policies.getName
import utils.setStringOrNull


fun handleRoads(
    insertRoadStmt: java.sql.PreparedStatement,
    insertWayNodeStmt: java.sql.PreparedStatement,
    way: Way
) {
    val names = way.tags.getName()
    val elName = names.first
    val enName = names.second

    val notEmptyNames = elName != null || enName != null

    if (notEmptyNames) {
        insertRoadStmt.setLong(1, way.id)
        insertRoadStmt.setStringOrNull(2, elName)
        insertRoadStmt.setStringOrNull(3, enName)

        insertRoadStmt.addBatch()

        insertWays(
            insertWayNodeStmt = insertWayNodeStmt,
            way = way
        )
    }
}
