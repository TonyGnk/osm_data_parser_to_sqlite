import parser.osmParser
import parser.parseOsmForCoordinates
import java.io.File

var nodesOut = 0
var nodesIn = 0

fun main() {

    val osmName = "allMap3"

    val osmPath = "src/main/resources/$osmName.osm"
    val dbPath = "output/${osmName}.sqlite"
    if (File(dbPath).exists()) File(dbPath).delete()


    // 1. Create the tables
    createTables(dbPath)

    val start = System.currentTimeMillis()
    osmParser(osmPath = osmPath, dbPath = dbPath)
    val end = System.currentTimeMillis()
    println("Time spent: ${end - start} ms\n")

    print("Nodes in: $nodesIn,")
    println(" out: $nodesOut")
    //Percent
    println("Percent: ${nodesOut.toDouble() / nodesIn * 100}%")

    //connectTheRoads(dbPath)

    //osmParser(osmPath = osmPath, dbPath = dbPath, type = ParserType.OTHER)

    parseOsmForCoordinates(osmPath, dbPath)
}
