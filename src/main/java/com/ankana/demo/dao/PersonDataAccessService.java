package com.ankana.demo.dao;

import com.ankana.demo.model.Person;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository("postgres")
public class PersonDataAccessService implements PersonDao{

    public static final String HASH_KEY ="Person";

    @Autowired
    StringRedisTemplate template;

    @Autowired
    private ObjectMapper objectMapper;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PersonDataAccessService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int insertPerson(UUID id, Person person) {
        return 0;
    }

    @Override
    public List<Person> selectAllPeople() {


        final String sql = "SELECT id,name,cnt FROM person";
        return jdbcTemplate.query(sql, (resultSet, i)-> {
            UUID id = UUID.fromString(resultSet.getString("id"));
            String name = resultSet.getString("name");
            Integer cnt = resultSet.getInt("cnt");
            return new Person(id,name,cnt);
        });

    }

    @Override
    @Cacheable(value="Person",key="#id" , unless="#result.count > 2")
    public Optional<Person> selectPersonById(UUID id) {
        System.out.println("inside selectPersonById ");


        Person person;
        if(template.opsForHash().hasKey(HASH_KEY,id.toString())){
            System.out.println("FETCHED FROM REDIS");
            person = (Person) template.opsForHash().get(HASH_KEY,id);

        }else{

            final String sql = "SELECT id,name,cnt FROM person where id = ?";
            System.out.println("SQL AFTER ");
            person = jdbcTemplate.queryForObject(sql,
                    new Object[]{id},
                    (resultSet, i)-> {
                        UUID personId = UUID.fromString(resultSet.getString("id"));
                        System.out.println("CHecking UUID"+personId);

                        String name = resultSet.getString("name");
                        Integer cnt = resultSet.getInt("cnt");
                        Person newPerson = new Person(personId,name,cnt);

                        if(cnt > 1){

                            try {
                                System.out.println("INSERTED TO CACHE");
                                String jsonValue = objectMapper.writeValueAsString(newPerson);
                                template.opsForHash().put(HASH_KEY,personId.toString(),jsonValue);
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }


                        }else{
                            System.out.println("FETCHED FROM POSTGRES");
                            final String updateSql = "UPDATE person SET cnt = cnt + 1 WHERE id = ?";
                            if(jdbcTemplate.update(updateSql,id)==1){
                                System.out.println("UPDATED POSTGRES");
                            }

                        }
                        return newPerson;
                    });


        }



        return Optional.ofNullable(person);



//        redisTemplate.execute((RedisOperations<String, Product> operations) -> {
//            operations.multi();
//            // Increment the field you want to auto-increment by 1.
//            // For example, if you have a "quantity" field, you can increment it as follows:
//            operations.opsForHash().increment("product:" + productId, "quantity", 1);
//            // Retrieve the product.
//            Product product = hashOperations.get("product:" + productId, productId);
//            operations.exec();
//            return product;
//        });
    }

    @Override
    public int deletePersonById(UUID id) {
        return 0;
    }

    @Override
    public int updatePersonByID(UUID id, Person person) {
        return 0;
    }
}
