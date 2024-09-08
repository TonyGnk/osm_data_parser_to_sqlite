package parser

import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer
import org.openstreetmap.osmosis.core.domain.v0_6.Node
import org.openstreetmap.osmosis.core.domain.v0_6.Way
import org.openstreetmap.osmosis.core.task.v0_6.Sink
import org.openstreetmap.osmosis.xml.common.CompressionMethod
import org.openstreetmap.osmosis.xml.v0_6.XmlReader
import utils.findTagWithName
import java.io.File
import java.sql.Connection
import java.sql.DriverManager

fun parseOsmForSuburbs(osmPath: String, dbPath: String) {
    print("Inserting nodes...")
    DriverManager.getConnection("jdbc:sqlite:$dbPath").use { connection ->
        connection.autoCommit = false

        processOsmFile(osmPath, connection)

        connection.commit()
    }
    println("Done")
}

private fun processOsmFile(
    osmFilePath: String, connection: Connection,
) {
    val reader = XmlReader(File(osmFilePath), false, CompressionMethod.None)
    val sink = createOsmSink(connection)
    reader.setSink(sink)
    reader.run()
}

private fun createOsmSink(connection: Connection): Sink {
    return object : Sink {
        override fun process(entityContainer: EntityContainer) {
            when (val entity = entityContainer.entity) {
                is Node -> {
                    val category = entity.tags.findTagWithName(possibleDistricts)
                    val subCategory = if (
                        category.isNotBlank() && category in possibleSubDistricts
                    ) category else ""

                    val isSuburb = subCategory.isNotBlank()
                    if (isSuburb) handleSuburb(connection, entity, subCategory)
                }

                is Way -> {
                    val category = entity.tags.findTagWithName(possibleDistricts)
                    val subCategory = if (
                        category.isNotBlank() && category in possibleSubDistricts
                    ) category else ""

                    val isSuburb = subCategory.isNotBlank()
                    if (isSuburb) handleSuburbWay(connection, entity, subCategory)
                }
            }
        }

        override fun close() {}
        override fun complete() {}
        override fun initialize(metaData: MutableMap<String, Any>?) {}
    }
}

private fun handleSuburb(sql: Connection, node: Node, subCategory: String) {

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

private fun handleSuburbWay(sql: Connection, way: Way, subCategory: String) {

    val elName = way.tags.findTagWithName(listOf("name:el", "name"))
    val enName = way.tags.findTagWithName(listOf("name:en", "int_name"))

    val notEmptyNames = elName.isNotBlank() || enName.isNotBlank()

    if (notEmptyNames) insertSuburb(
        sql = sql,
        id = way.id,
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
    sql.prepareStatement(
        "INSERT INTO suburbs (node_id, elName, enName, category) VALUES (?, ?, ?, ?)"
    ).use { statement ->
        statement.setLong(1, id)
        statement.setString(2, elName)
        statement.setString(3, enName)
        statement.setString(4, category)
        statement.executeUpdate()
    }
}

val possibleDistricts = listOf(
    "landuse",
)

val possibleSubDistricts = listOf(
    "residential",
//    "suburb",
//    "neighbourhood",
//    "civic",
//    "city",
//    "hamlet",
//    "quarter",
//    "village",
)
