package com.maxenceraux.purse_test.mapper;

import com.maxenceraux.purse_test.model.Purchase;
import com.maxenceraux.purse_test.model.PurchaseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.core.convert.converter.Converter;

@Mapper(componentModel = "spring")
public interface PurchaseDtoToPurchaseMapper extends Converter<PurchaseDTO, Purchase> {

    @Override
    @Mapping(source = "purchasedProducts", target = "purchasedProducts")
    Purchase convert(PurchaseDTO source);

}
