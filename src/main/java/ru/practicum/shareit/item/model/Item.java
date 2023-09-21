package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

@Data
@Builder(toBuilder = true)
public class Item {
    Integer id;
    User owner;
    String name;
    String description;
}
