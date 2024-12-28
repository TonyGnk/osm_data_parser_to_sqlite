import actions.connectRoadParts
import actions.convertRoadPartsToRoads
import actions.removeDuplicateSuburbs
import convert.convertNodesToLocations
import convert.convertNodesToSuburbs
import convert.convertWaysToLocations
import convert.convertWaysToRoads
import convert.convertWaysToSuburb
import data.globalGlassList
import data.globalCoordinateList
import data.globalRoadGlassList
import kotlinx.coroutines.runBlocking
import sql.createTables
import sql.saveToDatabase

const val BATCH_SIZE = 1000

fun main() {

    val osmName = "largeFile"// "smallFile"//"largeFile"

    val osmPath = "src/main/resources/$osmName.osm"
    val dbPath = "output/${osmName}.sqlite"


    runBlocking { parseOsmData(osmPath) }

    //Now we have a 2 lists of nodes and ways
    val tasks = listOf(
        { convertWaysToSuburb() },
        { convertNodesToSuburbs() },
        { removeDuplicateSuburbs() },

        { convertWaysToRoads() },

        { connectRoadParts() },
        { convertRoadPartsToRoads() },

        { convertWaysToLocations() },
        { convertNodesToLocations() },

        //SQLite
        { createTables(dbPath) },
        {
            println("Glass count: ${globalGlassList.size}")
            println("Locations count: ${globalCoordinateList.size}")
            println("Roads count: ${globalRoadGlassList.size}")
            println("Suburbs count: ${globalCoordinateList.size}")
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
