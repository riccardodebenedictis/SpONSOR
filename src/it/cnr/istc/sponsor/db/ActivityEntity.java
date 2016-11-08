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
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;

/**
 *
 * @author Riccardo De Benedictis <riccardo.debenedictis@istc.cnr.it>
 */
@Entity
public class ActivityEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date startTime;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date endTime;
    @OneToMany(mappedBy = "activity")
    private final Collection<ProfileSchema> schemas = new ArrayList<>();
    @ManyToMany(mappedBy = "negated_activities")
    private final Collection<UserEntity> negated_users = new ArrayList<>();
    @ManyToMany(mappedBy = "assigned_activities")
    private final Collection<UserEntity> assigned_users = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public void addProfileSchema(ProfileSchema schema) {
        schemas.add(schema);
    }

    public void removeProfileSchema(ProfileSchema schema) {
        schemas.remove(schema);
    }

    public Collection<ProfileSchema> getSchemas() {
        return Collections.unmodifiableCollection(schemas);
    }

    public void addNegatedUser(UserEntity denial) {
        negated_users.add(denial);
    }

    public void removeNegatedUser(UserEntity denial) {
        negated_users.remove(denial);
    }

    public Collection<UserEntity> getNegatedUsers() {
        return Collections.unmodifiableCollection(negated_users);
    }

    public void addAssignedUser(UserEntity request) {
        assigned_users.add(request);
    }

    public void removeAssignedUser(UserEntity request) {
        assigned_users.remove(request);
    }

    public Collection<UserEntity> getAssignedUsers() {
        return Collections.unmodifiableCollection(assigned_users);
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
        if (!(object instanceof ActivityEntity)) {
            return false;
        }
        ActivityEntity other = (ActivityEntity) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "it.cnr.istc.sponsor.db.Activity[ id=" + id + " ]";
    }
}
