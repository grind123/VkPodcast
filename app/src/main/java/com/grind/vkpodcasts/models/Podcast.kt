package com.grind.vkpodcasts.models

class Podcast {
    var logoUri: String? = null
    var name: String? = null
    var desc: String? = null
    var fileName: String? = null
    var backgroundTrack: SoundTrack? = null
    var emergency: Boolean = false
    var extinction: Boolean = false
    var durationString: String? = null
    var duration: Long? = null
    var timeCodesList: List<TimeCode>? = null

}