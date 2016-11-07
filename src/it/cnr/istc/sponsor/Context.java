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
import it.cnr.istc.sponsor.db.ProfileSchema;
import it.cnr.istc.sponsor.db.Storage;
import it.cnr.istc.sponsor.db.UserEntity;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import jfxtras.scene.control.agenda.Agenda;

/**
 *
 * @author Riccardo De Benedictis <riccardo.debenedictis@istc.cnr.it>
 */
public class Context {

    private static Context _instance;
    public final ObjectProperty<Stage> stage = new SimpleObjectProperty<>();
    public final ObjectProperty<Agenda> agenda = new SimpleObjectProperty<>();
    public final ObjectProperty<Activity> selected_activity = new SimpleObjectProperty<>();
    public final ObservableList<User> users;
    public final ObservableList<Activity> activities;
    public final ObservableList<Schema> schemas;
    private final Map<Agenda.Appointment, Activity> app_act = new IdentityHashMap<>();
    private final Map<Activity, Agenda.Appointment> act_app = new IdentityHashMap<>();

    private Context() {
        this.users = FXCollections.observableArrayList(Storage.getInstance().getAllUsers().stream().map(user -> new User(user)).collect(Collectors.toList()));
        this.activities = FXCollections.observableArrayList(Storage.getInstance().getAllActivities().stream().map(activity -> new Activity(activity)).collect(Collectors.toList()));
        this.schemas = FXCollections.observableArrayList();
        for (Activity activity : activities) {
            Agenda.AppointmentImplLocal app = new Agenda.AppointmentImplLocal()
                    .withDescription(activity.name.getValue())
                    .withSummary(activity.name.getValue())
                    .withStartLocalDateTime(activity.start.getValue())
                    .withEndLocalDateTime(activity.end.getValue())
                    .withAppointmentGroup(new Agenda.AppointmentGroupImpl().withStyleClass("group1"));
            app_act.put(app, activity);
            act_app.put(activity, app);
            schemas.addAll(activity.schemas.getValue());
        }
    }

    public Activity getActivity(Agenda.Appointment appointment) {
        return app_act.get(appointment);
    }

    public Agenda.Appointment getAppointment(Activity activity) {
        return act_app.get(activity);
    }

    public Agenda.Appointment newAppointment(Agenda.LocalDateTimeRange range) {
        Agenda.Appointment app = new Agenda.AppointmentImplLocal()
                .withStartLocalDateTime(range.getStartLocalDateTime())
                .withEndLocalDateTime(range.getEndLocalDateTime())
                .withAppointmentGroup(new Agenda.AppointmentGroupImpl().withStyleClass("group1"));
        ActivityEntity entity = new ActivityEntity();
        entity.setStartTime(Date.from(range.getStartLocalDateTime().atZone(ZoneId.systemDefault()).toInstant()));
        entity.setEndTime(Date.from(range.getEndLocalDateTime().atZone(ZoneId.systemDefault()).toInstant()));
        Storage.getInstance().persist(entity);
        Activity activity = new Activity(entity);

        app_act.put(app, activity);
        act_app.put(activity, app);

        return app;
    }

    public User newUser() {
        User user = new User(new UserEntity());
        Storage.getInstance().persist(user.getEntity());
        users.add(user);
        return user;
    }

    public void removeUsers(Collection<User> us) {
        us.forEach((user) -> {
            Storage.getInstance().remove(user.getEntity());
        });
        users.removeAll(us);
    }

    public Schema newSchema() {
        Activity activity = selected_activity.getValue();
        Schema schema = new Schema(activity, new ProfileSchema());
        schema.getEntity().setActivity(activity.getEntity());
        Storage.getInstance().persist(schema.getEntity());
        activity.schemas.getValue().add(schema);
        schemas.add(schema);
        return schema;
    }

    public void removeSchemas(Collection<Schema> ss) {
        ss.forEach((schema) -> {
            Storage.getInstance().remove(schema.getEntity());
        });
        schemas.removeAll(ss);
    }

    public boolean solve() {
        List<Schema> schemas = new ArrayList<>();
        for (Activity activity : activities) {
            schemas.addAll(activity.schemas.getValue());
        }

        Map<User, Integer> user_id = new IdentityHashMap<>();
        for (int i = 0; i < users.size(); i++) {
            user_id.put(users.get(i), i);
        }
        Map<Schema, Integer> schema_id = new IdentityHashMap<>();
        for (int i = 0; i < schemas.size(); i++) {
            schema_id.put(schemas.get(i), i);
        }

        HashMap<String, String> cfg = new HashMap<>();
        cfg.put("model", "true");
        com.microsoft.z3.Context ctx = new com.microsoft.z3.Context(cfg);
        Optimize o = ctx.mkOptimize();

        // the 0-1 variables..
        IntExpr[][] vars = new IntExpr[users.size()][schemas.size()];

        // the objective function..
        ArithExpr[] objective = new ArithExpr[users.size() * schemas.size()];

        int expr_id = 0;
        for (int i = 0; i < vars.length; i++) {
            for (int j = 0; j < vars[i].length; j++) {
                vars[i][j] = ctx.mkIntConst("x_" + i + "_" + j);
                o.Add(ctx.mkGe(vars[i][j], ctx.mkInt("0")), ctx.mkLe(vars[i][j], ctx.mkInt("1")));
                objective[expr_id++] = ctx.mkMul(vars[i][j], ctx.mkReal(Double.toString(match_rate(users.get(i), schemas.get(j)))));
            }
        }

        o.MkMaximize(ctx.mkAdd(objective));

        // any users should do something..
        for (IntExpr[] var : vars) {
            o.Add(ctx.mkGe(ctx.mkAdd(var), ctx.mkInt("1")));
        }

        // every schema should be done by someone..
        for (int j = 0; j < schemas.size(); j++) {
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
        for (Activity activity : activities) {
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
        for (LocalDateTime c_pulses_array1 : c_pulses_array) {
            if (starting_schemas.containsKey(c_pulses_array1)) {
                overlapping_schemas.addAll(starting_schemas.get(c_pulses_array1));
            }
            if (ending_schemas.containsKey(c_pulses_array1)) {
                overlapping_schemas.removeAll(ending_schemas.get(c_pulses_array1));
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
                return false;
            case SATISFIABLE:
                System.out.println("Solution found!");
                System.out.println(o.getModel());
                return true;
            default:
                throw new AssertionError(o.Check().name());
        }
    }

    public static Context getInstance() {
        if (_instance == null) {
            _instance = new Context();
        }
        return _instance;
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
