import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import sample.app.App
import top.kagg886.mkmb.MMKV
import top.kagg886.mkmb.initialize
import java.awt.Dimension
import java.io.File

fun main() = run {
    MMKV.initialize(
        File(System.getProperty("user.home"), ".cache").apply {
            if (!exists()) mkdirs()
        }.absolutePath
    )
    application {
        Window(
            title = "sample",
            state = rememberWindowState(width = 800.dp, height = 600.dp),
            onCloseRequest = ::exitApplication,
        ) {
            window.minimumSize = Dimension(350, 600)
            App()
        }
    }
}
