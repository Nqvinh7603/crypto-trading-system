package com.nqvinh.cryptotradingsystem.mapper;

import com.nqvinh.cryptotradingsystem.domain.Trade;
import com.nqvinh.cryptotradingsystem.dto.response.TradeHistoryResponse.TradeItem;
import com.nqvinh.cryptotradingsystem.dto.response.TradeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TradeMapper {

    TradeResponse toResponse(Trade trade);

    TradeItem toTradeItem(Trade trade);
}
