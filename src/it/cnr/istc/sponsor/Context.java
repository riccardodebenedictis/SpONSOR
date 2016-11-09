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
import com.microsoft.z3.Model;
import com.microsoft.z3.Optimize;
import com.microsoft.z3.Status;
import it.cnr.istc.sponsor.db.ActivityEntity;
import it.cnr.istc.sponsor.db.ProfileSchema;
import it.cnr.istc.sponsor.db.Storage;
import it.cnr.istc.sponsor.db.UserEntity;
import it.cnr.istc.sponsor.view.Activity;
import it.cnr.istc.sponsor.view.Schema;
import it.cnr.istc.sponsor.view.User;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import jfxtras.scene.control.agenda.Agenda;

/**
 *
 * @author Riccardo De Benedictis <riccardo.debenedictis@istc.cnr.it>
 */
public class Context {

    private static final Logger LOG = Logger.getLogger(Context.class.getName());
    private static Context _instance;
    public final ObjectProperty<Stage> primary_stage = new SimpleObjectProperty<>();
    public final ObjectProperty<Activity> selected_activity = new SimpleObjectProperty<>();
    public final ObjectProperty<Activity> selected_assigned_activity = new SimpleObjectProperty<>();
    public final ObjectProperty<User> selected_user_activities = new SimpleObjectProperty<>();
    public final ObjectProperty<Solution> solution = new SimpleObjectProperty<>();
    public final ObservableList<User> users = FXCollections.observableArrayList();
    private final Map<User, UserEntity> user_entity = new IdentityHashMap<>();
    private final Map<UserEntity, User> entity_user = new IdentityHashMap<>();
    public final ObservableList<Activity> activities = FXCollections.observableArrayList();
    private final Map<Activity, ActivityEntity> activity_entity = new IdentityHashMap<>();
    private final Map<ActivityEntity, Activity> entity_activity = new IdentityHashMap<>();
    private final Map<Activity, Agenda.Appointment> activity_appointment = new IdentityHashMap<>();
    private final Map<Agenda.Appointment, Activity> appointment_activity = new IdentityHashMap<>();
    public final ObservableList<Schema> schemas = FXCollections.observableArrayList();
    private final Map<Schema, ProfileSchema> schema_entities = new IdentityHashMap<>();
    private final Map<ProfileSchema, Schema> entity_schemas = new IdentityHashMap<>();

    private Context() {
        Collection<UserEntity> c_users = Storage.getInstance().getAllUsers();
        Collection<ActivityEntity> c_activities = Storage.getInstance().getAllActivities();
        for (UserEntity entity : c_users) {
            User user = newUser(entity);
            users.add(user);
            user_entity.put(user, entity);
            entity_user.put(entity, user);
        }
        for (ActivityEntity c_activity : c_activities) {
            Activity activity = newActivity(c_activity);
            activities.add(activity);
            activity_entity.put(activity, c_activity);
            entity_activity.put(c_activity, activity);

            Agenda.Appointment app = new Agenda.AppointmentImplLocal()
                    .withDescription(activity.name.getValue())
                    .withSummary(activity.name.getValue())
                    .withStartLocalDateTime(activity.start.getValue())
                    .withEndLocalDateTime(activity.end.getValue())
                    .withAppointmentGroup(new Agenda.AppointmentGroupImpl().withStyleClass("group1"));
            activity_appointment.put(activity, app);
            appointment_activity.put(app, activity);

            for (ProfileSchema schema_entity : c_activity.getSchemas()) {
                Schema schema = newSchema(activity, schema_entity);
                activity.schemas.add(schema);
                schemas.add(schema);
                schema_entities.put(schema, schema_entity);
                entity_schemas.put(schema_entity, schema);
            }
        }

        for (UserEntity user : c_users) {
            for (ActivityEntity activity : user.getAssignedActivities()) {
                entity_user.get(user).assigned_activities.add(entity_activity.get(activity));
                entity_activity.get(activity).assigned_users.add(entity_user.get(user));
            }
            for (ActivityEntity activity : user.getNegatedActivities()) {
                entity_user.get(user).negated_activities.add(entity_activity.get(activity));
                entity_activity.get(activity).negated_users.add(entity_user.get(user));
            }
        }

        for (User user : users) {
            user.assigned_activities.addListener((ListChangeListener.Change<? extends Activity> c) -> {
                while (c.next()) {
                    for (Activity activity : c.getAddedSubList()) {
                        Storage.getInstance().assign(user_entity.get(user), activity_entity.get(activity));
                    }
                    for (Activity activity : c.getRemoved()) {
                        Storage.getInstance().unassign(user_entity.get(user), activity_entity.get(activity));
                    }
                }
            });
            user.negated_activities.addListener((ListChangeListener.Change<? extends Activity> c) -> {
                while (c.next()) {
                    for (Activity activity : c.getAddedSubList()) {
                        Storage.getInstance().negate(user_entity.get(user), activity_entity.get(activity));
                    }
                    for (Activity activity : c.getRemoved()) {
                        Storage.getInstance().allow(user_entity.get(user), activity_entity.get(activity));
                    }
                }
            });
        }
    }

    public Agenda.Appointment getAppointment(Activity activity) {
        return activity_appointment.get(activity);
    }

    public Agenda.Appointment newAppointment(Agenda.LocalDateTimeRange range) {
        ActivityEntity entity = new ActivityEntity();
        entity.setStartTime(Date.from(range.getStartLocalDateTime().atZone(ZoneId.systemDefault()).toInstant()));
        entity.setEndTime(Date.from(range.getEndLocalDateTime().atZone(ZoneId.systemDefault()).toInstant()));
        Storage.getInstance().persist(entity);

        Activity activity = newActivity(entity);

        activities.add(activity);

        activity_entity.put(activity, entity);
        entity_activity.put(entity, activity);

        Agenda.Appointment app = new Agenda.AppointmentImplLocal()
                .withStartLocalDateTime(range.getStartLocalDateTime())
                .withEndLocalDateTime(range.getEndLocalDateTime())
                .withAppointmentGroup(new Agenda.AppointmentGroupImpl().withStyleClass("group1"));

        activity_appointment.put(activity, app);
        appointment_activity.put(app, activity);

        return app;
    }

    public User newUser() {
        UserEntity entity = new UserEntity();
        Storage.getInstance().persist(entity);
        User user = newUser(entity);
        users.add(user);
        return user;
    }

    public void removeUsers(Collection<User> us) {
        us.forEach(user -> Storage.getInstance().remove(user_entity.get(user)));
        users.removeAll(us);
    }

    public Schema newSchema() {
        Activity activity = selected_activity.getValue();
        ProfileSchema entity = new ProfileSchema();
        entity.setActivity(activity_entity.get(activity));
        Storage.getInstance().persist(entity);
        activity_entity.get(activity).addProfileSchema(entity);
        Storage.getInstance().merge(activity_entity.get(activity));
        Schema schema = newSchema(activity, entity);
        activity.schemas.add(schema);
        schemas.add(schema);
        return schema;
    }

    public void removeSchemas(Collection<Schema> ss) {
        ss.forEach(schema -> Storage.getInstance().remove(schema_entities.get(schema)));
        schemas.removeAll(ss);
    }

    public Activity getActivity(Agenda.Appointment app) {
        return appointment_activity.get(app);
    }

    public static Context getInstance() {
        if (_instance == null) {
            _instance = new Context();
        }
        return _instance;
    }

    private static User newUser(UserEntity entity) {
        User user = new User();
        user.firstName.setValue(entity.getFirstName());
        user.firstName.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            entity.setFirstName(newValue);
            Storage.getInstance().merge(entity);
        });
        user.lastName.setValue(entity.getLastName());
        user.lastName.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            entity.setLastName(newValue);
            Storage.getInstance().merge(entity);
        });
        user.president.setValue(entity.getPresident());
        user.president.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            entity.setPresident(newValue.intValue());
            Storage.getInstance().merge(entity);
        });
        user.structure.setValue(entity.getStructure());
        user.structure.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            entity.setStructure(newValue.intValue());
            Storage.getInstance().merge(entity);
        });
        user.brilliant.setValue(entity.getBrilliant());
        user.brilliant.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            entity.setBrilliant(newValue.intValue());
            Storage.getInstance().merge(entity);
        });
        user.evaluator.setValue(entity.getEvaluator());
        user.evaluator.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            entity.setEvaluator(newValue.intValue());
            Storage.getInstance().merge(entity);
        });
        user.concrete.setValue(entity.getConcrete());
        user.concrete.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            entity.setConcrete(newValue.intValue());
            Storage.getInstance().merge(entity);
        });
        user.explorer.setValue(entity.getExplorer());
        user.explorer.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            entity.setExplorer(newValue.intValue());
            Storage.getInstance().merge(entity);
        });
        user.worker.setValue(entity.getWorker());
        user.worker.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            entity.setWorker(newValue.intValue());
            Storage.getInstance().merge(entity);
        });
        user.objectivist.setValue(entity.getObjectivist());
        user.objectivist.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            entity.setObjectivist(newValue.intValue());
            Storage.getInstance().merge(entity);
        });
        return user;
    }

    private static Activity newActivity(ActivityEntity entity) {
        Activity activity = new Activity();
        activity.name.setValue(entity.getName());
        activity.name.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            entity.setName(newValue);
            Storage.getInstance().merge(entity);
        });
        activity.start.setValue(LocalDateTime.ofInstant(entity.getStartTime().toInstant(), ZoneId.systemDefault()));
        activity.start.addListener((ObservableValue<? extends LocalDateTime> observable, LocalDateTime oldValue, LocalDateTime newValue) -> {
            entity.setStartTime(Date.from(newValue.atZone(ZoneId.systemDefault()).toInstant()));
            Storage.getInstance().merge(entity);
        });
        activity.end.setValue(LocalDateTime.ofInstant(entity.getEndTime().toInstant(), ZoneId.systemDefault()));
        activity.end.addListener((ObservableValue<? extends LocalDateTime> observable, LocalDateTime oldValue, LocalDateTime newValue) -> {
            entity.setEndTime(Date.from(newValue.atZone(ZoneId.systemDefault()).toInstant()));
            Storage.getInstance().merge(entity);
        });
        return activity;
    }

    private static Schema newSchema(Activity activity, ProfileSchema entity) {
        Schema schema = new Schema(activity);
        schema.president.setValue(entity.isPresident());
        schema.president.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            entity.setPresident(newValue);
            Storage.getInstance().merge(entity);
        });
        schema.structure.setValue(entity.isStructure());
        schema.structure.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            entity.setStructure(newValue);
            Storage.getInstance().merge(entity);
        });
        schema.brilliant.setValue(entity.isBrilliant());
        schema.brilliant.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            entity.setBrilliant(newValue);
            Storage.getInstance().merge(entity);
        });
        schema.evaluator.setValue(entity.isEvaluator());
        schema.evaluator.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            entity.setEvaluator(newValue);
            Storage.getInstance().merge(entity);
        });
        schema.concrete.setValue(entity.isConcrete());
        schema.concrete.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            entity.setConcrete(newValue);
            Storage.getInstance().merge(entity);
        });
        schema.explorer.setValue(entity.isExplorer());
        schema.explorer.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            entity.setExplorer(newValue);
            Storage.getInstance().merge(entity);
        });
        schema.worker.setValue(entity.isWorker());
        schema.worker.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            entity.setWorker(newValue);
            Storage.getInstance().merge(entity);
        });
        schema.objectivist.setValue(entity.isObjectivist());
        schema.objectivist.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            entity.setObjectivist(newValue);
            Storage.getInstance().merge(entity);
        });
        return schema;
    }

    public boolean solve() {
        LOG.info("Building the problem..");
        if (Context.getInstance().users.size() < Context.getInstance().schemas.size()) {
            LOG.warning("The number of users is lower then the number of schemas..");
            return false;
        }
        long start_time = System.currentTimeMillis();
        Map<User, Map<Schema, IntExpr>> user_assignments = new IdentityHashMap<>();
        Map<Schema, Map<User, IntExpr>> schema_assignments = new IdentityHashMap<>();
        com.microsoft.z3.Context ctx;
        Optimize o;
        // the objective function..
        ArithExpr[] objective;
        HashMap<String, String> cfg = new HashMap<>();
        cfg.put("model", "true");
        ctx = new com.microsoft.z3.Context(cfg);
        o = ctx.mkOptimize();
        for (User user : Context.getInstance().users) {
            user_assignments.put(user, new IdentityHashMap<>());
        }
        for (Schema schema : Context.getInstance().schemas) {
            schema_assignments.put(schema, new IdentityHashMap<>());
        }

        objective = new ArithExpr[Context.getInstance().users.size() * Context.getInstance().schemas.size()];

        int user_id = 0, schema_id = 0, i = 0;
        for (User user : Context.getInstance().users) {
            schema_id = 0;
            for (Schema schema : Context.getInstance().schemas) {
                IntExpr var = ctx.mkIntConst("x_" + user_id++ + "_" + schema_id++);
                o.Add(ctx.mkGe(var, ctx.mkInt("0")), ctx.mkLe(var, ctx.mkInt("1")));
                user_assignments.get(user).put(schema, var);
                schema_assignments.get(schema).put(user, var);
                objective[i++] = ctx.mkMul(var, ctx.mkReal(Double.toString(match_rate(user, schema))));
            }
        }
        Optimize.Handle handle = o.MkMaximize(ctx.mkAdd(objective));

        // any users should do something..
        for (User user : Context.getInstance().users) {
            ArithExpr[] cst = new ArithExpr[Context.getInstance().schemas.size()];
            schema_id = 0;
            for (Schema schema : Context.getInstance().schemas) {
                cst[schema_id++] = user_assignments.get(user).get(schema);
            }
            o.Add(ctx.mkGe(ctx.mkAdd(cst), ctx.mkInt("1")));
        }

        // every schema should be done by someone..
        for (Schema schema : Context.getInstance().schemas) {
            ArithExpr[] cst = new ArithExpr[Context.getInstance().users.size()];
            schema_id = 0;
            for (User user : Context.getInstance().users) {
                cst[schema_id++] = user_assignments.get(user).get(schema);
            }
            o.Add(ctx.mkEq(ctx.mkAdd(cst), ctx.mkInt("1")));
        }

        // overlapping schemas cannot be done by the same person..
        Map<LocalDateTime, Collection<Schema>> starting_schemas = new HashMap<>();
        Map<LocalDateTime, Collection<Schema>> ending_schemas = new HashMap<>();
        Set<LocalDateTime> pulses = new HashSet<>();
        for (Schema schema : Context.getInstance().schemas) {
            if (!starting_schemas.containsKey(schema.activity.start.getValue())) {
                starting_schemas.put(schema.activity.start.getValue(), new ArrayList<>());
            }
            if (!ending_schemas.containsKey(schema.activity.end.getValue())) {
                ending_schemas.put(schema.activity.end.getValue(), new ArrayList<>());
            }
            starting_schemas.get(schema.activity.start.getValue()).add(schema);
            ending_schemas.get(schema.activity.end.getValue()).add(schema);
            pulses.add(schema.activity.start.getValue());
            pulses.add(schema.activity.end.getValue());
        }

        // Sort current pulses
        LocalDateTime[] c_pulses_array = pulses.toArray(new LocalDateTime[pulses.size()]);
        Arrays.sort(c_pulses_array);

        List<Schema> overlapping_schemas = new ArrayList<>();
        for (LocalDateTime time : c_pulses_array) {
            if (starting_schemas.containsKey(time)) {
                overlapping_schemas.addAll(starting_schemas.get(time));
            }
            if (ending_schemas.containsKey(time)) {
                overlapping_schemas.removeAll(ending_schemas.get(time));
            }
            if (overlapping_schemas.size() > 1) {
                for (User user : Context.getInstance().users) {
                    ArithExpr[] cst = new ArithExpr[overlapping_schemas.size()];
                    schema_id = 0;
                    for (Schema schema : overlapping_schemas) {
                        cst[schema_id++] = user_assignments.get(user).get(schema);
                    }
                    o.Add(ctx.mkLe(ctx.mkAdd(cst), ctx.mkInt("1")));
                }
            }
        }

        // We set the user constraints..
        for (User user : Context.getInstance().users) {
            for (Activity activity : user.assigned_activities) {
                ArithExpr[] cst = new ArithExpr[activity.schemas.size()];
                schema_id = 0;
                for (Schema schema : activity.schemas) {
                    cst[schema_id++] = user_assignments.get(user).get(schema);
                }
                o.Add(ctx.mkEq(ctx.mkAdd(cst), ctx.mkInt("1")));
            }
            for (Activity activity : user.negated_activities) {
                ArithExpr[] cst = new ArithExpr[activity.schemas.size()];
                schema_id = 0;
                for (Schema schema : activity.schemas) {
                    cst[schema_id++] = user_assignments.get(user).get(schema);
                }
                o.Add(ctx.mkEq(ctx.mkAdd(cst), ctx.mkInt("0")));
            }
        }

        long problem_time = System.currentTimeMillis();
        LOG.log(Level.INFO, "Building the problem required {0} seconds..", (problem_time - start_time) / 1000);
        LOG.info("Solving the problem..");
        if (o.Check() == Status.SATISFIABLE) {
            long solving_time = System.currentTimeMillis();
            LOG.log(Level.INFO, "Solving the problem required {0} seconds..", (solving_time - problem_time) / 1000);
            LOG.log(Level.INFO, "Total time: {0} seconds..", (solving_time - start_time) / 1000);
            LOG.log(Level.INFO, "Solution quality: {0}", handle.toString());
            Map<User, Collection<Schema>> user_schemas = new IdentityHashMap<>();
            Map<Schema, User> assignments = new IdentityHashMap<>();
            Model model = o.getModel();
            for (Map.Entry<User, Map<Schema, IntExpr>> user_entry : user_assignments.entrySet()) {
                user_schemas.put(user_entry.getKey(), new ArrayList<>());
                for (Map.Entry<Schema, IntExpr> schema_entry : user_entry.getValue().entrySet()) {
                    if (model.evaluate(schema_entry.getValue(), true).toString().equals("1")) {
                        user_schemas.get(user_entry.getKey()).add(schema_entry.getKey());
                        assignments.put(schema_entry.getKey(), user_entry.getKey());
                    }
                }
            }
            solution.setValue(new Solution(user_schemas, assignments));
            return true;
        } else {
            LOG.info("The problem is inconsistent!!");
            solution.setValue(null);
            return false;
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
