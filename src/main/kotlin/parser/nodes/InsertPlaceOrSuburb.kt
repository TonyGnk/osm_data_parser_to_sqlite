package nodes

import utils.findTagWithName
import parser.nodes.handleSinglePoint
import org.openstreetmap.osmosis.core.domain.v0_6.Node
import parser.possibleDistricts
import parser.possibleSubDistricts
import java.sql.Connection

//fun insertPlaceOrSuburb(
//    sql: Connection,
//    node: Node,
//    suburbIsSelected: Boolean,
//) {
//    val category = node.tags.findTagWithName(possibleDistricts)
//    val subCategory = if (
//        category.isNotBlank() && category in possibleSubDistricts
//    ) category else ""
//
//    val isSuburb = subCategory.isNotBlank()
//
//    when (isSuburb) {
//        true -> if (suburbIsSelected) handleSuburb(sql, node, subCategory)
//        false -> if (!suburbIsSelected) handleSinglePoint(sql, node)
//    }
//}
