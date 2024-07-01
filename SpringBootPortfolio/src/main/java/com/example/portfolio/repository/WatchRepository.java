package com.example.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.portfolio.model.po.Watch;

@Repository(value = "watchRepository")
public interface WatchRepository extends JpaRepository<Watch, Integer>{

}