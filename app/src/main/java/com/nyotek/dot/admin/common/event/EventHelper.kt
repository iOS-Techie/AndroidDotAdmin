package com.nyotek.dot.admin.common.event

object EventHelper {
    private lateinit var eventViewModel: EventViewModel

    fun init() {
        eventViewModel = EventViewModel()
    }

    fun getEventViewModel(): EventViewModel {
        return eventViewModel
    }
}