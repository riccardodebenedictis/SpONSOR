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

import it.cnr.istc.sponsor.view.Activity;
import it.cnr.istc.sponsor.view.User;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
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
import javafx.util.converter.NumberStringConverter;
import jfxtras.scene.control.agenda.Agenda;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

/**
 * FXML Controller class
 *
 * @author Riccardo De Benedictis <riccardo.debenedictis@istc.cnr.it>
 */
public class MainController implements Initializable {

    @FXML
    private Glyph solveGlyph;
    @FXML
    private Glyph addUserGlyph;
    @FXML
    private Glyph removeUsersGlyph;
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
    private SplitPane activities_split_pane;
    @FXML
    private SplitPane user_activities_split_pane;
    @FXML
    private SplitPane assigned_activities_split_pane;
    @FXML
    private TableView<User> userActivities;
    @FXML
    private TableColumn<User, String> userActivitiesFirstName;
    @FXML
    private TableColumn<User, String> userActivitiesLastName;
    @FXML
    private Agenda assignedActivities;
    private final DoubleProperty divider_position = new SimpleDoubleProperty(0.7);

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        solveGlyph.setIcon(FontAwesome.Glyph.GEARS);
        addUserGlyph.setIcon(FontAwesome.Glyph.PLUS_CIRCLE);
        removeUsersGlyph.setIcon(FontAwesome.Glyph.MINUS_CIRCLE);

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

        users.itemsProperty().setValue(Context.getInstance().users);
        removeUsers.disableProperty().bind(users.getSelectionModel().selectedItemProperty().isNull());

        users.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        for (Activity activity : Context.getInstance().activities) {
            agenda.appointments().add(Context.getInstance().getAppointment(activity));
        }

        agenda.setNewAppointmentCallback(range -> {
            Agenda.Appointment app = Context.getInstance().newAppointment(range);
            assignedActivities.appointments().add(app);
            return app;
        });
        agenda.setAppointmentChangedCallback((Agenda.Appointment app) -> {
            Activity activity = Context.getInstance().getActivity(app);
            activity.start.setValue(app.getStartLocalDateTime());
            activity.end.setValue(app.getEndLocalDateTime());
            return null;
        });

        agenda.selectedAppointments().addListener((ListChangeListener.Change<? extends Agenda.Appointment> c) -> {
            while (c.next()) {
                if (!c.getList().isEmpty()) {
                    Context.getInstance().selected_activity.setValue(Context.getInstance().getActivity(c.getList().get(0)));
                    FXMLLoader loader = new FXMLLoader(MainController.class.getResource("activity.fxml"));
                    try {
                        if (activities_split_pane.getItems().size() == 1) {
                            activities_split_pane.getItems().add(loader.load());
                        } else {
                            activities_split_pane.getItems().set(1, loader.load());
                        }
                        activities_split_pane.getDividers().get(0).positionProperty().bindBidirectional(divider_position);
                    } catch (IOException ex) {
                        Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    if (activities_split_pane.getItems().size() == 2) {
                        activities_split_pane.getItems().remove(1);
                    }
                }
            }
        });

        for (Activity activity : Context.getInstance().activities) {
            activity.name.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                Context.getInstance().getAppointment(activity).setDescription(newValue);
                Context.getInstance().getAppointment(activity).setSummary(newValue);
                agenda.refresh();
                assignedActivities.refresh();
            });
            activity.start.addListener((ObservableValue<? extends LocalDateTime> observable, LocalDateTime oldValue, LocalDateTime newValue) -> {
                Context.getInstance().getAppointment(activity).setStartLocalDateTime(newValue);
                agenda.refresh();
                assignedActivities.refresh();
            });
            activity.end.addListener((ObservableValue<? extends LocalDateTime> observable, LocalDateTime oldValue, LocalDateTime newValue) -> {
                Context.getInstance().getAppointment(activity).setEndLocalDateTime(newValue);
                agenda.refresh();
                assignedActivities.refresh();
            });
        }
        Context.getInstance().activities.addListener((ListChangeListener.Change<? extends Activity> c) -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (Activity activity : c.getAddedSubList()) {
                        activity.name.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                            Context.getInstance().getAppointment(activity).setDescription(newValue);
                            Context.getInstance().getAppointment(activity).setSummary(newValue);
                            agenda.refresh();
                            assignedActivities.refresh();
                        });
                        activity.start.addListener((ObservableValue<? extends LocalDateTime> observable, LocalDateTime oldValue, LocalDateTime newValue) -> {
                            Context.getInstance().getAppointment(activity).setStartLocalDateTime(newValue);
                            agenda.refresh();
                            assignedActivities.refresh();
                        });
                        activity.end.addListener((ObservableValue<? extends LocalDateTime> observable, LocalDateTime oldValue, LocalDateTime newValue) -> {
                            Context.getInstance().getAppointment(activity).setEndLocalDateTime(newValue);
                            agenda.refresh();
                            assignedActivities.refresh();
                        });
                    }
                }
            }
        });

        userActivities.itemsProperty().setValue(Context.getInstance().users);
        userActivitiesFirstName.setCellValueFactory(cellData -> cellData.getValue().firstName);
        userActivitiesLastName.setCellValueFactory(cellData -> cellData.getValue().lastName);

        userActivities.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends User> observable, User oldValue, User newValue) -> {
            if (newValue != null && Context.getInstance().solution.getValue() != null) {
                Context.getInstance().selected_user_activities.setValue(newValue);
                FXMLLoader loader = new FXMLLoader(MainController.class.getResource("user_activities.fxml"));
                try {
                    if (user_activities_split_pane.getItems().size() == 1) {
                        user_activities_split_pane.getItems().add(loader.load());
                    } else {
                        user_activities_split_pane.getItems().set(1, loader.load());
                    }
                    user_activities_split_pane.getDividers().get(0).positionProperty().bindBidirectional(divider_position);
                } catch (IOException ex) {
                    Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                if (user_activities_split_pane.getItems().size() == 2) {
                    user_activities_split_pane.getItems().remove(1);
                }
            }
        });

        for (Activity activity : Context.getInstance().activities) {
            assignedActivities.appointments().add(Context.getInstance().getAppointment(activity));
        }

        assignedActivities.selectedAppointments().addListener((ListChangeListener.Change<? extends Agenda.Appointment> c) -> {
            while (c.next()) {
                if (!c.getList().isEmpty() && Context.getInstance().solution.getValue() != null) {
                    Context.getInstance().selected_assigned_activity.setValue(Context.getInstance().getActivity(c.getList().get(0)));
                    FXMLLoader loader = new FXMLLoader(MainController.class.getResource("assigned_activity.fxml"));
                    try {
                        if (assigned_activities_split_pane.getItems().size() == 1) {
                            assigned_activities_split_pane.getItems().add(loader.load());
                        } else {
                            assigned_activities_split_pane.getItems().set(1, loader.load());
                        }
                        assigned_activities_split_pane.getDividers().get(0).positionProperty().bindBidirectional(divider_position);
                    } catch (IOException ex) {
                        Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    if (assigned_activities_split_pane.getItems().size() == 2) {
                        assigned_activities_split_pane.getItems().remove(1);
                    }
                }
            }
        });
    }

    public void addUser() {
        User user = Context.getInstance().newUser();
        users.getSelectionModel().select(user);
    }

    public void removeSelectedUsers() {
        Context.getInstance().removeUsers(users.getSelectionModel().getSelectedItems());
        users.getSelectionModel().selectFirst();
    }

    public void solve() {
        Context.getInstance().solve();
    }
}
