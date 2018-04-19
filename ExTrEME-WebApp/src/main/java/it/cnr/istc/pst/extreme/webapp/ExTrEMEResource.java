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
package it.cnr.istc.pst.extreme.webapp;

import it.cnr.istc.pst.extreme.api.Credentials;
import it.cnr.istc.pst.extreme.api.Employee;
import it.cnr.istc.pst.extreme.api.User;
import it.cnr.istc.pst.extreme.webapp.db.EmployeeEntity;
import it.cnr.istc.pst.extreme.webapp.db.UserEntity;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service.
 *
 * @author Riccardo De Benedictis
 */
@Path("/")
public class ExTrEMEResource {

    private static final Logger LOG = Logger.getLogger(ExTrEMEResource.class.getName());
    private static final Jsonb JSONB = JsonbBuilder.create();
    @PersistenceContext
    private EntityManager em;

    @GET
    @Path("users")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<User> getUsers() {
        List<UserEntity> users = em.createQuery("SELECT u FROM UserEntity u", UserEntity.class).getResultList();
        return users.stream().map(u -> new User(u.getId(), u.getEmail(), u.getFirstName(), u.getLastName())).collect(Collectors.toList());
    }

    @GET
    @Path("employees")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Employee> getEmployees() {
        List<EmployeeEntity> employees = em.createQuery("SELECT e FROM EmployeeEntity e", EmployeeEntity.class).getResultList();
        return employees.stream().map(e -> new Employee(e.getId(), e.getEmail(), e.getFirstName(), e.getLastName(), JSONB.fromJson(e.getParameters(), new HashMap<String, String>() {
        }.getClass().getGenericSuperclass()))).collect(Collectors.toList());
    }

    @POST
    @Path("login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public User login(Credentials credentials) {
        LOG.info("Logging in..");
        try {
            TypedQuery<UserEntity> query = em.createQuery("SELECT u FROM UserEntity u WHERE u.email = :email AND u.password = :password", UserEntity.class);
            query.setParameter("email", credentials.email);
            query.setParameter("password", credentials.password);
            UserEntity u = query.getSingleResult();
            return new User(u.getId(), u.getEmail(), u.getFirstName(), u.getLastName());
        } catch (NoResultException e) {
            throw new WebApplicationException(e.getLocalizedMessage(), Response.Status.UNAUTHORIZED);
        }
    }
}
