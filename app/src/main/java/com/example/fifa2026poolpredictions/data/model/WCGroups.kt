package com.example.fifa2026poolpredictions.data.model

data class WcGroup(
    val letter: String,
    val teams: List<String>
)

val WC_GROUPS = listOf(
    WcGroup("A", listOf("Mexico", "South Africa", "South Korea", "Czechia")),
    WcGroup("B", listOf("Canada", "Switzerland", "Qatar", "Bosnia and Herzegovina")),
    WcGroup("C", listOf("Brazil", "Morocco", "Haiti", "Scotland")),
    WcGroup("D", listOf("United States", "Paraguay", "Australia", "Türkiye")),
    WcGroup("E", listOf("Germany", "Curaçao", "Ivory Coast", "Ecuador")),
    WcGroup("F", listOf("Netherlands", "Japan", "Sweden", "Tunisia")),
    WcGroup("G", listOf("Belgium", "Egypt", "Iran", "New Zealand")),
    WcGroup("H", listOf("Spain", "Cape Verde", "Saudi Arabia", "Uruguay")),
    WcGroup("I", listOf("France", "Senegal", "Norway", "Iraq")),
    WcGroup("J", listOf("Argentina", "Algeria", "Austria", "Jordan")),
    WcGroup("K", listOf("Portugal", "Congo DR", "Uzbekistan", "Colombia")),
    WcGroup("L", listOf("England", "Croatia", "Ghana", "Panama"))
)
