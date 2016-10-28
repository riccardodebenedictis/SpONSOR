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

import it.cnr.istc.sponsor.db.ActivityEntity;
import it.cnr.istc.sponsor.db.Storage;
import it.cnr.istc.sponsor.db.UserEntity;
import java.io.IOException;
import java.net.URL;
import java.time.ZoneId;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.util.converter.NumberStringConverter;
import jfxtras.scene.control.agenda.Agenda;

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
    private TableView<User> users;
    @FXML
    private TableColumn<User, String> firstName;
    @FXML
    private TableColumn<User, String> lastName;
    @FXML
    private TableColumn<User, Number> president;
    @FXML
    private TableColumn<User, Number> structure;
    @FXML
    private TableColumn<User, Number> brilliant;
    @FXML
    private TableColumn<User, Number> evaluator;
    @FXML
    private TableColumn<User, Number> concrete;
    @FXML
    private TableColumn<User, Number> explorer;
    @FXML
    private TableColumn<User, Number> worker;
    @FXML
    private TableColumn<User, Number> objectivist;
    @FXML
    private Agenda agenda;
    @FXML
    private SplitPane split_pane;
    private final DoubleProperty divider_position = new SimpleDoubleProperty(0.7);
    private final Map<Agenda.Appointment, Activity> activities_map = new IdentityHashMap<>();

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

        firstName.setCellFactory(TextFieldTableCell.<User>forTableColumn());
        lastName.setCellFactory(TextFieldTableCell.<User>forTableColumn());
        president.setCellFactory(TextFieldTableCell.<User, Number>forTableColumn(new NumberStringConverter()));
        structure.setCellFactory(TextFieldTableCell.<User, Number>forTableColumn(new NumberStringConverter()));
        brilliant.setCellFactory(TextFieldTableCell.<User, Number>forTableColumn(new NumberStringConverter()));
        evaluator.setCellFactory(TextFieldTableCell.<User, Number>forTableColumn(new NumberStringConverter()));
        concrete.setCellFactory(TextFieldTableCell.<User, Number>forTableColumn(new NumberStringConverter()));
        explorer.setCellFactory(TextFieldTableCell.<User, Number>forTableColumn(new NumberStringConverter()));
        worker.setCellFactory(TextFieldTableCell.<User, Number>forTableColumn(new NumberStringConverter()));
        objectivist.setCellFactory(TextFieldTableCell.<User, Number>forTableColumn(new NumberStringConverter()));

        firstName.setOnEditCommit(cellData -> cellData.getRowValue().firstName.setValue(cellData.getNewValue()));
        lastName.setOnEditCommit(cellData -> cellData.getRowValue().lastName.setValue(cellData.getNewValue()));
        president.setOnEditCommit(cellData -> cellData.getRowValue().president.setValue(cellData.getNewValue()));
        structure.setOnEditCommit(cellData -> cellData.getRowValue().structure.setValue(cellData.getNewValue()));
        brilliant.setOnEditCommit(cellData -> cellData.getRowValue().brilliant.setValue(cellData.getNewValue()));
        evaluator.setOnEditCommit(cellData -> cellData.getRowValue().evaluator.setValue(cellData.getNewValue()));
        concrete.setOnEditCommit(cellData -> cellData.getRowValue().concrete.setValue(cellData.getNewValue()));
        explorer.setOnEditCommit(cellData -> cellData.getRowValue().explorer.setValue(cellData.getNewValue()));
        worker.setOnEditCommit(cellData -> cellData.getRowValue().worker.setValue(cellData.getNewValue()));
        objectivist.setOnEditCommit(cellData -> cellData.getRowValue().objectivist.setValue(cellData.getNewValue()));

        users.getItems().addAll(Storage.getInstance().getAllUsers().stream().map(user -> new User(user)).collect(Collectors.toList()));
        removeUsers.disableProperty().bind(users.getSelectionModel().selectedItemProperty().isNull());

        users.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        agenda.setNewAppointmentCallback((Agenda.LocalDateTimeRange param) -> {
            Agenda.Appointment appointment = new Agenda.AppointmentImplLocal()
                    .withStartLocalDateTime(param.getStartLocalDateTime())
                    .withEndLocalDateTime(param.getEndLocalDateTime())
                    .withAppointmentGroup(new Agenda.AppointmentGroupImpl().withStyleClass("group1"));
            ActivityEntity entity = new ActivityEntity();
            entity.setStartTime(Date.from(param.getStartLocalDateTime().atZone(ZoneId.systemDefault()).toInstant()));
            entity.setEndTime(Date.from(param.getEndLocalDateTime().atZone(ZoneId.systemDefault()).toInstant()));
            activities_map.put(appointment, new Activity(appointment, entity));
            return appointment;
        });
        agenda.setAppointmentChangedCallback((Agenda.Appointment param) -> {
            Activity view = activities_map.get(param);
            view.start.setValue(param.getStartLocalDateTime());
            view.end.setValue(param.getEndLocalDateTime());
            return null;
        });
        agenda.selectedAppointments().addListener((ListChangeListener.Change<? extends Agenda.Appointment> c) -> {
            if (!c.getList().isEmpty()) {
                Context.getInstance().setSelectedActivity(activities_map.get(c.getList().get(0)));
                FXMLLoader loader = new FXMLLoader(MainController.class.getResource("activity.fxml"));
                try {
                    if (split_pane.getItems().size() == 1) {
                        split_pane.getItems().add(loader.load());
                    } else {
                        split_pane.getItems().set(1, loader.load());
                    }
                    split_pane.getDividers().get(0).positionProperty().bindBidirectional(divider_position);
                } catch (IOException ex) {
                    Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                if (split_pane.getItems().size() == 2) {
                    split_pane.getItems().remove(1);
                }
            }
        });
    }

    public void addUser() {
        User user = new User(new UserEntity());
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
