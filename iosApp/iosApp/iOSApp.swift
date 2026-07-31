import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        let baseUrl = Bundle.main.object(forInfoDictionaryKey: "BASE_URL") as? String ?? "http://10.11.11.212:8085/api/v1/"
        KoinInitKt.doInitKoinIos(baseUrl: baseUrl)
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
