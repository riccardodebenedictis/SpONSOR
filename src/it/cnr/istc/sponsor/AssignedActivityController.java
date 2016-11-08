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

import it.cnr.istc.sponsor.view.Activity;
import it.cnr.istc.sponsor.view.Schema;
import it.cnr.istc.sponsor.view.User;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import jfxtras.scene.control.LocalDateTimeTextField;

/**
 * FXML Controller class
 *
 * @author Riccardo De Benedictis <riccardo.debenedictis@istc.cnr.it>
 */
public class AssignedActivityController implements Initializable {

    @FXML
    private TextField name;
    @FXML
    private LocalDateTimeTextField start;
    @FXML
    private LocalDateTimeTextField end;
    @FXML
    private TableView<User> users;
    @FXML
    private TableColumn<User, String> firstName;
    @FXML
    private TableColumn<User, String> lastName;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Activity activity = Context.getInstance().selected_assigned_activity.getValue();

        name.textProperty().bindBidirectional(activity.name);
        start.localDateTimeProperty().bindBidirectional(activity.start);
        end.localDateTimeProperty().bindBidirectional(activity.end);

        firstName.setCellValueFactory(cellData -> cellData.getValue().firstName);
        lastName.setCellValueFactory(cellData -> cellData.getValue().lastName);

        Solution solution = Context.getInstance().solution.getValue();
        if (solution != null) {
            for (Schema schema : activity.schemas) {
                users.itemsProperty().getValue().add(solution.getAssignedUser(schema));
            }
        }
    }
}
