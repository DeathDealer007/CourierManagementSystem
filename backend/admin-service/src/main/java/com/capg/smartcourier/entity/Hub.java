package com.capg.smartcourier.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Hub {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Hub name is required")
    @Size(min = 3, max = 100, message = "Hub name must be between 3 and 100 characters")
    private String name;

    @NotBlank(message = "Location is required")
    @Size(min = 3, max = 100, message = "Location must be between 3 and 100 characters")
    private String location;

    @NotBlank(message = "Address is required")
    @Size(min = 5, max = 200, message = "Address must be between 5 and 200 characters")
    private String address;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^[\\d\\s\\-\\+\\(\\)]{10,20}$", message = "Contact number must be valid (10-20 characters)")
    private String contactNumber;

    @NotBlank(message = "Manager name is required")
    @Size(min = 3, max = 100, message = "Manager name must be between 3 and 100 characters")
    private String manager;

    private boolean active;

    public Hub() {}

    public Hub(String name, String location, String address, String contactNumber, String manager) {
        this.name = name;
        this.location = location;
        this.address = address;
        this.contactNumber = contactNumber;
        this.manager = manager;
        this.active = true;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getManager() { return manager; }
    public void setManager(String manager) { this.manager = manager; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}