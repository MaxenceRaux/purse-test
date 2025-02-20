package com.maxenceraux.purse_test.config;

import com.maxenceraux.purse_test.mapper.PurchaseDtoToPurchaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;

import java.util.List;

@Configuration
public class ConversionServiceConfig {

    @Autowired
    public ConversionService addCustomMappers(
            ConversionService conversionService,
            List<Converter> converters
    ) {
        converters.forEach(converter -> ((DefaultFormattingConversionService) conversionService).addConverter(converter));
        return conversionService;
    }
}
