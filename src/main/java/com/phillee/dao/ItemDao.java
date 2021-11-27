package com.phillee.dao;

import com.phillee.pojo.Item;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author: PhilLee
 * @Description:
 * @Date: 2021/11/26 22:52
 */
public interface ItemDao extends JpaRepository<Item, Long> {
}
