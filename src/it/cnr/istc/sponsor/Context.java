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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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
    public final ObjectProperty<Solution> solution = new SimpleObjectProperty<>();

    private Context() {
    }

    public static Context getInstance() {
        if (_instance == null) {
            _instance = new Context();
        }
        return _instance;
    }
}
