package com.google.ar.core.codelabs.hellogeospatial.utilities

import com.google.android.gms.maps.model.LatLng
import com.google.ar.core.codelabs.hellogeospatial.models.POILocation

/**
 * Representación de conectores intermedios entre dos POIs.
 * Se definen fuera de los POIs para no mezclar datos de render con datos de navegación.
 */
object RouteConfig {
    const val START_ID: String = "START"
    /** Clave de segmento: "fromId->toId" */
    fun routeKey(fromId: String, toId: String) = "$fromId->$toId"
    fun routeKeyStart(toId: String) = routeKey(START_ID, toId)
    fun routeKeyToStart(fromId: String) = routeKey(fromId, START_ID)

    /**
     * Mapa de conectores intermedios por segmento. Personaliza aquí tus puntos de unión.
     * Ejemplo:
     *   CONNECTORS[routeKey("monticulo_3","monticulo_7")] = listOf(
     *       LatLng(14.59240, -90.46065),
     *       LatLng(14.59237, -90.46062)
     *   )
     */
    val CONNECTORS: MutableMap<String, List<LatLng>> = mutableMapOf()

    init {
        CONNECTORS[routeKey("informacion_inicial","monticulo_6")] = listOf(
            LatLng(14.63141060, -90.54846954),
            LatLng(14.63158607, -90.54853058),
            LatLng(14.63171482, -90.54850769),
            LatLng(14.63184834, -90.54848480)
        )
        CONNECTORS[routeKey("monticulo_6","acropolis")] = listOf(
            LatLng(14.63199234, -90.54843903),
            LatLng(14.63208675, -90.54845428),
            LatLng(14.63213348, -90.54851532),
            LatLng(14.63215160, -90.54857635),
            LatLng(14.63217735, -90.54860687),
            LatLng(14.63232803, -90.54868317),
            LatLng(14.63245583, -90.54875183)
        )
        CONNECTORS[routeKey("acropolis","monticulo_3")] = listOf(
            LatLng(14.63252068, -90.54873657),
            LatLng(14.63262844, -90.54869843),
            LatLng(14.63270664, -90.54861450),
            LatLng(14.63281822, -90.54849243),
            LatLng(14.63291645, -90.54841614),
            LatLng(14.63302231, -90.54831696),
            LatLng(14.63315487, -90.54814911)
        )
        CONNECTORS[routeKey("monticulo_3","monticulo_5")] = listOf(
            LatLng(14.63302803, -90.54804230),
            LatLng(14.63293839, -90.54796600),
            LatLng(14.63282394, -90.54795837),
            LatLng(14.63269615, -90.54794312),
            LatLng(14.63256359, -90.54798889)
        )
        CONNECTORS[routeKey("monticulo_5","monticulo_7")] = listOf(
            LatLng(14.63241768, -90.54798126),
            LatLng(14.63225842, -90.54789734)
        )
        CONNECTORS[routeKey("monticulo_7","monticulo_8")] = listOf(
            LatLng(14.63218307, -90.54788971),
            LatLng(14.63209438, -90.54796600),
            LatLng(14.63202286, -90.54804993),
            LatLng(14.63189411, -90.54812622)
        )
        CONNECTORS[routeKey("monticulo_8","monticulo_12")] = listOf(
            LatLng(14.63174248, -90.54814148),
            LatLng(14.63163948, -90.54816437),
            LatLng(14.63151836, -90.54818726),
            LatLng(14.63139534, -90.54819489),
            LatLng(14.63113403, -90.54820251)
        )
        CONNECTORS[routeKey("monticulo_12","palangana")] = listOf(
            LatLng(14.63098431, -90.54824829),
            LatLng(14.63087177, -90.54821014),
            LatLng(14.63079166, -90.54817963),
            LatLng(14.63071537, -90.54816437),
            LatLng(14.63071251, -90.54811096),
            LatLng(14.63066578, -90.54807281),
            LatLng(14.63062763, -90.54802704),
            LatLng(14.63066769, -90.54792023),
            LatLng(14.63069344, -90.54781342),
            LatLng(14.63075733, -90.54759216)
        )
        CONNECTORS[routeKey("palangana","monticulo_14")] = listOf(
            LatLng(14.63063622, -90.54764557),
            LatLng(14.63058567, -90.54766846),
            LatLng(14.63052845, -90.54768372),
            LatLng(14.63046169, -90.54771423),
            LatLng(14.63039017, -90.54770660),
            LatLng(14.63041973, -90.54763794),
            LatLng(14.63045216, -90.54757690),
            LatLng(14.63045502, -90.54750824),
            LatLng(14.63044548, -90.54742432),
            LatLng(14.63059425, -90.54727173),
            LatLng(14.63068390, -90.54722595)
        )
        CONNECTORS[routeKey("monticulo_14","monticulo_13")] = listOf(
            LatLng(14.63066864, -90.54715729),
            LatLng(14.63061810, -90.54702759)
        )
        CONNECTORS[routeKeyToStart("monticulo_13")] = listOf(
            LatLng(14.63037395, -90.54730988),
            LatLng(14.63033676, -90.54745483),
            LatLng(14.63038635, -90.54757690),
            LatLng(14.63036823, -90.54768372),
            LatLng(14.63043118, -90.54778290),
            LatLng(14.63052559, -90.54791260),
            LatLng(14.63059330, -90.54804230),
            LatLng(14.63068295, -90.54814148),
            LatLng(14.63072681, -90.54820251),
            LatLng(14.63078117, -90.54824829),
            LatLng(14.63089085, -90.54828644),
            LatLng(14.63104153, -90.54838562),
            LatLng(14.63119030, -90.54845428)
        )
    }
}

/**
 * Construye una lista de waypoints en este orden:
 *   POI[i].position -> conectores(fromId->toId) -> POI[i+1].position -> ...
 * Si no hay conectores para un tramo, se usa el salto directo.
 */
fun buildRouteWaypoints(
    pois: List<POILocation>,
    connectorsByLeg: Map<String, List<LatLng>> = RouteConfig.CONNECTORS
): List<LatLng> {
    if (pois.isEmpty()) return emptyList()

    val out = mutableListOf<LatLng>()
    for (i in 0 until pois.size) {
        val current = pois[i]
        out.add(current.position)
        if (i < pois.lastIndex) {
            val next = pois[i + 1]
            val legKey = RouteConfig.routeKey(current.id, next.id)
            val connectors = connectorsByLeg[legKey]
            if (!connectors.isNullOrEmpty()) out.addAll(connectors)
            // El siguiente POI se añadirá en la próxima iteración como current.position
        }
    }
    return out
}

/** Construye el tramo desde el punto de inicio hasta el primer POI, aplicando conectores START->first.id si existen */
fun buildStartLeg(
    start: LatLng,
    first: POILocation,
    connectorsByLeg: Map<String, List<LatLng>> = RouteConfig.CONNECTORS
): List<LatLng> {
    val out = mutableListOf<LatLng>()
    out.add(start)
    val connectors = connectorsByLeg[RouteConfig.routeKeyStart(first.id)]
    if (!connectors.isNullOrEmpty()) out.addAll(connectors)
    out.add(first.position)
    return out
}

/** Construye el tramo desde el último POI hasta el punto de inicio, aplicando conectores last.id->START si existen */
fun buildReturnLeg(
    last: POILocation,
    start: LatLng,
    connectorsByLeg: Map<String, List<LatLng>> = RouteConfig.CONNECTORS
): List<LatLng> {
    val out = mutableListOf<LatLng>()
    out.add(last.position)
    val connectors = connectorsByLeg[RouteConfig.routeKeyToStart(last.id)]
    if (!connectors.isNullOrEmpty()) out.addAll(connectors)
    out.add(start)
    return out
}

/** Ruta completa incluyendo el tramo desde START hasta el primer POI */
fun buildFullRouteIncludingStart(
    start: LatLng,
    pois: List<POILocation>,
    connectorsByLeg: Map<String, List<LatLng>> = RouteConfig.CONNECTORS
): List<LatLng> {
    if (pois.isEmpty()) return listOf(start)
    val full = mutableListOf<LatLng>()
    full.addAll(buildStartLeg(start, pois.first(), connectorsByLeg))
    // Evita duplicar el primer POI (ya añadido al final del tramo START->first)
    val rest = buildRouteWaypoints(pois, connectorsByLeg).drop(1)
    full.addAll(rest)
    return full
}

// Connectors-only helpers (exclude endpoints) so POIs are NOT treated as connector targets
fun connectorsForStartLeg(first: POILocation,
    connectorsByLeg: Map<String, List<LatLng>> = RouteConfig.CONNECTORS
): List<LatLng> = connectorsByLeg[RouteConfig.routeKeyStart(first.id)] ?: emptyList()

fun connectorsForPoiLeg(from: POILocation, to: POILocation,
    connectorsByLeg: Map<String, List<LatLng>> = RouteConfig.CONNECTORS
): List<LatLng> = connectorsByLeg[RouteConfig.routeKey(from.id, to.id)] ?: emptyList()

fun connectorsForReturnLeg(last: POILocation,
    connectorsByLeg: Map<String, List<LatLng>> = RouteConfig.CONNECTORS
): List<LatLng> = connectorsByLeg[RouteConfig.routeKeyToStart(last.id)] ?: emptyList()
