package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.Collection;
import java.util.List;

public interface CommentJpaRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findCommentsByItem_Id(int item_id);
    List<Comment> findCommentsByItem_IdIn(Collection<Integer> itemIds);
}
