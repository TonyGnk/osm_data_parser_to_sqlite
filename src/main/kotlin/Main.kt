import actions.connectRoadParts
import actions.convertRoadPartsToRoads
import convert.convertNodesToLocations
import convert.convertWaysToLocations
import convert.convertWaysToRoads
import data.globalGlassList
import data.globalLocationGlassList
import data.globalRoadGlassList
import kotlinx.coroutines.runBlocking
import sql.createTables2
import sql.saveToDatabase

const val BATCH_SIZE = 1000

fun main() {

    val osmName = "smallFile"//"largeFile"

    val osmPath = "src/main/resources/$osmName.osm"
    val dbPath = "output/${osmName}.sqlite"


    runBlocking { parseOsmData(osmPath) }

    //Now we have a 2 lists of nodes and ways
    val tasks = listOf(
        { convertWaysToRoads() },

        { connectRoadParts() },
        { convertRoadPartsToRoads() },

        { convertWaysToLocations() },
        { convertNodesToLocations() },

        //SQLite
        { createTables2(dbPath) },
        {
            println("Glass count: ${globalGlassList.size}")
            println("Locations count: ${globalLocationGlassList.size}")
            println("Roads count: ${globalRoadGlassList.size}")
        },
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
