import actions.connectRoads.connectTheRoads
import actions.getTheIdOfTheRoadL
import parser.insertNodeInRoads
import parser.insertNodePlaces
import parser.parseOsmForCoordinates
import parser.parseOsmL
import java.io.File

fun main() {

    val osmName = "mapFirstKoinotita"

    val osmPath = "src/main/resources/$osmName.osm"
    val dbPath = "output/${osmName}.sqlite"
    if (File(dbPath).exists()) File(dbPath).delete()

    val tasks = listOf(

        { parseOsmL(osmPath) },
        { parseOsmForCoordinates(osmPath) },
        { getTheIdOfTheRoadL() },
        { connectTheRoads() },
        { insertNodePlaces() },
        { insertNodeInRoads() },

        // SQLite
        { createTables(dbPath) },
        { saveToDB(dbPath) }
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
