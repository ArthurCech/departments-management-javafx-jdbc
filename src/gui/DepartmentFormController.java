package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.exceptions.ValidationException;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {

	private DepartmentService departmentService;
	private Department department;
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private Label labelErrorName;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	@FXML
	private void onBtSaveAction(ActionEvent actionEvent) {
		if (department == null) {
			throw new IllegalStateException("Department was null");
		}
		if (departmentService == null) {
			throw new IllegalStateException("departmentService was null");
		}
		try {
			department = getFormData();
			departmentService.saveOrUpdate(department);
			notifyDataChangeListeners();
			Utils.currentStage(actionEvent).close();
		} catch (ValidationException exception) {
			setErrorMessages(exception.getErrors());
		} catch (DbException exception) {
			Alerts.showAlert("Error saving Department", null, exception.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDataChangeListeners() {
		dataChangeListeners.forEach(x -> x.onDataChanged());
	}

	private Department getFormData() {
		Department department = new Department();
		ValidationException validationException = new ValidationException("Validation error");

		department.setId(Utils.tryParseToInt(txtId.getText()));

		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			validationException.addError("name", "Field can't be empty");
		}

		department.setName(txtName.getText());

		if (validationException.getErrors().size() > 0) {
			throw validationException;
		}

		return department;
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();

		if (fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}
	}

	@FXML
	private void onBtCancelAction(ActionEvent actionEvent) {
		Utils.currentStage(actionEvent).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		initializeNodes();
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
	}

	public void updateFormData() {
		if (department == null) {
			throw new IllegalStateException("Department was null");
		}
		txtId.setText(String.valueOf(department.getId()));
		txtName.setText(department.getName());
	}

	public void subscribeDataChangeListener(DataChangeListener dataChangeListener) {
		dataChangeListeners.add(dataChangeListener);
	}

	public void setDepartmentService(DepartmentService departmentService) {
		this.departmentService = departmentService;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

}
