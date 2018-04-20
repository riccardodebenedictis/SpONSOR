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

var nav_bar = $('<nav/>').addClass('navbar navbar-expand-lg navbar-light bg-light justify-content-between');
var login_form = $('<form/>').addClass('form-inline');
var user_info = $('<form/>').addClass('form-inline');
var user_a = $('<a/>').addClass('nav-link dropdown-toggle mx-1');
var current_user;

$(document).ready(function () {
    console.log('init ExTrEME');

    nav_bar.append($('<a/>').addClass('navbar-brand').text('ExTrEME'));

    var email_input = $('<input/>').addClass('form-control form-control-sm mx-1');
    var password_input = $('<input/>').addClass('form-control form-control-sm mx-1').attr('type', 'password');
    var signin_button = $('<button/>').addClass('btn btn-secondary btn-sm mx-1').attr('type', 'button').text('Sign in');
    signin_button.click(function () {
        console.log('signin button clicked');
        var credentials = {email: email_input.val(), password: password_input.val()};
        login(credentials);
    });
    var signup_button = $('<button/>').addClass('btn btn-secondary btn-sm mx-1').attr('type', 'button').text('Sign up');
    signup_button.click(function () {
        console.log('signup button clicked');
    });
    login_form.append(
            $('<div/>').addClass('form-group').append(email_input),
            $('<div/>').addClass('form-group').append(password_input),
            $('<div/>').addClass('form-group').append(signin_button),
            $('<div/>').addClass('form-group').append(signup_button)
            );

    var ul = $('<ul/>').addClass('navbar-nav ml-auto');
    var user_dropdown = $('<li/>').addClass('nav-item dropdown').attr('data-toggle', 'dropdown');
    user_dropdown.append(user_a);

    var user_dropdown_menu = $('<div/>').addClass('dropdown-menu');
    var signout_button = $('<a/>').addClass('dropdown-item').text('Sign out');
    signout_button.click(function () {
        console.log('signout button clicked');
        logout();
    });
    user_dropdown_menu.append(signout_button);

    user_dropdown.append(user_dropdown_menu);
    ul.append(user_dropdown);
    user_info.append(ul);
    user_info.hide();

    nav_bar.append(login_form);
    nav_bar.append(user_info);

    var email = localStorage.getItem("email");
    var password = localStorage.getItem("password");
    if (email != null & password != null) {
        var credentials = {email: email, password: password};
        login(credentials);
    }

    $('body').append(nav_bar);
});

function login(credentials) {
    console.log('logging in');
    $.ajax({
        type: 'POST',
        url: 'http://localhost:8080/ExTrEME-WebApp/ExTrEME/login',
        contentType: 'application/json',
        data: JSON.stringify(credentials)
    }).done(function (user) {
        console.log('logged in');
        login_form.hide();
        user_a.text(user.first_name);
        user_info.show();
        localStorage.setItem('email', credentials.email);
        localStorage.setItem('password', credentials.password);
        current_user = user;
    }).fail(function (jqXHR, textStatus) {
        alert(textStatus);
    });
}

function logout() {
    console.log('logging out');
    current_user = null;
    localStorage.removeItem('email');
    localStorage.removeItem('password');
    user_info.hide();
    login_form.show();
}