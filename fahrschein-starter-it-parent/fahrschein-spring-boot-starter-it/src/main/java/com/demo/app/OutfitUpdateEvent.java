package com.demo.app;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.zalando.fahrschein.domain.AbstractDataChangeEvent;
import org.zalando.fahrschein.domain.DataOperation;
import org.zalando.fahrschein.domain.Metadata;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@ToString
public class OutfitUpdateEvent extends AbstractDataChangeEvent<OutfitId> {

  public static final String NAME = "outfit.outfit-update";
  private static final String DATA_TYPE = "outfit:outfit-update";

  @JsonCreator
  public OutfitUpdateEvent(@JsonProperty("metadata") final Metadata metadata,
      @JsonProperty("data_op") final DataOperation dataOp,
      @JsonProperty("data") final OutfitId data) {
    super(metadata, DATA_TYPE, dataOp, data);
  }

  public static OutfitUpdateEvent buildEvent(final DataOperation dataOperation, final OutfitId payload,
      final String flowId) {

    final Metadata metadata = new Metadata(NAME, UUID.randomUUID().toString(), OffsetDateTime.now(),
        null,
        flowId);

    return new OutfitUpdateEvent(metadata, dataOperation, payload);
  }
}
