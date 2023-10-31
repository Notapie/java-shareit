package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemJpaRepository extends JpaRepository<Item, Integer> {
    List<Item> findItemsByOwnerIdIs(int ownerId);

    @Query("from Item " +
            "where (lower(name) like lower(concat('%', :query, '%')) " +
            "or lower(description) like lower(concat('%', :query, '%'))) " +
            "and isAvailable = true")
    List<Item> searchItemsByQuery(String query);
}
