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
package it.cnr.istc.sponsor.view;

import it.cnr.istc.sponsor.db.ActivityEntity;
import it.cnr.istc.sponsor.db.ProfileSchema;
import it.cnr.istc.sponsor.db.Storage;
import it.cnr.istc.sponsor.db.UserEntity;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Map;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Riccardo De Benedictis <riccardo.debenedictis@istc.cnr.it>
 */
public class Context {

    private static Context _instance;
    public final ObservableList<User> users = FXCollections.observableArrayList();
    private final Map<User, UserEntity> user_entities = new IdentityHashMap<>();
    private final Map<UserEntity, User> entity_users = new IdentityHashMap<>();
    public final ObservableList<Activity> activities = FXCollections.observableArrayList();
    private final Map<Activity, ActivityEntity> activity_entities = new IdentityHashMap<>();
    private final Map<ActivityEntity, Activity> entity_activities = new IdentityHashMap<>();
    public final ObservableList<Schema> schemas = FXCollections.observableArrayList();
    private final Map<Schema, ProfileSchema> schema_entities = new IdentityHashMap<>();
    private final Map<ProfileSchema, Schema> entity_schemas = new IdentityHashMap<>();

    private Context() {
        Collection<UserEntity> c_users = Storage.getInstance().getAllUsers();
        Collection<ActivityEntity> c_activities = Storage.getInstance().getAllActivities();
        for (UserEntity user_entity : c_users) {
            User user = new User();
            user.firstName.setValue(user_entity.getFirstName());
            user.firstName.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                user_entity.setFirstName(newValue);
                Storage.getInstance().merge(user_entity);
            });
            user.lastName.setValue(user_entity.getLastName());
            user.lastName.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                user_entity.setLastName(newValue);
                Storage.getInstance().merge(user_entity);
            });
            user.president.setValue(user_entity.getPresident());
            user.president.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                user_entity.setPresident(newValue.intValue());
                Storage.getInstance().merge(user_entity);
            });
            user.structure.setValue(user_entity.getStructure());
            user.structure.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                user_entity.setStructure(newValue.intValue());
                Storage.getInstance().merge(user_entity);
            });
            user.brilliant.setValue(user_entity.getBrilliant());
            user.brilliant.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                user_entity.setBrilliant(newValue.intValue());
                Storage.getInstance().merge(user_entity);
            });
            user.evaluator.setValue(user_entity.getEvaluator());
            user.evaluator.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                user_entity.setEvaluator(newValue.intValue());
                Storage.getInstance().merge(user_entity);
            });
            user.concrete.setValue(user_entity.getConcrete());
            user.concrete.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                user_entity.setConcrete(newValue.intValue());
                Storage.getInstance().merge(user_entity);
            });
            user.explorer.setValue(user_entity.getExplorer());
            user.explorer.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                user_entity.setExplorer(newValue.intValue());
                Storage.getInstance().merge(user_entity);
            });
            user.worker.setValue(user_entity.getWorker());
            user.worker.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                user_entity.setWorker(newValue.intValue());
                Storage.getInstance().merge(user_entity);
            });
            user.objectivist.setValue(user_entity.getObjectivist());
            user.objectivist.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                user_entity.setObjectivist(newValue.intValue());
                Storage.getInstance().merge(user_entity);
            });
            users.add(user);
            user_entities.put(user, user_entity);
            entity_users.put(user_entity, user);
        }
        for (ActivityEntity activity_entity : c_activities) {
            Activity activity = new Activity();
            activity.name.setValue(activity_entity.getName());
            activity.name.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                activity_entity.setName(newValue);
                Storage.getInstance().merge(activity_entity);
            });
            activity.start.setValue(LocalDateTime.ofInstant(activity_entity.getStartTime().toInstant(), ZoneId.systemDefault()));
            activity.start.addListener((ObservableValue<? extends LocalDateTime> observable, LocalDateTime oldValue, LocalDateTime newValue) -> {
                activity_entity.setStartTime(Date.from(newValue.atZone(ZoneId.systemDefault()).toInstant()));
                Storage.getInstance().merge(activity_entity);
            });
            activity.end.setValue(LocalDateTime.ofInstant(activity_entity.getEndTime().toInstant(), ZoneId.systemDefault()));
            activity.end.addListener((ObservableValue<? extends LocalDateTime> observable, LocalDateTime oldValue, LocalDateTime newValue) -> {
                activity_entity.setEndTime(Date.from(newValue.atZone(ZoneId.systemDefault()).toInstant()));
                Storage.getInstance().merge(activity_entity);
            });
            activities.add(activity);
            activity_entities.put(activity, activity_entity);
            entity_activities.put(activity_entity, activity);
            for (ProfileSchema schema_entity : activity_entity.getSchemas()) {
                Schema schema = new Schema(activity);
                schema.president.setValue(schema_entity.isPresident());
                schema.president.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    schema_entity.setPresident(newValue);
                    Storage.getInstance().merge(schema_entity);
                });
                schema.structure.setValue(schema_entity.isStructure());
                schema.structure.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    schema_entity.setStructure(newValue);
                    Storage.getInstance().merge(schema_entity);
                });
                schema.brilliant.setValue(schema_entity.isBrilliant());
                schema.brilliant.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    schema_entity.setBrilliant(newValue);
                    Storage.getInstance().merge(schema_entity);
                });
                schema.evaluator.setValue(schema_entity.isEvaluator());
                schema.evaluator.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    schema_entity.setEvaluator(newValue);
                    Storage.getInstance().merge(schema_entity);
                });
                schema.concrete.setValue(schema_entity.isConcrete());
                schema.concrete.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    schema_entity.setConcrete(newValue);
                    Storage.getInstance().merge(schema_entity);
                });
                schema.explorer.setValue(schema_entity.isExplorer());
                schema.explorer.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    schema_entity.setExplorer(newValue);
                    Storage.getInstance().merge(schema_entity);
                });
                schema.worker.setValue(schema_entity.isWorker());
                schema.worker.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    schema_entity.setWorker(newValue);
                    Storage.getInstance().merge(schema_entity);
                });
                schema.objectivist.setValue(schema_entity.isObjectivist());
                schema.objectivist.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    schema_entity.setObjectivist(newValue);
                    Storage.getInstance().merge(schema_entity);
                });
                schemas.add(schema);
                activity.schemas.add(schema);
                schema_entities.put(schema, schema_entity);
                entity_schemas.put(schema_entity, schema);
            }
        }
        for (UserEntity user : c_users) {
            for (ProfileSchema denial : user.getDenials()) {
                entity_users.get(user).denials.add(entity_schemas.get(denial));
                entity_schemas.get(denial).denials.add(entity_users.get(user));
            }
            for (ProfileSchema denial : user.getRequests()) {
                entity_users.get(user).requests.add(entity_schemas.get(denial));
                entity_schemas.get(denial).requests.add(entity_users.get(user));
            }
        }
    }

    public static Context getInstance() {
        if (_instance == null) {
            _instance = new Context();
        }
        return _instance;
    }
}
