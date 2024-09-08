package parser

import parser.way.insertRoadOrMultiPointPlace
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer
import org.openstreetmap.osmosis.core.domain.v0_6.Node
import org.openstreetmap.osmosis.core.domain.v0_6.Way
import org.openstreetmap.osmosis.core.task.v0_6.Sink
import org.openstreetmap.osmosis.xml.common.CompressionMethod
import org.openstreetmap.osmosis.xml.v0_6.XmlReader
import parser.nodes.handleSinglePoint
import java.io.File
import java.sql.Connection
import java.sql.DriverManager

fun osmParser(
    osmPath: String, dbPath: String, type: ParserType,
) {
    print("Inserting $type...")
    val sql: Connection = DriverManager.getConnection("jdbc:sqlite:$dbPath")
    sql.autoCommit = false

    val reader = XmlReader(File(osmPath), false, CompressionMethod.None)

    val sink = object : Sink {
        override fun process(entityContainer: EntityContainer) {
            val entity = entityContainer.entity

            when (type) {
                ParserType.OTHER -> when (entity) {
                    is Node -> handleSinglePoint(sql, entity)
                    is Way -> insertRoadOrMultiPointPlace(sql, entity, placeSelected = true)
                }

                ParserType.ROADS -> when (entity) {
                    is Way -> insertRoadOrMultiPointPlace(sql, entity, placeSelected = false)
                }

            }
        }

        override fun close() {}
        override fun complete() {}
        override fun initialize(metaData: MutableMap<String, Any>?) {}
    }


    reader.setSink(sink)
    reader.run()

    sql.commit()
    sql.autoCommit = true
    sql.createStatement().execute("VACUUM")
    sql.close()

    println("Done")
}


enum class ParserType { ROADS, OTHER }
