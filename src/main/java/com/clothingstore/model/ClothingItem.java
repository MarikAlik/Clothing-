package com.clothingstore.model;

public class ClothingItem {
    
    private Long id;
    private String name;
    private String size;
    private double price;

    public ClothingItem(Long id, String name, String size, double price) {

        this.id = id;
        this.name = name;
        this.size = size;
        this.price = price;
    }


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public double getPrice() {
        return price;
    }
}