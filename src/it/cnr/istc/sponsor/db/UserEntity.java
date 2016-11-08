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

/**
 *
 * @author Riccardo De Benedictis <riccardo.debenedictis@istc.cnr.it>
 */
@Entity
public class UserEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String firstName;
    private String lastName;
    private int c_president;
    private int c_structure;
    private int c_brilliant;
    private int c_evaluator;
    private int c_concrete;
    private int c_explorer;
    private int c_worker;
    private int c_objectivist;
    @ManyToMany
    private final Collection<ActivityEntity> negated_activities = new ArrayList<>();
    @ManyToMany
    private final Collection<ActivityEntity> assigned_activities = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getPresident() {
        return c_president;
    }

    public void setPresident(int president) {
        this.c_president = president;
    }

    public int getStructure() {
        return c_structure;
    }

    public void setStructure(int structure) {
        this.c_structure = structure;
    }

    public int getBrilliant() {
        return c_brilliant;
    }

    public void setBrilliant(int brilliant) {
        this.c_brilliant = brilliant;
    }

    public int getEvaluator() {
        return c_evaluator;
    }

    public void setEvaluator(int evaluator) {
        this.c_evaluator = evaluator;
    }

    public int getConcrete() {
        return c_concrete;
    }

    public void setConcrete(int concrete) {
        this.c_concrete = concrete;
    }

    public int getExplorer() {
        return c_explorer;
    }

    public void setExplorer(int explorer) {
        this.c_explorer = explorer;
    }

    public int getWorker() {
        return c_worker;
    }

    public void setWorker(int worker) {
        this.c_worker = worker;
    }

    public int getObjectivist() {
        return c_objectivist;
    }

    public void setObjectivist(int objectivist) {
        this.c_objectivist = objectivist;
    }

    public void addNegatedActivity(ActivityEntity denial) {
        negated_activities.add(denial);
    }

    public void removeNegatedActivity(ActivityEntity denial) {
        negated_activities.remove(denial);
    }

    public Collection<ActivityEntity> getNegatedActivities() {
        return Collections.unmodifiableCollection(negated_activities);
    }

    public void addAssignedActivity(ActivityEntity request) {
        assigned_activities.add(request);
    }

    public void removeAssignedActivity(ActivityEntity request) {
        assigned_activities.remove(request);
    }

    public Collection<ActivityEntity> getAssignedActivities() {
        return Collections.unmodifiableCollection(assigned_activities);
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
        if (!(object instanceof UserEntity)) {
            return false;
        }
        UserEntity other = (UserEntity) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "it.cnr.istc.sponsor.db.User[ id=" + id + " ]";
    }
}
