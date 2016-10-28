/*
 * Copyright (C) 2016 Riccardo De Benedictis <riccardo.debenedictis@istc.cnr.it>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.cnr.istc.sponsor;

import it.cnr.istc.sponsor.db.ProfileSchema;
import it.cnr.istc.sponsor.db.Storage;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import jfxtras.scene.control.LocalDateTimeTextField;

/**
 * FXML Controller class
 *
 * @author Riccardo De Benedictis <riccardo.debenedictis@istc.cnr.it>
 */
public class ActivityController implements Initializable {

    @FXML
    private TextField name;
    @FXML
    private LocalDateTimeTextField start;
    @FXML
    private LocalDateTimeTextField end;
    @FXML
    private Button addSchema;
    @FXML
    private Button removeSchemas;
    @FXML
    private TableView<Schema> schemas;
    @FXML
    private TableColumn<Schema, Boolean> president;
    @FXML
    private TableColumn<Schema, Boolean> structure;
    @FXML
    private TableColumn<Schema, Boolean> brilliant;
    @FXML
    private TableColumn<Schema, Boolean> evaluator;
    @FXML
    private TableColumn<Schema, Boolean> concrete;
    @FXML
    private TableColumn<Schema, Boolean> explorer;
    @FXML
    private TableColumn<Schema, Boolean> worker;
    @FXML
    private TableColumn<Schema, Boolean> objectivist;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Activity activity = Context.getInstance().getSelectedActivity();

        name.textProperty().bindBidirectional(activity.name);
        start.localDateTimeProperty().bindBidirectional(activity.start);
        end.localDateTimeProperty().bindBidirectional(activity.end);

        name.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            activity.getAppointment().setDescription(newValue);
            activity.getAppointment().setSummary(newValue);
            Context.getInstance().getAgenda().refresh();
        });
        start.localDateTimeProperty().addListener((ObservableValue<? extends LocalDateTime> observable, LocalDateTime oldValue, LocalDateTime newValue) -> {
            activity.getAppointment().setStartLocalDateTime(newValue);
            Context.getInstance().getAgenda().refresh();
        });
        end.localDateTimeProperty().addListener((ObservableValue<? extends LocalDateTime> observable, LocalDateTime oldValue, LocalDateTime newValue) -> {
            activity.getAppointment().setEndLocalDateTime(newValue);
            Context.getInstance().getAgenda().refresh();
        });

        president.setCellFactory(CheckBoxTableCell.forTableColumn(president));
        structure.setCellFactory(CheckBoxTableCell.forTableColumn(structure));
        brilliant.setCellFactory(CheckBoxTableCell.forTableColumn(brilliant));
        evaluator.setCellFactory(CheckBoxTableCell.forTableColumn(evaluator));
        concrete.setCellFactory(CheckBoxTableCell.forTableColumn(concrete));
        explorer.setCellFactory(CheckBoxTableCell.forTableColumn(explorer));
        worker.setCellFactory(CheckBoxTableCell.forTableColumn(worker));
        objectivist.setCellFactory(CheckBoxTableCell.forTableColumn(objectivist));

        president.setCellValueFactory(cellData -> cellData.getValue().president);
        structure.setCellValueFactory(cellData -> cellData.getValue().structure);
        brilliant.setCellValueFactory(cellData -> cellData.getValue().brilliant);
        evaluator.setCellValueFactory(cellData -> cellData.getValue().evaluator);
        concrete.setCellValueFactory(cellData -> cellData.getValue().concrete);
        explorer.setCellValueFactory(cellData -> cellData.getValue().explorer);
        worker.setCellValueFactory(cellData -> cellData.getValue().worker);
        objectivist.setCellValueFactory(cellData -> cellData.getValue().objectivist);

        president.setOnEditCommit(cellData -> cellData.getRowValue().president.setValue(cellData.getNewValue()));
        structure.setOnEditCommit(cellData -> cellData.getRowValue().structure.setValue(cellData.getNewValue()));
        brilliant.setOnEditCommit(cellData -> cellData.getRowValue().brilliant.setValue(cellData.getNewValue()));
        evaluator.setOnEditCommit(cellData -> cellData.getRowValue().evaluator.setValue(cellData.getNewValue()));
        concrete.setOnEditCommit(cellData -> cellData.getRowValue().concrete.setValue(cellData.getNewValue()));
        explorer.setOnEditCommit(cellData -> cellData.getRowValue().explorer.setValue(cellData.getNewValue()));
        worker.setOnEditCommit(cellData -> cellData.getRowValue().worker.setValue(cellData.getNewValue()));
        objectivist.setOnEditCommit(cellData -> cellData.getRowValue().objectivist.setValue(cellData.getNewValue()));

        schemas.itemsProperty().bindBidirectional(activity.schemas);
        removeSchemas.disableProperty().bind(schemas.getSelectionModel().selectedItemProperty().isNull());

        schemas.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void addSchema() {
        Activity activity = Context.getInstance().getSelectedActivity();
        Schema schema = new Schema(activity, new ProfileSchema());
        schema.getEntity().setActivity(activity.getEntity());
        Storage.getInstance().persist(schema.getEntity());
        activity.schemas.getValue().add(schema);
    }

    public void removeSchemas() {
        schemas.getSelectionModel().getSelectedItems().forEach((user) -> {
            Storage.getInstance().remove(user.getEntity());
        });
        schemas.getItems().removeAll(schemas.getSelectionModel().getSelectedItems());
        schemas.getSelectionModel().selectFirst();
    }
}
