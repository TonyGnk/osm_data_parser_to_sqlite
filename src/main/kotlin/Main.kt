import actions.connectRoads.connectTheRoads
import parser.ParserType
import parser.osmParser
import parser.parseOsmForCoordinates
import java.io.File

/*
    SCRIPT
    1. Create the tables
 */

fun main() {

    val osmName = "allMap3"

    val osmPath = "src/main/resources/$osmName.osm"
    val dbPath = "output/${osmName}.sqlite"
    if (File(dbPath).exists()) File(dbPath).delete()


    // 1. Create the tables
    createTables(dbPath)


    osmParser(osmPath = osmPath, dbPath = dbPath, type = ParserType.ROADS)

    connectTheRoads(dbPath)

    osmParser(osmPath = osmPath, dbPath = dbPath, type = ParserType.OTHER)

    //val start = System.currentTimeMillis()

    parseOsmForCoordinates(osmPath, dbPath)

    // val end = System.currentTimeMillis()
    //  println("Time spent in osmParserForCoordinates: ${end - start} ms")


//    val manualEnableCleanEmptyNodes = true
//    val cleanEmptyNodes = if (manualEnableCleanEmptyNodes) true else {
//        println("Do you want to clean nodes that are not used? (y/n)")
//        readlnOrNull() == "y"
//    }
//    if (cleanEmptyNodes) cleanEmptyNodesAction(dbPath)
}
