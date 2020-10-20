package co.newlabs.repository.item;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@AllArgsConstructor
public class ItemRepository {
    private NamedParameterJdbcTemplate template;

    public void saveItem(ItemEntity entity) {
        String query = "insert into items (palletId, weight, product) values (:palletId, :weight, :product)";
        Map<String, Object> params = new HashMap<>();
        params.put("palletId", entity.getPalletId());
        params.put("weight", entity.getWeight());
        params.put("product", entity.getProduct());

        template.update(query, params);
    }

    public void removeItem(int itemId) {
        String query = "delete from items where itemId = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", itemId);

        template.update(query, params);
    }

    public List<ItemEntity> getItemsByPalletId(int palletId) {
        String query = "select * from items where palletId = :palletId";
        Map<String, Object> params = new HashMap<>();
        params.put("palletId", palletId);

        RowMapper<ItemEntity> rowMapper = new BeanPropertyRowMapper<>(ItemEntity.class);

        return template.query(query, params, rowMapper);
    }
}
