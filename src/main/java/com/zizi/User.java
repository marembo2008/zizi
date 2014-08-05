package com.zizi;

/**
 * Represents the details of a user in this chat applications.
 *
 * @author marembo
 */
public class User {

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

    /**
     * Override toString to return the name of the user.
     *
     * @return
     */
    @Override
    public String toString() {
        return this.name;
    }

}
