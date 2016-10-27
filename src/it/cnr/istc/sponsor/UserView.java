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

import it.cnr.istc.sponsor.db.UserEntity;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Riccardo De Benedictis <riccardo.debenedictis@istc.cnr.it>
 */
public class UserView {

    public final StringProperty firstName;
    public final StringProperty lastName;
    public final IntegerProperty president;
    public final IntegerProperty structure;
    public final IntegerProperty brilliant;
    public final IntegerProperty evaluator;
    public final IntegerProperty concrete;
    public final IntegerProperty explorer;
    public final IntegerProperty worker;
    public final IntegerProperty objectivist;
    private final UserEntity entity;

    public UserView(UserEntity entity) {
        this.firstName = new SimpleStringProperty(entity.getFirstName());
        this.lastName = new SimpleStringProperty(entity.getLastName());
        this.president = new SimpleIntegerProperty(entity.getPresident());
        this.structure = new SimpleIntegerProperty(entity.getStructure());
        this.brilliant = new SimpleIntegerProperty(entity.getBrilliant());
        this.evaluator = new SimpleIntegerProperty(entity.getEvaluator());
        this.concrete = new SimpleIntegerProperty(entity.getConcrete());
        this.explorer = new SimpleIntegerProperty(entity.getExplorer());
        this.worker = new SimpleIntegerProperty(entity.getWorker());
        this.objectivist = new SimpleIntegerProperty(entity.getObjectivist());
        this.entity = entity;
    }

    public UserEntity getEntity() {
        return entity;
    }
}
