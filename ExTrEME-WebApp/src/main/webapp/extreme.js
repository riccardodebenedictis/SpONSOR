/* 
 * Copyright (C) 2018 Riccardo De Benedictis
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

$(document).ready(function () {
    var uname = localStorage.getItem("username");
    var upass = localStorage.getItem("password");
    console.log("stored username is: " + uname);
    console.log("stored password is: " + upass);
    localStorage.setItem("username", null);
    localStorage.setItem("password", null);
    if (uname !== 'null' & upass !== 'null') {
        console.log("login: " + uname + " " + upass);
        login({email: uname, password: upass});
    } else {
        var btn_login = document.getElementById("btn_login");
        btn_login.onclick = function () {
            console.log("login button clicked");
            uname = document.getElementById("email_input").value;
            upass = document.getElementById("password_input").value;
            console.log("login: " + uname + " " + upass);
            login({email: uname, password: upass});
        };
    }
});

function login(credentials) {
    console.log("login: " + credentials);
    $.ajax({
        type: 'POST',
        url: "http://localhost:8080/ExTrEME-WebApp/ExTrEME/login",
        contentType: 'application/json',
        data: JSON.stringify(credentials),
        error: function (xhr, ajaxOptions, thrownError) {
            alert(thrownError);
        }
    }).then(function (user) {
        var nav_bar = document.getElementById("nav_bar");
        sessionStorage.setItem("user", user);
        var login_form = document.getElementById("login_form");
        nav_bar.removeChild(login_form);
        localStorage.setItem("username", credentials.email);
        localStorage.setItem("password", credentials.password);

        var a = document.createElement("a");
        a.href = "#";
        a.className = "dropdown-toggle mx-sm-3 mb-2";
        a.setAttribute("data-toggle", "dropdown");
        var sp0 = document.createElement("span");
        sp0.className = "fas fa-user";
        a.appendChild(sp0);
        var a_txt = document.createTextNode(user.first_name);
        a.appendChild(a_txt);
        nav_bar.append(a);

        var btn = document.createElement("button");
        btn.type = "submit";
        btn.className = "btn btn-primary mb-2";
        var btn_txt = document.createTextNode("Sign out");
        btn.appendChild(btn_txt);
        btn.onclick = function () {
            localStorage.setItem("username", null);
            localStorage.setItem("password", null);
            location.reload();
        };
        nav_bar.append(btn);
    });
}

function init() {
    console.log("retrieving users..");
    $.ajax({
        url: "http://localhost:8080/ExTrEME-WebApp/ExTrEME/users"
    }).then(function (users) {
        console.log("found " + users.length + " users..");
        document.getElementById("users_count").textContent = users.length;

        var tbl = document.getElementById("users_table");
        var tbdy = document.createElement("tbody");
        for (var i = 0; i < users.length; i++) {
            var tr = document.createElement("tr");

            var td_email = document.createElement("td");
            td_email.appendChild(document.createTextNode(users[i].email));
            tr.appendChild(td_email);

            var td_first_name = document.createElement("td");
            td_first_name.appendChild(document.createTextNode(users[i].first_name));
            tr.appendChild(td_first_name);

            var td_last_name = document.createElement("td");
            td_last_name.appendChild(document.createTextNode(users[i].last_name));
            tr.appendChild(td_last_name);

            var td_actions = document.createElement("td");
            var actions_group = document.createElement("div");
            actions_group.className = "btn-group";
            var btn_edit = document.createElement("button");
            btn_edit.type = "button";
            btn_edit.className = "btn btn-sm";
            btn_edit.disabled = true;
            var btn_edit_icon = document.createElement("i");
            btn_edit_icon.className = "fas fa-edit";
            btn_edit.append(btn_edit_icon);
            actions_group.append(btn_edit);
            var btn_del = document.createElement("button");
            btn_del.type = "button";
            btn_del.className = "btn btn-sm";
            var btn_del_icon = document.createElement("i");
            btn_del_icon.className = "fas fa-trash";
            btn_del.append(btn_del_icon);
            var user_id = users[i].id;
            btn_del.onclick = function () {
                $.ajax({
                    url: "http://localhost:8080/ExTrEME-WebApp/ExTrEME/users/" + user_id,
                    type: "DELETE"
                }).then(function () {
                    tbdy.removeChild(tr);
                    document.getElementById("users_count").textContent = users.length;
                });
            };
            actions_group.append(btn_del);
            td_actions.append(actions_group);
            tr.appendChild(td_actions);

            tbdy.appendChild(tr);
        }
        tbl.appendChild(tbdy);
    });

    console.log("retrieving employees..");
    $.ajax({
        url: "http://localhost:8080/ExTrEME-WebApp/ExTrEME/employees"
    }).then(function (employees) {
        console.log("found " + employees.length + " employees..");
        document.getElementById("employees_count").textContent = employees.length;

        var tbl = document.getElementById("employees_table");
        var tbdy = document.createElement("tbody");
        for (var i = 0; i < employees.length; i++) {
            var tr = document.createElement("tr");

            var td_email = document.createElement("td");
            td_email.appendChild(document.createTextNode(employees[i].email));
            tr.appendChild(td_email);

            var td_first_name = document.createElement("td");
            td_first_name.appendChild(document.createTextNode(employees[i].first_name));
            tr.appendChild(td_first_name);

            var td_last_name = document.createElement("td");
            td_last_name.appendChild(document.createTextNode(employees[i].last_name));
            tr.appendChild(td_last_name);

            var td_actions = document.createElement("td");
            var actions_group = document.createElement("div");
            actions_group.className = "btn-group";
            var btn_edit = document.createElement("button");
            btn_edit.type = "button";
            btn_edit.className = "btn btn-sm";
            btn_edit.disabled = true;
            var btn_edit_icon = document.createElement("i");
            btn_edit_icon.className = "fas fa-edit";
            btn_edit.append(btn_edit_icon);
            actions_group.append(btn_edit);
            var btn_del = document.createElement("button");
            btn_del.type = "button";
            btn_del.className = "btn btn-sm";
            var btn_del_icon = document.createElement("i");
            btn_del_icon.className = "fas fa-trash";
            btn_del.append(btn_del_icon);
            var employee_id = employees[i].id;
            btn_del.onclick = function () {
                $.ajax({
                    url: "http://localhost:8080/ExTrEME-WebApp/ExTrEME/employees/" + employee_id,
                    type: "DELETE"
                }).then(function () {
                    tbdy.removeChild(tr);
                    document.getElementById("employees_count").textContent = employees.length;
                });
            };
            actions_group.append(btn_del);
            td_actions.append(actions_group);
            tr.appendChild(td_actions);

            tbdy.appendChild(tr);
        }
        tbl.appendChild(tbdy);
    });
}