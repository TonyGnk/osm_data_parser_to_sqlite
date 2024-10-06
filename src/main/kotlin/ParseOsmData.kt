import data.fullNodesMap
import data.fullWays
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer
import org.openstreetmap.osmosis.core.domain.v0_6.Node
import org.openstreetmap.osmosis.core.domain.v0_6.Way
import org.openstreetmap.osmosis.core.task.v0_6.Sink
import org.openstreetmap.osmosis.xml.common.CompressionMethod
import org.openstreetmap.osmosis.xml.v0_6.XmlReader
import utils.withDots
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

suspend fun parseOsmData(osmPath: String) = withContext(Dispatchers.Default) {
    print("Parsing...")

    val nodeCount = AtomicInteger(0)
    val wayCount = AtomicInteger(0)

    val reader = XmlReader(File(osmPath), false, CompressionMethod.None)

    val sink = object : Sink {
        override fun process(entityContainer: EntityContainer) {
            when (val entity = entityContainer.entity) {
                is Node -> {
                    fullNodesMap[entity.id] = entity
                    nodeCount.incrementAndGet()
                }

                is Way -> {
                    fullWays.add(entity)
                    wayCount.incrementAndGet()
                }
            }
        }

        override fun close() {}
        override fun complete() {}
        override fun initialize(metaData: MutableMap<String, Any>?) {}
    }

    reader.setSink(sink)
    reader.run()

    println("OK: ${nodeCount.get().withDots()} nodes ${wayCount.get().withDots()} ways\n")
}
