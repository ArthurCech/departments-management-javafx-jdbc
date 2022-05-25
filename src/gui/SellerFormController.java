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
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.SellerService;

public class SellerFormController implements Initializable {

	private SellerService sellerService;
	private Seller seller;
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
		if (seller == null) {
			throw new IllegalStateException("Seller was null");
		}
		if (sellerService == null) {
			throw new IllegalStateException("SellerService was null");
		}
		try {
			seller = getFormData();
			sellerService.saveOrUpdate(seller);
			this.notifyDataChangeListeners();
			Utils.currentStage(actionEvent).close();
		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		} catch (DbException e) {
			Alerts.showAlert("Error saving Seller", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDataChangeListeners() {
		dataChangeListeners.forEach(x -> x.onDataChanged());
	}

	private Seller getFormData() {
		Seller seller = new Seller();
		ValidationException validationException = new ValidationException("Validation error");

		seller.setId(Utils.tryParseToInt(txtId.getText()));

		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			validationException.addError("name", "Field can't be empty");
		}

		seller.setName(txtName.getText());

		if (validationException.getErrors().size() > 0) {
			throw validationException;
		}

		return seller;
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
		this.initializeNodes();
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
	}

	public void updateFormData() {
		if (seller == null) {
			throw new IllegalStateException("Seller was null");
		}
		this.txtId.setText(String.valueOf(seller.getId()));
		this.txtName.setText(seller.getName());
	}

	public void setSellerService(SellerService sellerService) {
		this.sellerService = sellerService;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
	}

	public void subscribeDataChangeListener(DataChangeListener dataChangeListener) {
		this.dataChangeListeners.add(dataChangeListener);
	}

}
