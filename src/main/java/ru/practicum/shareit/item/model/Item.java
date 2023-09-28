package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

@Data
@Builder(toBuilder = true)
public class Item {
    Integer id;
    String name;
    String description;
    Boolean isAvailable;
    int ownerId;
}
