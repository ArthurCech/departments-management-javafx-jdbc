package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class Utils {

	// retornar o stage de onde o evento foi emitido
	public static Stage currentStage(ActionEvent actionEvent) {
		return (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
	}

	public static Integer tryParseToInt(String value) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException exception) {
			return null;
		}
	}

}
