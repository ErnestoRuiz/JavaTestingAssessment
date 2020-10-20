package co.newlabs.repository.pallet;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
@AllArgsConstructor
public class PalletRepository {
    private NamedParameterJdbcTemplate template;

    public PalletEntity getPalletById(final int id) {
        String query = "select * from pallets where palletId = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        RowMapper<PalletEntity> rowMapper = new BeanPropertyRowMapper<>(PalletEntity.class);
        return template.queryForObject(query, params, rowMapper);
    }
}
