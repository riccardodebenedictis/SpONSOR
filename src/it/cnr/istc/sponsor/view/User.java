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

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Riccardo De Benedictis <riccardo.debenedictis@istc.cnr.it>
 */
public class User {

    public final StringProperty firstName = new SimpleStringProperty();
    public final StringProperty lastName = new SimpleStringProperty();
    public final IntegerProperty president = new SimpleIntegerProperty();
    public final IntegerProperty structure = new SimpleIntegerProperty();
    public final IntegerProperty brilliant = new SimpleIntegerProperty();
    public final IntegerProperty evaluator = new SimpleIntegerProperty();
    public final IntegerProperty concrete = new SimpleIntegerProperty();
    public final IntegerProperty explorer = new SimpleIntegerProperty();
    public final IntegerProperty worker = new SimpleIntegerProperty();
    public final IntegerProperty objectivist = new SimpleIntegerProperty();
    public final ObservableList<Schema> denials = FXCollections.observableArrayList();
    public final ObservableList<Schema> requests = FXCollections.observableArrayList();
}
