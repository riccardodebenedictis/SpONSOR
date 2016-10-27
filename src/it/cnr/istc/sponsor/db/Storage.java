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
package it.cnr.istc.sponsor.db;

import java.util.Collection;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Riccardo De Benedictis <riccardo.debenedictis@istc.cnr.it>
 */
public class Storage {

    private static Storage _instance;
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("SpONSOR_PU");
    private final EntityManager em = emf.createEntityManager();

    public Collection<UserEntity> getAllUsers() {
        return em.createQuery("SELECT u FROM UserEntity u", UserEntity.class).getResultList();
    }

    public Collection<ActivityEntity> getAllActivities() {
        return em.createQuery("SELECT a FROM ActivityEntity a", ActivityEntity.class).getResultList();
    }

    private Storage() {
    }

    public static Storage getInstance() {
        if (_instance == null) {
            _instance = new Storage();
        }
        return _instance;
    }
}