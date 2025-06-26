import androidx.compose.ui.window.ComposeUIViewController
import platform.Foundation.NSTemporaryDirectory
import platform.UIKit.UIViewController
import sample.app.App
import top.kagg886.mkmb.MMKV
import top.kagg886.mkmb.initialize

fun MainViewController(): UIViewController = run {
    MMKV.initialize(NSTemporaryDirectory())
    ComposeUIViewController { App() }
}

