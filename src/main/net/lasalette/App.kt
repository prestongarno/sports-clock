package net.lasalette

import javafx.application.Application
import javafx.application.Application.launch
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

class App : Application() {

  @Throws(Exception::class)
  override fun start(primaryStage: Stage) {
    val root = FXMLLoader.load<Parent>(javaClass.getResource("sample.fxml"))

    primaryStage.title = "La Salette Clock"

    primaryStage.scene = Scene(
        root,
        root.prefWidth(root.layoutBounds.width),
        root.prefHeight(root.layoutBounds.height)
    )
    primaryStage.show()
    Controller(primaryStage, root)
  }

  companion object {
  }
}

fun main(args: Array<String>) {
  launch(App::class.java)
}
