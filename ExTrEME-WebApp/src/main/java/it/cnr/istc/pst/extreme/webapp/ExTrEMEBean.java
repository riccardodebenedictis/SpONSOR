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

import it.cnr.istc.pst.extreme.webapp.db.UserEntity;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Riccardo De Benedictis
 */
@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class ExTrEMEBean {

    private static final Logger LOG = Logger.getLogger(ExTrEMEBean.class.getName());
    private static final Jsonb JSONB = JsonbBuilder.create();
    @PersistenceContext
    private EntityManager em;

    @PostConstruct
    private void startup() {
        LOG.info("Starting ExTrEME server..");
        UserEntity ue = new UserEntity();
        ue.setEmail("riccardo.debenedictis@istc.cnr.it");
        ue.setPassword("password001");
        ue.setFirstName("Riccardo");
        ue.setLastName("De Benedictis");
        em.persist(ue);
    }

    @PreDestroy
    private void shutdown() {
        LOG.info("Stopping ExTrEME server..");
    }
}
