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
    private Boolean _president;
    private Boolean _structure;
    private Boolean _brilliant;
    private Boolean _evaluator;
    private Boolean _concrete;
    private Boolean _explorer;
    private Boolean _worker;
    private Boolean _objectivist;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getPresident() {
        return _president;
    }

    public void setPresident(Boolean _president) {
        this._president = _president;
    }

    public Boolean getStructure() {
        return _structure;
    }

    public void setStructure(Boolean _structure) {
        this._structure = _structure;
    }

    public Boolean getBrilliant() {
        return _brilliant;
    }

    public void setBrilliant(Boolean _brilliant) {
        this._brilliant = _brilliant;
    }

    public Boolean getEvaluator() {
        return _evaluator;
    }

    public void setEvaluator(Boolean _evaluator) {
        this._evaluator = _evaluator;
    }

    public Boolean getConcrete() {
        return _concrete;
    }

    public void setConcrete(Boolean _concrete) {
        this._concrete = _concrete;
    }

    public Boolean getExplorer() {
        return _explorer;
    }

    public void setExplorer(Boolean _explorer) {
        this._explorer = _explorer;
    }

    public Boolean getWorker() {
        return _worker;
    }

    public void setWorker(Boolean _worker) {
        this._worker = _worker;
    }

    public Boolean getObjectivist() {
        return _objectivist;
    }

    public void setObjectivist(Boolean _objectivist) {
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
