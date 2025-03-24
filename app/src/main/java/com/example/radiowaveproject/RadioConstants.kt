package com.example.radiowaveproject

object RadioConstants {
    const val ACTION_START = "com.example.radiowaveproject.ACTION_START"
    const val ACTION_STOP = "com.example.radiowaveproject.ACTION_STOP"
    const val EXTRA_STATION_NAME = "EXTRA_STATION_NAME"

    const val LOG_BROADCAST = "com.example.radiowaveproject.LOG_EVENT"


    const val FEATURE_RADIO_FM = "android.hardware.radio.fm"

    val radioStations = mapOf(
        "FIP Radio" to "https://icecast.radiofrance.fr/fip-midfi.mp3",
        "KEXP 90.3 FM (Seattle)" to "https://kexp-mp3-128.streamguys1.com/kexp128.mp3",
        "Jazz FM (UK)" to "http://media-ice.musicradio.com/JazzFMMP3",
        "France Inter" to "https://icecast.radiofrance.fr/franceinter-midfi.mp3",
        "Deutschlandfunk" to "https://live.deutschlandfunk.de/deutschlandfunk/live/stream.mp3",
        "Deutschlandfunk Kultur" to "https://live.deutschlandfunkkultur.de/deutschlandfunkkultur/live/stream.mp3",
        "Deutschlandfunk Nova" to "https://live.deutschlandfunknova.de/deutschlandfunknova/live/stream.mp3",
        "Deutschlandfunk Dok" to "https://live.deutschlandfunkdok.de/deutschlandfunkdok/live/stream.mp3",
        "Deutschlandfunk Musik" to "https://live.deutschlandfunkmusik.de/deutschlandfunkmusik/live/stream.mp3",
        "Deutschlandfunk Kultur Musik" to "https://live.deutschlandfunkkultur.de/deutschlandfunkkultur/musik/stream.mp3",
        "Deutschlandfunk Kultur Wissen" to "https://live.deutschlandfunkkultur.de/deutschlandfunkkultur/wissen/stream.mp3",
        "Deutschlandfunk Kultur Literatur" to "https://live.deutschlandfunkkultur.de/deutschlandfunkkultur/literatur/stream.mp3",
        "Deutschlandfunk Kultur Film" to "https://live.deutschlandfunkkultur.de/deutschlandfunkkultur/film/stream.mp3",

        "J-Wave" to "https://jwf-r.akamaized.net/hls/live/2003451/jwf/master.m3u8",
        "NHK FM" to "https://nhkradio.akamaihd.net/i/nhkfm_1@393515/index_1_a-p.m3u8",
        "Swiss Pop" to "http://stream.srg-ssr.ch/m/rsp/aacp_96",
        "BBC World Service" to "http://stream.live.vc.bbcmedia.co.uk/bbc_world_service",
        "NPR News" to "https://npr-ice.streamguys1.com/live.mp3",

        "Radio Swiss Jazz" to "https://stream.srg-ssr.ch/m/rsj/mp3_128",
        "Classic FM (UK)" to "http://media-ice.musicradio.com/ClassicFMMP3",
        "Tokyo FM" to "https://tokyofm.mp3.radionomy.com/tokyofm",
        "FM Okinawa" to "https://fmiruka.co.jp:8443/fmiruka/stream.m3u8",
        "FM Fukuoka" to "http://www.simulradio.info/asx/FmFukuoka.asx",
        "Kiss FM Kobe" to "https://musicbird-hls.mmbi.co.jp/musicbird/stream.m3u8", // May require handling
        "ZIP-FM Nagoya" to "http://radiko.jp/v2/api/ts/stream/ZIP-FM" // Usually tokenized
    )
}
