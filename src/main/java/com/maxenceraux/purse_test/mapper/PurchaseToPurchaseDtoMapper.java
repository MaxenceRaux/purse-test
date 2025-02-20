package com.maxenceraux.purse_test.mapper;

import com.maxenceraux.purse_test.model.Purchase;
import com.maxenceraux.purse_test.model.PurchaseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.core.convert.converter.Converter;

@Mapper(componentModel = "spring")
public interface PurchaseToPurchaseDtoMapper extends Converter<Purchase, PurchaseDTO> {

    @Override
    PurchaseDTO convert(Purchase source);

}
