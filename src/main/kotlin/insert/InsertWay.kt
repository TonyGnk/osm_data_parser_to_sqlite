package insert

import org.openstreetmap.osmosis.core.domain.v0_6.Way
import java.sql.Connection


fun insertWays(connection: Connection, way: Way) {
    val insertWayNode = connection.prepareStatement(
        "INSERT INTO way_nodes (way_id, node_id, sequence) VALUES (?, ?, ?)"
    )


    way.wayNodes.forEachIndexed { index, wayNode ->
        insertWayNode.setLong(1, way.id)
        insertWayNode.setLong(2, wayNode.nodeId)
        insertWayNode.setInt(3, index)
        insertWayNode.addBatch()
    }

    insertWayNode.executeBatch()
    insertWayNode.close()
}
