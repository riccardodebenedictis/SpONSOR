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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
    private String _first_name;
    private String _last_name;
    private Integer _president;
    private Integer _structure;
    private Integer _brilliant;
    private Integer _evaluator;
    private Integer _concrete;
    private Integer _explorer;
    private Integer _worker;
    private Integer _objectivist;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return _first_name;
    }

    public void setFirstName(String _first_name) {
        this._first_name = _first_name;
    }

    public String getLastName() {
        return _last_name;
    }

    public void setLastName(String _last_name) {
        this._last_name = _last_name;
    }

    public Integer getPresident() {
        return _president;
    }

    public void setPresident(Integer _president) {
        this._president = _president;
    }

    public Integer getStructure() {
        return _structure;
    }

    public void setStructure(Integer _structure) {
        this._structure = _structure;
    }

    public Integer getBrilliant() {
        return _brilliant;
    }

    public void setBrilliant(Integer _brilliant) {
        this._brilliant = _brilliant;
    }

    public Integer getEvaluator() {
        return _evaluator;
    }

    public void setEvaluator(Integer _evaluator) {
        this._evaluator = _evaluator;
    }

    public Integer getConcrete() {
        return _concrete;
    }

    public void setConcrete(Integer _concrete) {
        this._concrete = _concrete;
    }

    public Integer getExplorer() {
        return _explorer;
    }

    public void setExplorer(Integer _explorer) {
        this._explorer = _explorer;
    }

    public Integer getWorker() {
        return _worker;
    }

    public void setWorker(Integer _worker) {
        this._worker = _worker;
    }

    public Integer getObjectivist() {
        return _objectivist;
    }

    public void setObjectivist(Integer _objectivist) {
        this._objectivist = _objectivist;
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
