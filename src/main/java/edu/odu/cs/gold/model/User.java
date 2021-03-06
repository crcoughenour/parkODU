package edu.odu.cs.gold.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class User implements Serializable{

    private String userKey;
    private String email;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String roleType;
    private String roleTypeKey;
    private boolean enabled;
    private String confirmationToken;
    private String lastNotificationViewedDate;
    private String preferredStartingAddress;
    private String preferredDestinationBuilding;
    private Integer preferredMinimumAvailableSpaces;
    private Set<String> permissions;
    private Set<String> preferredPermitTypes;
    private Set<String> preferredSpaceTypes;

    public User() { }

    public User(String email,
                String username,
                String password,
                String firstName,
                String lastName,
                String roleType,
                String roleTypeKey,
                boolean enabled,
                String confirmationToken) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roleTypeKey = roleTypeKey;
        this.roleType = roleType;
        this.enabled = enabled;
        this.confirmationToken = confirmationToken;
    }

    public void generateUserKey() {
        this.userKey = UUID.randomUUID().toString();
    }

    public void generateConfirmationToken() { this.confirmationToken = UUID.randomUUID().toString(); }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public String getConfirmationToken() {
        return confirmationToken;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean value) {
        this.enabled = value;
    }

    public String getRoleType() { return roleType; }

    public void setRoleType(String roleType) { this.roleType = roleType; }

    public String getRoleTypeKey() { return roleTypeKey; }

    public void setRoleTypeKey(String roleTypeKey) { this.roleTypeKey = roleTypeKey; }

    public String getLastNotificationViewedDate() {
        return this.lastNotificationViewedDate;
    }

    public String getPreferredStartingAddress() {
        return this.preferredStartingAddress;
    }

    public void setPreferredStartingAddress(String preferredStartingAddress) {
        this.preferredStartingAddress = preferredStartingAddress;
    }

    public String getPreferredDestinationBuilding() {
        return this.preferredDestinationBuilding;
    }

    public void setPreferredDestinationBuilding(String preferredDestinationBuilding) {
        this.preferredDestinationBuilding = preferredDestinationBuilding;
    }

    public Integer getPreferredMinimumAvailableSpaces() {
        return preferredMinimumAvailableSpaces;
    }

    public void setPreferredMinimumAvailableSpaces(Integer preferredMinimumAvailableSpaces) {
        this.preferredMinimumAvailableSpaces = preferredMinimumAvailableSpaces;
    }

    public void setLastNotificationViewedDate(String lastNotificationViewedDate) {
        this.lastNotificationViewedDate = lastNotificationViewedDate;
    }

    public Set<String> getPermissions() {
        if (permissions == null) {
            permissions = new HashSet<>();
        }
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public Set<String> getPreferredPermitTypes() {
        if(preferredPermitTypes == null) {
            preferredPermitTypes = new HashSet<>();
        }

        return preferredPermitTypes;
    }

    public void setPreferredPermitTypes(Set<String> preferredPermitTypes) {
        this.preferredPermitTypes = preferredPermitTypes;
    }

    public Set<String> getPreferredSpaceTypes() {
        if(preferredSpaceTypes == null) {
            preferredSpaceTypes = new HashSet<>();
        }

        return preferredSpaceTypes;
    }

    public void setPreferredSpaceTypes(Set<String> preferredSpaceTypes) {
        this.preferredSpaceTypes = preferredSpaceTypes;
    }

    public Set<GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (String permission : permissions) {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(permission);
            authorities.add(grantedAuthority);
        }
        return authorities;
    }

    @Override
    public String toString() {
        return "User{ " +
                "userKey='" + userKey + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", roleType='" + roleType + '\'' +
                ", roleTypeKey='" + roleTypeKey + '\'' +
                ", lastNotificationViewedDate=" + lastNotificationViewedDate +
                ", enabled=" + enabled +
                ", confirmationToken='" + confirmationToken + '\'' +
                ", permissions=" + permissions +
                ", preferredStartingAddress=" + preferredStartingAddress + '\'' +
                ", preferredDestinationBuilding=" + preferredDestinationBuilding + '\'' +
                ", preferredMinimumAvailableSpaces=" + preferredMinimumAvailableSpaces +
                ", preferredPermitTypes=" + preferredPermitTypes +
                ", preferredSpaceTypes=" + preferredSpaceTypes +
                " }";
    }
}