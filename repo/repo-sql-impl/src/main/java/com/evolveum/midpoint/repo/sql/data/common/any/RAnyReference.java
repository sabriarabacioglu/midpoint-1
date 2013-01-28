/*
 * Copyright (c) 2012 Evolveum
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://www.opensource.org/licenses/cddl1 or
 * CDDLv1.0.txt file in the source code distribution.
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 *
 * Portions Copyrighted 2013 [name of copyright owner]
 */

package com.evolveum.midpoint.repo.sql.data.common.any;

import com.evolveum.midpoint.repo.sql.data.common.RAnyContainer;
import com.evolveum.midpoint.repo.sql.data.common.RContainerType;
import com.evolveum.midpoint.repo.sql.data.common.id.RAnyReferenceId;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import javax.persistence.*;
import javax.xml.namespace.QName;

/**
 * todo fix primary key of this class, also name and type should probably be
 * in there, maybe more columns are necessary
 *
 * @author lazyman
 */
@Entity
@IdClass(RAnyReferenceId.class)
@Table(name = "m_any_reference")
public class RAnyReference implements RAnyValue {

    //owner entity
    private RAnyContainer anyContainer;
    private String ownerOid;
    private Long ownerId;
    private RContainerType ownerType;

    private boolean dynamic;
    private QName name;
    private QName type;
    private RValueType valueType;

    private String value;

    public RAnyReference() {
    }

    public RAnyReference(String value) {
        this.value = value;
    }

    @ForeignKey(name = "fk_any_clob")
    @MapsId("owner")
    @ManyToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumns({
            @PrimaryKeyJoinColumn(name = "anyContainer_owner_oid", referencedColumnName = "ownerOid"),
            @PrimaryKeyJoinColumn(name = "anyContainer_owner_id", referencedColumnName = "ownerId"),
            @PrimaryKeyJoinColumn(name = "anyContainer_ownertype", referencedColumnName = "ownerType")
    })
    public RAnyContainer getAnyContainer() {
        return anyContainer;
    }

    @Id
    @Column(name = "anyContainer_owner_oid", length = 36)
    public String getOwnerOid() {
        if (ownerOid == null && anyContainer != null) {
            ownerOid = anyContainer.getOwnerOid();
        }
        return ownerOid;
    }

    @Id
    @Column(name = "anyContainer_owner_id")
    public Long getOwnerId() {
        if (ownerId == null && anyContainer != null) {
            ownerId = anyContainer.getOwnerId();
        }
        return ownerId;
    }

    @Id
    @Column(name = "anyContainer_ownertype")
    public RContainerType getOwnerType() {
        if (ownerType == null && anyContainer != null) {
            ownerType = anyContainer.getOwnerType();
        }
        return ownerType;
    }

    @Columns(columns = {
            @Column(name = "name_namespace"),
            @Column(name = "name_localPart")
    })
    public QName getName() {
        return name;
    }

    @Columns(columns = {
            @Column(name = "type_namespace"),
            @Column(name = "type_localPart")
    })
    public QName getType() {
        return type;
    }

    @Enumerated(EnumType.ORDINAL)
    public RValueType getValueType() {
        return valueType;
    }

    /**
     * @return true if this property has dynamic definition
     */
    @Column(name = "dynamicDef")
    public boolean isDynamic() {
        return dynamic;
    }

    @Index(name = "iTargetOid")
    @Column(name = "targetoid")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setValueType(RValueType valueType) {
        this.valueType = valueType;
    }

    public void setName(QName name) {
        this.name = name;
    }

    public void setType(QName type) {
        this.type = type;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    public void setAnyContainer(RAnyContainer anyContainer) {
        this.anyContainer = anyContainer;
    }

    public void setOwnerOid(String ownerOid) {
        this.ownerOid = ownerOid;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public void setOwnerType(RContainerType ownerType) {
        this.ownerType = ownerType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RAnyReference that = (RAnyReference) o;

        if (dynamic != that.dynamic) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (valueType != that.valueType) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (dynamic ? 1 : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (valueType != null ? valueType.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}