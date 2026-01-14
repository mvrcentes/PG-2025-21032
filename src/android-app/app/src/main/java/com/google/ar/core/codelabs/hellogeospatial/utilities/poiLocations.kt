package com.google.ar.core.codelabs.hellogeospatial.utilities

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import com.google.ar.core.codelabs.hellogeospatial.R
import com.google.ar.core.codelabs.hellogeospatial.models.POILocation
import com.google.ar.core.codelabs.hellogeospatial.models.ModelPart

public fun getPoiLocations(context: Context): List<POILocation> {
    return listOf(
        POILocation(
            "informacion_inicial",
            LatLng(14.63131332, -90.54845428),
            context.getString(R.string.poi_kaminaljuyu_title),
            modelPath = "models/informacion/Oval_Loc_OBJ_Format.obj",
            texturePath = "models/textures/Oval_Loc_Red.png",
            description = context.getString(R.string.poi_kaminaljuyu_description),
            historicalInfo = "",
            constructionPeriod = "",
            height = "",
            archaeologicalFeatures = emptyList(),
            culturalSignificance = "",
            images = listOf("inicio"),
            baseScale = 0.2f,
            baseAltitudeMeters = -1f,
            baseYawDeg = 0f
        ),
        POILocation(
            "monticulo_6",
            LatLng(14.631895407440561, -90.54859084940932),
            context.getString(R.string.poi_monticulo_6_title),
            modelPath = "models/informacion/Oval_Loc_OBJ_Format.obj",
            texturePath = "models/textures/Oval_Loc_Red.png",
            description = context.getString(R.string.poi_monticulo_6_findings),
            historicalInfo = "",
            constructionPeriod = "Preclásico Tardío-Terminal - Clásico Tardío",
            height = "",
            archaeologicalFeatures = emptyList(),
            culturalSignificance = "",
            baseScale = 0.2f,
            baseAltitudeMeters = -1f,
            baseYawDeg = 0f
        ),
        POILocation(
            "acropolis",
            LatLng(14.632549261303337, -90.54889359532447),
            context.getString(R.string.poi_acropolis_title),
            modelPath = "models/informacion/Oval_Loc_OBJ_Format.obj",
            texturePath = "models/textures/Oval_Loc_Red.png",
            description = context.getString(R.string.poi_acropolis_findings) + "\n\n" + context.getString(R.string.poi_acropolis_findings_additional),
            historicalInfo = context.getString(R.string.poi_acropolis_function),
            constructionPeriod = "Clásico Temprano - Clásico Tardío",
            height = "",
            archaeologicalFeatures = emptyList(),
            culturalSignificance = "",
            images = listOf("c_ii_4"),
            baseScale = 0.2f,
            baseAltitudeMeters = -1f,
            baseYawDeg = 0f
        ),
        POILocation(
            "monticulo_3",
            LatLng(14.633263117257021, -90.54812859260066),
            context.getString(R.string.poi_monticulo_3_title),
            baseScale = 0.2f,
            baseAltitudeMeters = -3f,
            baseYawDeg = 0f,
            description = context.getString(R.string.poi_monticulo_3_findings),
            historicalInfo = "",
            constructionPeriod = "Preclásico Tardío - Clásico Tardío",
            height = "",
            archaeologicalFeatures = emptyList(),
            culturalSignificance = "",
            images = emptyList(),
            parts = listOf(
                ModelPart("models/monticulo-3/monticulo-3-techo.obj", "models/textures/reed_roof_03_diff_4k.jpg", baseScale=0.8f),
                ModelPart("models/monticulo-3/monticulo-3-rancho.obj", "models/textures/114009464-textura-de-palo-de-pino-fondo-de-madera-de-pinos-para-diseño.jpg", baseScale=0.8f),
                ModelPart("models/monticulo-3/monticulo-3-puerta.obj","models/textures/fondo-textura-madera-abstracta_23-2148931508.png", baseScale=0.8f),
                ModelPart("models/monticulo-3/monticulo-3-plane003.obj","models/textures/clay_plaster_diff_4k.jpg", baseScale=0.8f),
                ModelPart("models/monticulo-3/monticulo-3-plane001.obj","models/textures/clay_plaster_diff_4k.jpg", baseScale=0.8f),
                ModelPart("models/monticulo-3/monticulo-3-plane.obj","models/textures/clay_plaster_diff_4k.jpg", baseScale=0.8f)
            ),
        ),
        POILocation(
            "monticulo_5",
            LatLng(14.632555675181031, -90.54807733211727),
            context.getString(R.string.poi_monticulo_5_title),
            modelPath = "models/informacion/Oval_Loc_OBJ_Format.obj",
            texturePath = "models/textures/Oval_Loc_Red.png",
            description = context.getString(R.string.poi_monticulo_5_findings),
            historicalInfo = "",
            constructionPeriod = "Preclásico Tardío - Clásico Tardío",
            height = "",
            archaeologicalFeatures = emptyList(),
            culturalSignificance = "",
            baseScale = 0.2f,
            baseAltitudeMeters = -1f,
            baseYawDeg = 0f
        ),
        POILocation(
            "monticulo_7",
            LatLng(14.632234919514932, -90.54800651965228),
            context.getString(R.string.poi_monticulo_7_title),
            description = context.getString(R.string.poi_monticulo_7_findings),
            historicalInfo = "",
            constructionPeriod = "Preclásico Tardío-Terminal - Clásico Tardío",
            height = "",
            archaeologicalFeatures = emptyList(),
            culturalSignificance = "",
            baseScale = 0.2f,
            baseAltitudeMeters = -3f,
            baseYawDeg = 0f,
            parts = listOf(
                ModelPart("models/monticulo-7/monticulo-7-Rancho_techo.obj", "models/textures/reed_roof_03_diff_4k.jpg", baseScale=0.8f),
                ModelPart("models/monticulo-7/monticulo-7-Puerta.obj", "models/textures/fondo-textura-madera-abstracta_23-2148931508.png", baseScale=0.8f),
                ModelPart("models/monticulo-7/monticulo-7-Plane004.obj","models/textures/clay_plaster_diff_4k.jpg", baseScale=0.8f),
                ModelPart("models/monticulo-7/monticulo-7-Plane003.obj","models/textures/reed_roof_03_diff_4k.jpg", baseScale=0.8f),
                ModelPart("models/monticulo-7/monticulo-7-Plane002.obj","models/textures/clay_plaster_diff_4k.jpg", baseScale=0.8f),
                ModelPart("models/monticulo-7/monticulo-7-Plane001.obj","models/textures/clay_plaster_diff_4k.jpg", baseScale=0.8f),
                ModelPart("models/monticulo-7/monticulo-7-Plane.obj","models/textures/clay_plaster_diff_4k.jpg", baseScale=0.8f)
            )
        ),
        POILocation(
            "monticulo_8",
            LatLng(14.631888413377396, -90.54808529662705),
            context.getString(R.string.poi_monticulo_8_title),
            modelPath = "models/informacion/Oval_Loc_OBJ_Format.obj",
            texturePath = "models/textures/Oval_Loc_Red.png",
            description = context.getString(R.string.poi_monticulo_8_findings),
            historicalInfo = "",
            constructionPeriod = "Preclásico Tardío-Terminal - Clásico Tardío",
            height = "",
            archaeologicalFeatures = emptyList(),
            culturalSignificance = "",
            images = listOf("c_ii_8"),
            baseScale = 0.2f,
            baseAltitudeMeters = -1f,
            baseYawDeg = 0f
        ),
        POILocation(
            "monticulo_12",
            LatLng(14.631110755220972, -90.54816619269748),
            context.getString(R.string.poi_monticulo_12_title),
            description = context.getString(R.string.poi_monticulo_12_findings),
            historicalInfo = context.getString(R.string.poi_monticulo_12_function),
            constructionPeriod = "Preclásico Tardío-Terminal",
            height = "",
            archaeologicalFeatures = emptyList(),
            culturalSignificance = "",
            images = listOf("c_ii_12", "c_ii_12_2"),
            baseScale = 0.2f,
            baseAltitudeMeters = -3f,
            baseYawDeg = 0f,
            parts = listOf(
                ModelPart("models/monticulo-12/monticulo-12-Rancho_techo.obj", "models/textures/reed_roof_03_diff_4k.jpg", baseScale=0.8f),
                ModelPart("models/monticulo-12/monticulo-12-Puerta.obj", "models/textures/fondo-textura-madera-abstracta_23-2148931508.png", baseScale=0.8f),
                ModelPart("models/monticulo-12/monticulo-12-Rancho.obj","models/textures/clay_plaster_diff_4k.jpg", baseScale=0.8f),
                ModelPart("models/monticulo-12/monticulo-12-Templo_3.obj","models/textures/clay_plaster_diff_4k.jpg", baseScale=0.8f),
                ModelPart("models/monticulo-12/monticulo-12-Templo_1_y_2.obj","models/textures/clay_plaster_diff_4k.jpg", baseScale=0.8f),
                ModelPart("models/monticulo-12/monticulo-12-Gradas.obj","models/textures/clay_plaster_diff_4k.jpg", baseScale=0.8f),
                ModelPart("models/monticulo-12/monticulo-12-Escalinatas.obj","models/textures/clay_plaster_diff_4k.jpg", baseScale=0.8f)
            )
        ),
        POILocation(
            "palangana",
            LatLng(14.630813964263574, -90.54760512338584),
            context.getString(R.string.poi_palangana_title),
            baseScale = 0.5f,
            baseAltitudeMeters = -3f,
            baseYawDeg = 90f,
            description = context.getString(R.string.poi_palangana_intro) + "\n\n" +
                context.getString(R.string.poi_palangana_clasico_tardio_title) + "\n" +
                context.getString(R.string.poi_palangana_etapa5_title) + " " + context.getString(R.string.poi_palangana_etapa5) + "\n" +
                context.getString(R.string.poi_palangana_etapa4_title) + " " + context.getString(R.string.poi_palangana_etapa4),
            historicalInfo = context.getString(R.string.poi_palangana_clasico_temprano_title) + "\n\n" +
                context.getString(R.string.poi_palangana_etapa3_title) + "\n" +
                context.getString(R.string.poi_palangana_etapa3c) + "\n" +
                context.getString(R.string.poi_palangana_etapa3b) + "\n" +
                context.getString(R.string.poi_palangana_etapa3a) + "\n\n" +
                context.getString(R.string.poi_palangana_etapa2_title) + "\n" +
                context.getString(R.string.poi_palangana_etapa2c) + "\n" +
                context.getString(R.string.poi_palangana_etapa2b) + "\n" +
                context.getString(R.string.poi_palangana_etapa2a) + "\n\n" +
                context.getString(R.string.poi_palangana_etapa1_title) + "\n" +
                context.getString(R.string.poi_palangana_etapa1),
            constructionPeriod = "Clásico Temprano - Clásico Tardío",
            height = "",
            archaeologicalFeatures = emptyList(),
            culturalSignificance = context.getString(R.string.poi_palangana_etapa3_function) + "\n" +
                context.getString(R.string.poi_palangana_etapa2_function) + "\n" +
                context.getString(R.string.poi_palangana_etapa1_function),
            images = listOf("e", "etapa3", "etapa3a", "etapa2a", "etapa2b", "etapa2c", "etapa1"),
            parts = listOf(
                ModelPart("models/palangana/palangana-Talud-1.2.obj", "models/textures/clay_plaster_diff_4k.jpg", baseScale=0.8f),
                ModelPart("models/palangana/palangana-tablero-1.1.obj", "models/textures/clay_plaster_diff_4k.jpg", baseScale=0.8f),
                ModelPart("models/palangana/palangana-pasillo.obj","models/textures/clay_plaster_diff_4k.jpg", baseScale=0.8f),
                ModelPart("models/palangana/palangana-palangana.obj","models/textures/clay_plaster_diff_4k.jpg", baseScale=0.8f),
                ModelPart("models/palangana/palangana-gradas.obj","models/textures/clay_plaster_diff_4k.jpg", baseScale=0.8f),
                ModelPart("models/palangana/palangana-escalinatas.obj","models/textures/clay_plaster_diff_4k.jpg", baseScale=0.8f),
                ModelPart("models/palangana/palangana-bloque-largo.obj","models/textures/clay_plaster_diff_4k.jpg", baseScale=0.8f),
                ModelPart("models/palangana/palangana-bloque-2.obj","models/textures/clay_plaster_diff_4k.jpg", baseScale=0.8f),
                ModelPart("models/palangana/palangana-bloque-1.obj","models/textures/clay_plaster_diff_4k.jpg", baseScale=0.8f)
            )
        ),
        POILocation(
            "monticulo_14",
            LatLng(14.630593990926299, -90.54696166473626),
            context.getString(R.string.poi_monticulo_14_title),
            modelPath = "models/monticulo-14/monticulo-14.obj",
            texturePath = "models/textures/clay_plaster_diff_4k.jpg",
            baseScale = 0.2f,
            baseAltitudeMeters = -3f,
            baseYawDeg = 0f,
            description = context.getString(R.string.poi_monticulo_14_findings),
            historicalInfo = context.getString(R.string.poi_monticulo_14_function),
            constructionPeriod = "Preclásico Tardío-Terminal - Clásico Tardío",
            height = "",
            archaeologicalFeatures = emptyList(),
            culturalSignificance = "",
            // parts = listOf(
            //     ModelPart("models/monticulo-14/monticulo-14-Talud_2.obj", "models/textures/red_plaster_weathered_diff_4k.jpg", baseScale=0.8f),
            //     ModelPart("models/monticulo-14/monticulo-14-Talud_1.obj", "models/textures/red_plaster_weathered_diff_4k.jpg", baseScale=0.8f),
            //     ModelPart("models/monticulo-14/monticulo-14-tablero_2.obj","models/textures/red_plaster_weathered_diff_4k.jpg", baseScale=0.8f),
            //     ModelPart("models/monticulo-14/monticulo-14-Tablero_1_.obj","models/textures/red_plaster_weathered_diff_4k.jpg", baseScale=0.8f),
            //     ModelPart("models/monticulo-14/monticulo-14-Rancho-Techo.obj","models/textures/reed_roof_03_diff_4k.jpg", baseScale=0.8f),
            //     ModelPart("models/monticulo-14/monticulo-14-Rancho-Puerta2.obj","models/textures/fondo-textura-madera-abstracta_23-2148931508.png", baseScale=0.8f),
            //     ModelPart("models/monticulo-14/monticulo-14-Rancho-Puerta.obj","models/textures/fondo-textura-madera-abstracta_23-2148931508.png", baseScale=0.8f),
            //     ModelPart("models/monticulo-14/monticulo-14-Rancho.obj","models/textures/fondo-textura-madera-abstracta_23-2148931508.png", baseScale=0.8f),
            //     ModelPart("models/monticulo-14/monticulo-14-Gradas.obj","models/textures/red_plaster_weathered_diff_4k.jpg", baseScale=0.8f),
            //     ModelPart("models/monticulo-14/monticulo-14-Cosa_de_gradas.obj","models/textures/red_plaster_weathered_diff_4k.jpg", baseScale=0.8f)
            // )
        ),
        POILocation(
            "monticulo_13",
            LatLng(14.630643799200827, -90.5469887688274),
            context.getString(R.string.poi_monticulo_13_title),
            baseScale = 0.2f,
            baseAltitudeMeters = -3f,
            baseYawDeg = 0f,
            description = context.getString(R.string.poi_monticulo_13_findings),
            historicalInfo = context.getString(R.string.poi_monticulo_13_function),
            constructionPeriod = "Preclásico Tardío-Terminal - Clásico Tardío",
            height = "",
            archaeologicalFeatures = emptyList(),
            culturalSignificance = "",
            images = listOf("c_ii_13", "c_ii_13_2", "c_ii_13_3"),
            parts = listOf(
                ModelPart("models/monticulo-13/monticulo-13-Plane004.obj", "models/textures/reed_roof_03_diff_4k.jpg", baseScale=0.8f),
                ModelPart("models/monticulo-13/monticulo-13-Plane003.obj", "models/textures/114009464-textura-de-palo-de-pino-fondo-de-madera-de-pinos-para-diseño.jpg", baseScale=0.8f),
                ModelPart("models/monticulo-13/monticulo-13-Plane002.obj","models/textures/red_plaster_weathered_diff_4k.jpg", baseScale=0.8f),
                ModelPart("models/monticulo-13/monticulo-13-Plane001.obj","models/textures/clay_plaster_diff_4k.jpg", baseScale=0.8f),
                ModelPart("models/monticulo-13/monticulo-13-Plane.obj","models/textures/clay_plaster_diff_4k.jpg", baseScale=0.8f),
                ModelPart("models/monticulo-13/monticulo-13-Gradas.obj","models/textures/clay_plaster_diff_4k.jpg", baseScale=0.8f)
            )
        ),
    )
}