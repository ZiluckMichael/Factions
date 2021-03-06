package com.ziluck.factions.base.claims;

import java.util.Collection;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.block.Block;

import com.ziluck.factions.base.Faction;
import com.ziluck.factions.base.Purchasable;
import com.ziluck.factions.base.User;
import com.ziluck.factions.spatial.LazyLocation;

/**
 * Used to represent an area claimed by a {@link Faction}.
 * <p>
 * Claims exist from the bottom to the top of the world. {@link Claim2D} are individually used depending on the settings
 * in the configuration.
 * </p>
 * <p>
 * Additionally, Claims are able to be owned by one or more {@link User Users}. Additional users can be added to Claims
 * by Faction officers.
 * </p>
 * 
 * @author Michael Ziluck
 */
public interface Claim extends Purchasable
{

    /**
     * Claims only exist if they are owned by a Faction. If an area is the Wilderness, it does not have a set Faction
     * and therefore it can't be a claim.
     * 
     * @return the faction this claim belongs to.
     */
    Faction getFaction();

    /**
     * Utility method to get a {@link Collection} of all {@link User Users} that are currently physically inside this
     * claim.
     * 
     * @return all {@link User Users} within the claim.
     */
    Collection<User> getWithin();

    /**
     * All users who have ownership of the Claim. If the Server does not have the option to allow owners of a Claim then
     * this method will always return null.
     * 
     * @return the {@link Claim} owners.
     */
    Collection<User> getOwners();

    /**
     * Checks whether or not the given {@link User} is within this Claim. If the {@link User} is offline this method
     * will always return false.
     * 
     * @param user the User to check.
     * @return {@code true} if the User is within the Claim.
     */
    boolean isWithin(User user);

    /**
     * Checks whether or not the given {@link Location} is within this Claim.
     * 
     * @param location the Location to check.
     * @return {@code true} if the Location is within the Claim.
     */
    boolean isWithin(Location location);

    /**
     * Get the Blocks that make up the walls of this Claim. It also includes the floor and roof.
     * 
     * @return the Blocks of the walls.
     */
    Set<Block> getWalls();

    /**
     * Get the geometric center of the Claim. Important to note that this will most likely not be a whole number as it
     * gets the exact number of the center.
     * 
     * @return the center of the Claim.
     */
    LazyLocation getCenter();

}
