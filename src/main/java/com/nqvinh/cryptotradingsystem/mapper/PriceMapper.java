package com.nqvinh.cryptotradingsystem.mapper;

import com.nqvinh.cryptotradingsystem.domain.AggregatedPrice;
import com.nqvinh.cryptotradingsystem.dto.PriceDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PriceMapper {

    PriceDto toDto(AggregatedPrice source);
}
