package com.sanshengshui.server.common.data.id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sanshengshui.server.common.data.EntityType;

import java.util.UUID;

/**
 * @author james mu
 * @date 18-12-11 上午10:09
 */
public final class TenantId extends UUIDBased implements EntityId{
    public static final long serialVersionUID = 1L;

    @JsonCreator
    public TenantId(@JsonProperty("id") UUID id){
        super(id);
    }

    @JsonIgnore
    @Override
    public EntityType getEntityType() {
        return EntityType.TENANT;
    }
}
