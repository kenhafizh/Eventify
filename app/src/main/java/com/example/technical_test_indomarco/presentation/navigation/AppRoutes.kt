package com.example.technical_test_indomarco.presentation.navigation

enum class AppRoutes(val route: String) {
    SPLASH("splash"),
    LOGIN("login"),
    HOME("home"),
    DETAIL("detail/{eventId}"),
    EVENT_LIST("event_list"),
    ADD_EVENT("add_event");

    companion object {
        fun detailRoute(eventId: Int): String = "detail/$eventId"
    }
}

