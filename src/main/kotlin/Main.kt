import actions.connectTheRoads
import convert.convertNodesToLocations
import convert.convertWaysToLocations
import convert.convertWaysToRoads
import kotlinx.coroutines.runBlocking
import sql.createTables
import sql.saveToDatabase

const val BATCH_SIZE = 1000

fun main() {

    val osmName = "smallFile"//"largeFile"

    val osmPath = "src/main/resources/$osmName.osm"
    val dbPath = "output/${osmName}.sqlite"


    runBlocking { parseOsmData(osmPath) }

    val tasks = listOf(
        { convertWaysToRoads() },
        { convertWaysToLocations() },
        { convertNodesToLocations() },

        { connectTheRoads() },

        // SQLite
        { createTables(dbPath) },
        { saveToDatabase(dbPath) }
    )

    tasks.forEach { task ->
        startWithTime(task)
    }
}

private fun startWithTime(function: () -> Unit) {
    val startTime = System.currentTimeMillis()
    function()
    val endTime = System.currentTimeMillis()

    println("Time spent:  ${endTime - startTime} ms\n")
}
