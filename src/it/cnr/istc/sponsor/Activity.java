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
import it.cnr.istc.sponsor.db.ProfileSchema;
import it.cnr.istc.sponsor.db.Storage;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 *
 * @author Riccardo De Benedictis <riccardo.debenedictis@istc.cnr.it>
 */
public class Activity {

    public final StringProperty name;
    public final ObjectProperty<LocalDateTime> start;
    public final ObjectProperty<LocalDateTime> end;
    public final ObjectProperty<ObservableList<Schema>> schemas;
    private final ActivityEntity entity;

    public Activity(ActivityEntity entity) {
        this.name = new SimpleStringProperty(entity.getName());
        this.start = new SimpleObjectProperty<>(LocalDateTime.ofInstant(entity.getStartTime().toInstant(), ZoneId.systemDefault()));
        this.end = new SimpleObjectProperty<>(LocalDateTime.ofInstant(entity.getEndTime().toInstant(), ZoneId.systemDefault()));

        this.schemas = new SimpleObjectProperty<>(FXCollections.observableArrayList());
        for (ProfileSchema schema : entity.getSchemas()) {
            schemas.getValue().add(new Schema(this, schema));
        }

        this.name.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            Context.getInstance().getAppointment(this).setDescription(newValue);
            Context.getInstance().getAppointment(this).setSummary(newValue);
            Context.getInstance().agenda.getValue().refresh();
            entity.setName(newValue);
            Storage.getInstance().merge(entity);
        });
        this.start.addListener((ObservableValue<? extends LocalDateTime> observable, LocalDateTime oldValue, LocalDateTime newValue) -> {
            Context.getInstance().getAppointment(this).setStartLocalDateTime(newValue);
            Context.getInstance().agenda.getValue().refresh();
            entity.setStartTime(Date.from(newValue.atZone(ZoneId.systemDefault()).toInstant()));
            Storage.getInstance().merge(entity);
        });
        this.end.addListener((ObservableValue<? extends LocalDateTime> observable, LocalDateTime oldValue, LocalDateTime newValue) -> {
            Context.getInstance().getAppointment(this).setEndLocalDateTime(newValue);
            Context.getInstance().agenda.getValue().refresh();
            entity.setEndTime(Date.from(newValue.atZone(ZoneId.systemDefault()).toInstant()));
            Storage.getInstance().merge(entity);
        });
        this.schemas.getValue().addListener((ListChangeListener.Change<? extends Schema> c) -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach((schema) -> {
                        entity.addProfileSchema(schema.getEntity());
                    });
                }
                if (c.wasRemoved()) {
                    c.getRemoved().forEach((schema) -> {
                        entity.removeProfileSchema(schema.getEntity());
                    });
                }
                if (c.wasAdded() || c.wasRemoved()) {
                    Storage.getInstance().merge(entity);
                }
            }
        });

        this.entity = entity;
    }

    public ActivityEntity getEntity() {
        return entity;
    }
}
