package com.nothingtothetable.survivalpool.repository;

import com.nothingtothetable.survivalpool.types.DomainPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@MybatisTest
@ContextConfiguration(classes = PlayerRepositoryTest.MyBatisConfiguration.class)
class PlayerRepositoryTest
{
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp()
    {
        jdbcTemplate.execute("DROP TABLE IF EXISTS players");
        jdbcTemplate.execute("DROP TABLE IF EXISTS users");
        jdbcTemplate.execute("""
                CREATE TABLE users (
                    id BIGINT PRIMARY KEY,
                    email VARCHAR(320) NOT NULL UNIQUE
                )
                """);
        jdbcTemplate.update("INSERT INTO users (id, email) VALUES (?, ?)", 7L, "alex@example.com");
        jdbcTemplate.execute("""
                CREATE TABLE players (
                    id BIGINT PRIMARY KEY,
                    user_id BIGINT NOT NULL UNIQUE,
                    name VARCHAR(255) NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES users (id)
                )
                """);
        jdbcTemplate.update("INSERT INTO players (id, user_id, name) VALUES (?, ?, ?)",
                42L, 7L, "Alex");
    }

    @Test
    void mapsPlayerColumns()
    {
        DomainPlayer player = playerRepository.findById(42L);

        assertEquals(42L, player.getId());
        assertEquals(7L, player.getUserId());
        assertEquals("Alex", player.getName());
    }

    @Test
    void returnsNullWhenPlayerDoesNotExist()
    {
        assertNull(playerRepository.findById(404L));
    }

    @Configuration
    @MapperScan("com.nothingtothetable.survivalpool.repository")
    static class MyBatisConfiguration
    {
    }
}
