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
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author Riccardo De Benedictis <riccardo.debenedictis@istc.cnr.it>
 */
public class Schema {

    public final BooleanProperty president;
    public final BooleanProperty structure;
    public final BooleanProperty brilliant;
    public final BooleanProperty evaluator;
    public final BooleanProperty concrete;
    public final BooleanProperty explorer;
    public final BooleanProperty worker;
    public final BooleanProperty objectivist;
    private final ProfileSchema entity;

    public Schema(ProfileSchema entity) {
        this.president = new SimpleBooleanProperty(entity.isPresident());
        this.structure = new SimpleBooleanProperty(entity.isStructure());
        this.brilliant = new SimpleBooleanProperty(entity.isBrilliant());
        this.evaluator = new SimpleBooleanProperty(entity.isEvaluator());
        this.concrete = new SimpleBooleanProperty(entity.isConcrete());
        this.explorer = new SimpleBooleanProperty(entity.isExplorer());
        this.worker = new SimpleBooleanProperty(entity.isWorker());
        this.objectivist = new SimpleBooleanProperty(entity.isObjectivist());

        this.president.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            entity.setPresident(newValue);
            Storage.getInstance().merge(entity);
        });
        this.structure.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            entity.setStructure(newValue);
            Storage.getInstance().merge(entity);
        });
        this.brilliant.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            entity.setBrilliant(newValue);
            Storage.getInstance().merge(entity);
        });
        this.evaluator.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            entity.setEvaluator(newValue);
            Storage.getInstance().merge(entity);
        });
        this.concrete.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            entity.setConcrete(newValue);
            Storage.getInstance().merge(entity);
        });
        this.explorer.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            entity.setExplorer(newValue);
            Storage.getInstance().merge(entity);
        });
        this.worker.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            entity.setWorker(newValue);
            Storage.getInstance().merge(entity);
        });
        this.objectivist.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            entity.setObjectivist(newValue);
            Storage.getInstance().merge(entity);
        });

        this.entity = entity;
    }

    public ProfileSchema getEntity() {
        return entity;
    }
}
