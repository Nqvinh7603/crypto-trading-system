package com.nqvinh.cryptotradingsystem.mapper;

import com.nqvinh.cryptotradingsystem.domain.Wallet;
import com.nqvinh.cryptotradingsystem.dto.response.WalletBalanceResponse.BalanceItem;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WalletMapper {

    BalanceItem toBalanceItem(Wallet wallet);
}
