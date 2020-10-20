package co.newlabs.configuration;

import co.newlabs.dto.ItemDTO;
import co.newlabs.dto.PalletDTO;
import co.newlabs.repository.item.ItemEntity;
import co.newlabs.repository.pallet.PalletEntity;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MappingConfiguration {
    @Bean
    public MapperFactory mapperFactory() {
        DefaultMapperFactory mapperFactory = new DefaultMapperFactory.Builder()
                .dumpStateOnException(false)
                .useBuiltinConverters(true)
                .build();

        mapperFactory.classMap(PalletDTO.class, PalletEntity.class)
                .mapNulls(true)
                .byDefault()
                .register();

        mapperFactory.classMap(ItemDTO.class, ItemEntity.class)
                .mapNulls(true)
                .byDefault()
                .register();

        return mapperFactory;
    }

    @Bean
    public MapperFacade mapper(MapperFactory mapperFactory) {
        return mapperFactory.getMapperFacade();
    }
}
