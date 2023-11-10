package com.ankana.demo.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import jdk.jfr.DataAmount;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.UUID;
//import javax.validation.constraints.NotBlank;


@RedisHash("Person")
public class Person implements Serializable {

//    private static final long serialVersionUID = 1L;
    @Id
    private final UUID id;


    private final String name;
    private  Integer count=0;

    public Integer getCount() {
        return count;
    }

    public Person(@JsonProperty("id") UUID id, @JsonProperty("name") String name, @JsonProperty("count") Integer cnt ){
        this.id = id;
        this.name= name;
        this.count=cnt;
    }

    public UUID getId(){
        return id;
    }

    public String getName(){
        return name;
    }

//    @Override
//    public String toString() {
////        return String.format("Person{id= %id, name='%name', count=%count}", id, name, count);
//        return "Person{" +
//                "id=" + id +
//                ", name='" + name + '\'' +
//                ", count=" + count +
//                '}';
//    }
}
