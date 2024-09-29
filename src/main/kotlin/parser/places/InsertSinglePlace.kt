package parser.places

import org.openstreetmap.osmosis.core.domain.v0_6.Node
import parser.places.policies.getAddress
import parser.places.policies.getAddressNumber
import parser.places.policies.getCategoryId
import parser.places.policies.getName
import parser.roundToFiveDecimals
import utils.setIntOrNull
import utils.setStringOrNull

fun insertSinglePlace(
    insertPlaceStmt: java.sql.PreparedStatement, node: Node,
) {
    if (node.tags.isEmpty()) return

    val addresses = node.tags.getAddress()
    val category = node.tags.getCategoryId(addresses) ?: return

    val elAddress = addresses.first
    val enAddress = addresses.second

    val names = node.tags.getName()
    val elName = names.first
    val enName = names.second

    val addressNumber = node.tags.getAddressNumber()

    insertPlaceStmt.setStringOrNull(1, elName)
    insertPlaceStmt.setStringOrNull(2, enName)

    insertPlaceStmt.setStringOrNull(3, elAddress)
    insertPlaceStmt.setStringOrNull(4, enAddress)
    insertPlaceStmt.setIntOrNull(5, addressNumber)

    insertPlaceStmt.setDouble(6, roundToFiveDecimals(node.latitude))
    insertPlaceStmt.setDouble(7, roundToFiveDecimals(node.longitude))

    insertPlaceStmt.setInt(8, category)

    insertPlaceStmt.addBatch()
}
