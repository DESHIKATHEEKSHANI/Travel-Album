package edu.icet.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackingList {
    private List<String> essentials = new ArrayList<>();
    private List<String> clothing = new ArrayList<>();
}
