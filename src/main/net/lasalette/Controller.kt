package net.lasalette

import javafx.application.Platform
import javafx.geometry.Insets
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.RadioButton
import javafx.scene.control.TextField
import javafx.scene.control.ToggleButton
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.HBox
import javafx.scene.paint.Paint
import javafx.stage.Stage

class Controller(stage: Stage, scene: Parent) {

  private val startButton = scene.lookup("#start_button") as ToggleButton
  private val clockDisplay = scene.lookup("#clock_display") as Label

  private val textMin = scene.lookup("#txt_min") as TextField
  private val textSec = scene.lookup("#txt_sec") as TextField
  private val textMilli = scene.lookup("#txt_milli") as TextField

  private val labelOtherTeam = scene.lookup("#label_other_team") as Label
  private val editOtherTeam = scene.lookup("#txt_other_team") as TextField
  private val buttonOtherTeam = scene.lookup("#btn_set_other_team") as Button

  private val ndlsButtons = scene.lookup("#btn_ndls_scores") as HBox
  private val otherScores = scene.lookup("#btn_other_scores") as HBox

  private val ndlsScore = scene.lookup("#ndls_score") as Label
  private val otherTeamButtonLabel = scene.lookup("#other_team_score_label") as Label
  private val otherScore = scene.lookup("#other_score") as Label

  private val txtNdlsScore = scene.lookup("#txt_ndls_score") as TextField
  private val txtOtherScore = scene.lookup("#txt_other_team_score") as TextField

  private val setTimeButton = scene.lookup("#btn_set_time") as Button

  private val inputs = listOf(textMin, textSec, textMilli, txtNdlsScore, txtOtherScore)

  private val quarterHbox = scene.lookup("#quarter_hbox") as HBox
  private val quarterLabel = scene.lookup("#quarter_label") as Label
  private val quarterValues = listOf("1st", "2nd", "3rd", "4th")

  private val clock = Clock(10, 0, 0)

  private val updater: TimerListener = { _, _, _ ->
    runUI { clockDisplay.text = clock.toString() }
  }

  val INITIAL_OTHER_TEAM_NAME = "Bad Guys"

  init {
    runUI {
      clockDisplay.text = clock.toString()
      setTimeButton.isFocusTraversable = false
      buttonOtherTeam.isFocusTraversable = false
    }

    inputs.forEach { field ->
      field.textProperty().addListener { _, _, newValue ->
        if (!newValue.matches("\\d*".toRegex())) {
          runUI { field.text = newValue.replace("[^\\d]".toRegex(), ""); }
        }
      }
    }
    listOf(Pair(txtNdlsScore, ndlsScore), Pair(txtOtherScore, otherScore)).forEach { (edit, label) ->
      edit.focusedProperty().addListener { observable ->
        if (edit.text.isEmpty()) runUI { edit.text = "${ label.text.toIntOrNull() ?: 0 }"}
      }
    }

    inputs.forEach { field ->
      field.setOnMouseClicked {
        runUI { field.selectAll() }
      }
      field.setOnAction {
        runUI { field.selectAll() }
      }
    }

    ndlsScore.textProperty().bindBidirectional(txtNdlsScore.textProperty())
    otherScore.textProperty().bindBidirectional(txtOtherScore.textProperty())

    setTimeButton.setOnMouseClicked {
      clock.setTime(
          textMin.text.toIntOrNull() ?: 0,
          textSec.text.toIntOrNull() ?: 0,
          textMilli.text.toIntOrNull() ?: 0
      )
    }

    runUI {
      otherTeamButtonLabel.text = INITIAL_OTHER_TEAM_NAME
      labelOtherTeam.text = INITIAL_OTHER_TEAM_NAME
      editOtherTeam.text = INITIAL_OTHER_TEAM_NAME
      otherTeamButtonLabel.textProperty().bind(labelOtherTeam.textProperty())
    }
    buttonOtherTeam.setOnMouseClicked {
      runUI { labelOtherTeam.text = editOtherTeam.text }
    }

    val defaultStartBackground = startButton.background
    val backgroundOnRunning = BackgroundFill(
        Paint.valueOf("GREEN"),
        startButton.background.fills.first().radii,
        startButton.background.fills.first().insets
    )

    clock.subscribe(updater)
    clock.listen { _, current ->
      when {
        current.status == Clock.Status.COMPLETE -> startButton.fire()
        current.status == Clock.Status.STOPPED -> runUI {
          startButton.background = defaultStartBackground
          startButton.text = "Start"
        }
        current.status == Clock.Status.STARTED -> runUI {
          startButton.text = "Stop"
          startButton.background = Background(backgroundOnRunning)
        }
      }
    }

    ndlsButtons.children.filterIsInstance<Button>().forEach { btn ->
      runUI { btn.isFocusTraversable = false }
      btn.setOnMouseClicked {
        val score = ndlsScore.text.toIntOrNull() ?: 0
        runUI {
          ndlsScore.text = "" + when (btn.text.first()) {
            '+' -> score + (btn.text.subSequence(1, btn.text.length).toString().toIntOrNull() ?: 0)
            '-' -> score - (btn.text.subSequence(1, btn.text.length).toString().toIntOrNull() ?: 0)
            else -> throw IllegalStateException()
          }
        }
      }
    }

    otherScores.children.filterIsInstance<Button>().forEach { btn ->
      runUI { btn.isFocusTraversable = false }
      btn.setOnMouseClicked {
        val score = otherScore.text.toIntOrNull() ?: 0
        runUI {
          otherScore.text = "" + when (btn.text.first()) {
            '+' -> score + (btn.text.subSequence(1, btn.text.length).toString().toIntOrNull() ?: 0)
            '-' -> score - (btn.text.subSequence(1, btn.text.length).toString().toIntOrNull() ?: 0)
            else -> throw IllegalStateException()
          }
        }
      }
    }

    startButton.selectedProperty().addListener { _, _, selected ->
      if (selected) clock.start() else clock.pause()
    }

    runUI { quarterHbox.spacing = 5.0 }
    val toggleQuarter = ToggleGroup()

    quarterHbox.children.run {
      runUI {
        for (i in 1..4) {
          add(RadioButton("$i").apply {
            isFocusTraversable = false
            toggleGroup = toggleQuarter
            if (i == 1) toggleQuarter.selectToggle(this)
          })
        }
      }
    }

    toggleQuarter.selectedToggleProperty().addListener { _, _, newValue ->
      newValue.toggleGroup.toggles.forEachIndexed { index, toggle ->
        if (newValue === toggle) runUI { quarterLabel.text = quarterValues[index] + " Quarter" }
      }
    }

    runUI {
      stage.height = quarterLabel.height + quarterLabel.localToScene(quarterLabel.boundsInLocal).maxY + 15
    }

    stage.setOnCloseRequest { clock.kill() }
  }
}

private fun runUI(action: () -> Unit) = Platform.runLater(action)
