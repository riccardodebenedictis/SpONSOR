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

import it.cnr.istc.sponsor.db.Storage;
import it.cnr.istc.sponsor.db.UserEntity;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * FXML Controller class
 *
 * @author Riccardo De Benedictis <riccardo.debenedictis@istc.cnr.it>
 */
public class MainController implements Initializable {

    @FXML
    private Button addUser;
    @FXML
    private Button removeUsers;
    @FXML
    private TableView<UserView> users;
    @FXML
    private TableColumn<UserView, String> firstName;
    @FXML
    private TableColumn<UserView, String> lastName;
    @FXML
    private TableColumn<UserView, Number> president;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        firstName.setCellValueFactory(cellData -> cellData.getValue().firstName);
        lastName.setCellValueFactory(cellData -> cellData.getValue().lastName);
        president.setCellValueFactory(cellData -> cellData.getValue().president);
        users.getItems().addAll(Storage.getInstance().getAllUsers().stream().map(user -> new UserView(user)).collect(Collectors.toList()));
        removeUsers.disableProperty().bind(users.getSelectionModel().selectedItemProperty().isNull());
    }

    public void addUser() {
        UserView uv = new UserView(new UserEntity());
        users.getItems().add(uv);
        users.getSelectionModel().select(uv);
    }

    public void removeSelectedUsers() {
        users.getSelectionModel().getSelectedItems().forEach((user) -> {
            Storage.getInstance().remove(user.getEntity());
        });
        users.getItems().removeAll(users.getSelectionModel().getSelectedItems());
        users.getSelectionModel().selectFirst();
    }
}
