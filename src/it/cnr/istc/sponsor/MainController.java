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

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Optimize;
import it.cnr.istc.sponsor.db.ActivityEntity;
import it.cnr.istc.sponsor.db.Storage;
import it.cnr.istc.sponsor.db.UserEntity;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
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

        users.getItems().addAll(Storage.getInstance().getAllUsers().stream().map(user -> new User(user)).collect(Collectors.toList()));
        removeUsers.disableProperty().bind(users.getSelectionModel().selectedItemProperty().isNull());

        users.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Storage.getInstance().getAllActivities().forEach((entity) -> {
            Activity activity = new Activity(new Agenda.AppointmentImplLocal()
                    .withDescription(entity.getName())
                    .withSummary(entity.getName())
                    .withStartLocalDateTime(LocalDateTime.ofInstant(entity.getStartTime().toInstant(), ZoneId.systemDefault()))
                    .withEndLocalDateTime(LocalDateTime.ofInstant(entity.getEndTime().toInstant(), ZoneId.systemDefault()))
                    .withAppointmentGroup(new Agenda.AppointmentGroupImpl().withStyleClass("group1")), entity);
            activities_map.put(activity.getAppointment(), activity);
            agenda.appointments().add(activity.getAppointment());
        });

        agenda.setNewAppointmentCallback((Agenda.LocalDateTimeRange param) -> {
            Agenda.Appointment appointment = new Agenda.AppointmentImplLocal()
                    .withStartLocalDateTime(param.getStartLocalDateTime())
                    .withEndLocalDateTime(param.getEndLocalDateTime())
                    .withAppointmentGroup(new Agenda.AppointmentGroupImpl().withStyleClass("group1"));
            ActivityEntity entity = new ActivityEntity();
            entity.setStartTime(Date.from(param.getStartLocalDateTime().atZone(ZoneId.systemDefault()).toInstant()));
            entity.setEndTime(Date.from(param.getEndLocalDateTime().atZone(ZoneId.systemDefault()).toInstant()));
            Storage.getInstance().persist(entity);
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

        Context.getInstance().setAgenda(agenda);
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

    public void solve() {
        List<User> all_users = new ArrayList<>(users.getItems());
        Collections.sort(all_users, (User u1, User u2) -> {
            String u1_val = u1.lastName.getValue() + " " + u1.firstName.getValue();
            String u2_val = u2.lastName.getValue() + " " + u2.firstName.getValue();
            return u1_val.compareTo(u2_val);
        });
        List<Schema> all_schemas = new ArrayList<>();
        for (Activity activity : activities_map.values()) {
            all_schemas.addAll(activity.schemas.getValue());
        }
        Collections.sort(all_schemas, (Schema s1, Schema s2) -> s1.getActivity().start.getValue().compareTo(s2.getActivity().start.getValue()));
        Map<Schema, Integer> schema_id = new IdentityHashMap<>();
        for (int i = 0; i < all_schemas.size(); i++) {
            schema_id.put(all_schemas.get(i), i);
        }

        HashMap<String, String> cfg = new HashMap<>();
        cfg.put("model", "true");
        com.microsoft.z3.Context ctx = new com.microsoft.z3.Context(cfg);
        Optimize o = ctx.mkOptimize();

        // the 0-1 variables..
        IntExpr[][] vars = new IntExpr[all_users.size()][all_schemas.size()];

        // the objective function..
        ArithExpr[] objective = new ArithExpr[all_users.size() * all_schemas.size()];

        int expr_id = 0;
        for (int i = 0; i < vars.length; i++) {
            for (int j = 0; j < vars[i].length; j++) {
                vars[i][j] = ctx.mkIntConst("x_" + i + "_" + j);
                o.Add(ctx.mkGe(vars[i][j], ctx.mkInt("0")), ctx.mkLe(vars[i][j], ctx.mkInt("1")));
                objective[expr_id++] = ctx.mkMul(vars[i][j], ctx.mkReal(Double.toString(match_rate(all_users.get(i), all_schemas.get(j)))));
            }
        }

        o.MkMaximize(ctx.mkAdd(objective));

        // any users should do something..
        for (int i = 0; i < vars.length; i++) {
            o.Add(ctx.mkGe(ctx.mkAdd(vars[i]), ctx.mkInt("1")));
        }

        // every schema should be done by someone..
        for (int j = 0; j < all_schemas.size(); j++) {
            ArithExpr[] schema = new ArithExpr[vars.length];
            for (int i = 0; i < vars.length; i++) {
                schema[i] = vars[i][j];
            }
            o.Add(ctx.mkEq(ctx.mkAdd(schema), ctx.mkInt("1")));
        }

        // overlapping schemas cannot be done by the same person..
        Map<LocalDateTime, Collection<Schema>> starting_schemas = new HashMap<>();
        Map<LocalDateTime, Collection<Schema>> ending_schemas = new HashMap<>();
        Set<LocalDateTime> pulses = new HashSet<>();
        for (Activity activity : activities_map.values()) {
            if (!starting_schemas.containsKey(activity.start.getValue())) {
                starting_schemas.put(activity.start.getValue(), new ArrayList<>());
            }
            if (!ending_schemas.containsKey(activity.end.getValue())) {
                ending_schemas.put(activity.end.getValue(), new ArrayList<>());
            }
            starting_schemas.get(activity.start.getValue()).addAll(activity.schemas.getValue());
            ending_schemas.get(activity.end.getValue()).addAll(activity.schemas.getValue());
            pulses.add(activity.start.getValue());
            pulses.add(activity.end.getValue());
        }

        // Sort current pulses
        LocalDateTime[] c_pulses_array = pulses.toArray(new LocalDateTime[pulses.size()]);
        Arrays.sort(c_pulses_array);

        List<Schema> overlapping_schemas = new ArrayList<>();
        for (int p = 0; p < c_pulses_array.length; p++) {
            if (starting_schemas.containsKey(c_pulses_array[p])) {
                overlapping_schemas.addAll(starting_schemas.get(c_pulses_array[p]));
            }
            if (ending_schemas.containsKey(c_pulses_array[p])) {
                overlapping_schemas.removeAll(ending_schemas.get(c_pulses_array[p]));
            }
            if (overlapping_schemas.size() > 1) {
                for (int i = 0; i < vars.length; i++) {
                    ArithExpr[] overlapping = new ArithExpr[overlapping_schemas.size()];
                    for (int j = 0; j < overlapping.length; j++) {
                        overlapping[j] = vars[i][schema_id.get(overlapping_schemas.get(j))];
                    }
                    o.Add(ctx.mkLe(ctx.mkAdd(overlapping), ctx.mkInt("1")));
                }
            }
        }

        System.out.println(o);

        switch (o.Check()) {
            case UNSATISFIABLE:
                System.out.println("Unsatisfable..");
                break;
            case SATISFIABLE:
                System.out.println("Solution found!");
                System.out.println(o.getModel());
                break;
            default:
                throw new AssertionError(o.Check().name());
        }
    }

    private static double match_rate(User user, Schema schema) {
        double mr = 0;
        if (schema.president.getValue()) {
            mr += user.president.getValue();
        }
        if (schema.structure.getValue()) {
            mr += user.structure.getValue();
        }
        if (schema.brilliant.getValue()) {
            mr += user.brilliant.getValue();
        }
        if (schema.evaluator.getValue()) {
            mr += user.evaluator.getValue();
        }
        if (schema.concrete.getValue()) {
            mr += user.concrete.getValue();
        }
        if (schema.explorer.getValue()) {
            mr += user.explorer.getValue();
        }
        if (schema.worker.getValue()) {
            mr += user.worker.getValue();
        }
        if (schema.objectivist.getValue()) {
            mr += user.objectivist.getValue();
        }
        return mr;
    }
}
