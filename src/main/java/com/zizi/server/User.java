package com.zizi.server;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents the details of a user in this chat applications.
 */
public class User implements Serializable {

    /**
     * Unique id identifying the user.
     */
    private Long id;
    /**
     * The displayable name for the user.
     */
    private String name;

    public User() {
        //Initialize the user ID to current time millis.
        id = System.currentTimeMillis();
    }

    public User(String name) {
        this(); //Initalize new user ID.
        this.name = name;
    }

    /**
     * Returns the user ID. Once generated, cannot be set again.
     *
     * @return
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the name of the user.
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Updates the user's name.
     *
     * @param name the new name to update to.
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", name=" + name + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        return Objects.equals(this.id, other.id);
    }

}
