package com.diboot.example.entity;

import lombok.Data;

import java.util.List;

@Data
public class Tree {

    private String title;

    private String key;

    private String value;

    private TreeIcon slots;

    private List<Tree> children;

}
