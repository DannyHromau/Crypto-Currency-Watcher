package com.dannyhromau.watcher.mapper;


import com.dannyhromau.watcher.api.external.ExternalCoinDto;
import com.dannyhromau.watcher.model.Coin;
import com.dannyhromau.watcher.service.WatcherService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = WatcherService.class)
public interface CoinMapper {

   CoinMapper INSTANCE = Mappers.getMapper(CoinMapper.class);

   @Mapping(target = "name", source = "name")
   Coin toCoin(ExternalCoinDto dto);
   void updateCoinFromDto(ExternalCoinDto dto, @MappingTarget Coin coin);
}
