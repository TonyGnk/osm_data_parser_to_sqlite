import actions.connectRoads.connectTheRoads
import actions.getTheIdOfTheRoadL
import parser.insertNodeInRoads
import parser.insertNodePlaces
import parser.parseOsmForCoordinates
import parser.parseOsmL
import java.io.File

fun main() {

    val osmName = "mapFirstKoinotita" //"mapFirstKoinotita"

    val osmPath = "src/main/resources/$osmName.osm"
    val dbPath = "output/${osmName}.sqlite"
    if (File(dbPath).exists()) File(dbPath).delete()

    // 1. Create the tables
    createTables(dbPath)

    val start = System.currentTimeMillis()
    parseOsmL(osmPath = osmPath)
    val end = System.currentTimeMillis()
    println("Time spent: ${end - start} ms\n")

    val start2 = System.currentTimeMillis()
    parseOsmForCoordinates(osmPath)
    val end2 = System.currentTimeMillis()
    println("Time spent: ${end2 - start2} ms\n")


    val start3 = System.currentTimeMillis()
    getTheIdOfTheRoadL()
    val end3 = System.currentTimeMillis()
    println("Time spent: ${end3 - start3} ms\n")

    val start4 = System.currentTimeMillis()
    connectTheRoads()
    val end4 = System.currentTimeMillis()
    println("Time spent: ${end4 - start4} ms\n")

    insertNodePlaces()
    insertNodeInRoads()


    saveToDB(dbPath)
}
