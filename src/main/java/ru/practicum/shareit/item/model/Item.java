package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Item {
    Integer id;
    String name;
    String description;
    Boolean isAvailable;
    int ownerId;
}
