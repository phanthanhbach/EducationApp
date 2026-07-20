import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        #if DEBUG
        let baseUrl = "http://10.11.11.212:8085/api/v1/"
        #else
        let baseUrl = "http://cnxvn.ddns.net:9000/api/v1/"
        #endif
        KoinInitKt.doInitKoinIos(baseUrl: baseUrl)
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
