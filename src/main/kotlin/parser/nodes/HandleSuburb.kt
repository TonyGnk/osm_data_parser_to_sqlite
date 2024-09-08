package nodes


import utils.findTagWithName
import org.openstreetmap.osmosis.core.domain.v0_6.Node
import java.sql.Connection

fun handleSuburb(sql: Connection, node: Node, subCategory: String) {

    val elName = node.tags.findTagWithName(listOf("name:el", "name"))
    val enName = node.tags.findTagWithName(listOf("name:en", "int_name"))

    val notEmptyNames = elName.isNotBlank() || enName.isNotBlank()

    if (notEmptyNames) insertSuburb(
        sql = sql,
        id = node.id,
        elName = elName,
        enName = enName,
        category = subCategory,
    )
}


private fun insertSuburb(
    sql: Connection,
    id: Long,
    elName: String,
    enName: String,
    category: String,
) {
    val insertSuburb = sql.prepareStatement(
        "INSERT INTO suburbs (node_id, elName, enName, category) VALUES (?, ?, ?, ?)"
    )

    insertSuburb.setLong(1, id)
    insertSuburb.setString(2, elName)
    insertSuburb.setString(3, enName)
    insertSuburb.setString(4, category)

    insertSuburb.addBatch()
    insertSuburb.executeBatch()
    insertSuburb.close()
}
