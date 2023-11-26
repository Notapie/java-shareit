package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;
import java.util.List;

public interface ItemRequestJpaRepository extends JpaRepository<ItemRequest, Integer> {
    Collection<ItemRequest> findItemRequestsByOwner_Id(int ownerId);

    @Query("from ItemRequest where id >= :fromIndex")
    List<ItemRequest> findItemRequestsFromIndex(int fromIndex, Pageable pageable);
}
