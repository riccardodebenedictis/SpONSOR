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

import it.cnr.istc.sponsor.db.Storage;
import it.cnr.istc.sponsor.db.UserEntity;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.NumberStringConverter;

/**
 * FXML Controller class
 *
 * @author Riccardo De Benedictis <riccardo.debenedictis@istc.cnr.it>
 */
public class MainController implements Initializable {

    @FXML
    private Button addUser;
    @FXML
    private Button removeUsers;
    @FXML
    private TableView<UserView> users;
    @FXML
    private TableColumn<UserView, String> firstName;
    @FXML
    private TableColumn<UserView, String> lastName;
    @FXML
    private TableColumn<UserView, Number> president;
    @FXML
    private TableColumn<UserView, Number> structure;
    @FXML
    private TableColumn<UserView, Number> brilliant;
    @FXML
    private TableColumn<UserView, Number> evaluator;
    @FXML
    private TableColumn<UserView, Number> concrete;
    @FXML
    private TableColumn<UserView, Number> explorer;
    @FXML
    private TableColumn<UserView, Number> worker;
    @FXML
    private TableColumn<UserView, Number> objectivist;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        firstName.setCellValueFactory(cellData -> cellData.getValue().firstName);
        lastName.setCellValueFactory(cellData -> cellData.getValue().lastName);
        president.setCellValueFactory(cellData -> cellData.getValue().president);
        structure.setCellValueFactory(cellData -> cellData.getValue().structure);
        brilliant.setCellValueFactory(cellData -> cellData.getValue().brilliant);
        evaluator.setCellValueFactory(cellData -> cellData.getValue().evaluator);
        concrete.setCellValueFactory(cellData -> cellData.getValue().concrete);
        explorer.setCellValueFactory(cellData -> cellData.getValue().explorer);
        worker.setCellValueFactory(cellData -> cellData.getValue().worker);
        objectivist.setCellValueFactory(cellData -> cellData.getValue().objectivist);

        firstName.setCellFactory(TextFieldTableCell.<UserView>forTableColumn());
        lastName.setCellFactory(TextFieldTableCell.<UserView>forTableColumn());
        president.setCellFactory(TextFieldTableCell.<UserView, Number>forTableColumn(new NumberStringConverter()));
        structure.setCellFactory(TextFieldTableCell.<UserView, Number>forTableColumn(new NumberStringConverter()));
        brilliant.setCellFactory(TextFieldTableCell.<UserView, Number>forTableColumn(new NumberStringConverter()));
        evaluator.setCellFactory(TextFieldTableCell.<UserView, Number>forTableColumn(new NumberStringConverter()));
        concrete.setCellFactory(TextFieldTableCell.<UserView, Number>forTableColumn(new NumberStringConverter()));
        explorer.setCellFactory(TextFieldTableCell.<UserView, Number>forTableColumn(new NumberStringConverter()));
        worker.setCellFactory(TextFieldTableCell.<UserView, Number>forTableColumn(new NumberStringConverter()));
        objectivist.setCellFactory(TextFieldTableCell.<UserView, Number>forTableColumn(new NumberStringConverter()));

        firstName.setOnEditCommit(cellData -> {
            cellData.getRowValue().firstName.setValue(cellData.getNewValue());
            Storage.getInstance().merge(cellData.getRowValue().getEntity());
        });
        lastName.setOnEditCommit(cellData -> {
            cellData.getRowValue().lastName.setValue(cellData.getNewValue());
            Storage.getInstance().merge(cellData.getRowValue().getEntity());
        });
        president.setOnEditCommit(cellData -> {
            cellData.getRowValue().president.setValue(cellData.getNewValue());
            Storage.getInstance().merge(cellData.getRowValue().getEntity());
        });
        structure.setOnEditCommit(cellData -> {
            cellData.getRowValue().structure.setValue(cellData.getNewValue());
            Storage.getInstance().merge(cellData.getRowValue().getEntity());
        });
        brilliant.setOnEditCommit(cellData -> {
            cellData.getRowValue().brilliant.setValue(cellData.getNewValue());
            Storage.getInstance().merge(cellData.getRowValue().getEntity());
        });
        evaluator.setOnEditCommit(cellData -> {
            cellData.getRowValue().evaluator.setValue(cellData.getNewValue());
            Storage.getInstance().merge(cellData.getRowValue().getEntity());
        });
        concrete.setOnEditCommit(cellData -> {
            cellData.getRowValue().concrete.setValue(cellData.getNewValue());
            Storage.getInstance().merge(cellData.getRowValue().getEntity());
        });
        explorer.setOnEditCommit(cellData -> {
            cellData.getRowValue().explorer.setValue(cellData.getNewValue());
            Storage.getInstance().merge(cellData.getRowValue().getEntity());
        });
        worker.setOnEditCommit(cellData -> {
            cellData.getRowValue().worker.setValue(cellData.getNewValue());
            Storage.getInstance().merge(cellData.getRowValue().getEntity());
        });
        objectivist.setOnEditCommit(cellData -> {
            cellData.getRowValue().objectivist.setValue(cellData.getNewValue());
            Storage.getInstance().merge(cellData.getRowValue().getEntity());
        });

        users.getItems().addAll(Storage.getInstance().getAllUsers().stream().map(user -> new UserView(user)).collect(Collectors.toList()));
        removeUsers.disableProperty().bind(users.getSelectionModel().selectedItemProperty().isNull());

        users.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );
    }

    public void addUser() {
        UserView user = new UserView(new UserEntity());
        Storage.getInstance().persist(user.getEntity());
        users.getItems().add(user);
        users.getSelectionModel().select(user);
    }

    public void removeSelectedUsers() {
        users.getSelectionModel().getSelectedItems().forEach((user) -> {
            Storage.getInstance().remove(user.getEntity());
        });
        users.getItems().removeAll(users.getSelectionModel().getSelectedItems());
        users.getSelectionModel().selectFirst();
    }
}
