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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

/**
 *
 * @author Riccardo De Benedictis <riccardo.debenedictis@istc.cnr.it>
 */
@Entity
public class ProfileSchema implements Serializable {

    private static final long serialVersionUID = 1L;
    @ManyToOne
    private ActivityEntity activity;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private boolean c_president;
    private boolean c_structure;
    private boolean c_brilliant;
    private boolean c_evaluator;
    private boolean c_concrete;
    private boolean c_explorer;
    private boolean c_worker;
    private boolean c_objectivist;
    @ManyToMany(mappedBy = "denials")
    private final Collection<UserEntity> denials = new ArrayList<>();
    @ManyToMany(mappedBy = "requests")
    private final Collection<UserEntity> requests = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ActivityEntity getActivity() {
        return activity;
    }

    public void setActivity(ActivityEntity activity) {
        this.activity = activity;
    }

    public boolean isPresident() {
        return c_president;
    }

    public void setPresident(boolean president) {
        this.c_president = president;
    }

    public boolean isStructure() {
        return c_structure;
    }

    public void setStructure(boolean structure) {
        this.c_structure = structure;
    }

    public boolean isBrilliant() {
        return c_brilliant;
    }

    public void setBrilliant(boolean brilliant) {
        this.c_brilliant = brilliant;
    }

    public boolean isEvaluator() {
        return c_evaluator;
    }

    public void setEvaluator(boolean evaluator) {
        this.c_evaluator = evaluator;
    }

    public boolean isConcrete() {
        return c_concrete;
    }

    public void setConcrete(boolean concrete) {
        this.c_concrete = concrete;
    }

    public boolean isExplorer() {
        return c_explorer;
    }

    public void setExplorer(boolean explorer) {
        this.c_explorer = explorer;
    }

    public boolean isWorker() {
        return c_worker;
    }

    public void setWorker(boolean worker) {
        this.c_worker = worker;
    }

    public boolean isObjectivist() {
        return c_objectivist;
    }

    public void setObjectivist(boolean objectivist) {
        this.c_objectivist = objectivist;
    }

    public void addDenial(UserEntity denial) {
        denials.add(denial);
    }

    public void removeDenial(UserEntity denial) {
        denials.remove(denial);
    }

    public Collection<UserEntity> getDenials() {
        return Collections.unmodifiableCollection(denials);
    }

    public void addProfileSchema(UserEntity request) {
        requests.add(request);
    }

    public void removeProfileSchema(UserEntity request) {
        requests.remove(request);
    }

    public Collection<UserEntity> getRequests() {
        return Collections.unmodifiableCollection(requests);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProfileSchema)) {
            return false;
        }
        ProfileSchema other = (ProfileSchema) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "it.cnr.istc.sponsor.db.ProfileSchema[ id=" + id + " ]";
    }
}
