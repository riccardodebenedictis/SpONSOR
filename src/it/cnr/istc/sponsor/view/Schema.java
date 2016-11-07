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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Riccardo De Benedictis <riccardo.debenedictis@istc.cnr.it>
 */
public class Schema {

    public final Activity activity;
    public final BooleanProperty president = new SimpleBooleanProperty();
    public final BooleanProperty structure = new SimpleBooleanProperty();
    public final BooleanProperty brilliant = new SimpleBooleanProperty();
    public final BooleanProperty evaluator = new SimpleBooleanProperty();
    public final BooleanProperty concrete = new SimpleBooleanProperty();
    public final BooleanProperty explorer = new SimpleBooleanProperty();
    public final BooleanProperty worker = new SimpleBooleanProperty();
    public final BooleanProperty objectivist = new SimpleBooleanProperty();
    public final ObservableList<User> denials = FXCollections.observableArrayList();
    public final ObservableList<User> requests = FXCollections.observableArrayList();

    public Schema(Activity activity) {
        this.activity = activity;
    }
}
