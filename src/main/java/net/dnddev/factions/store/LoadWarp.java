package net.dnddev.factions.store;

import org.apache.commons.codec.digest.DigestUtils;

import net.dnddev.factions.base.Faction;
import net.dnddev.factions.base.Warp;
import net.dnddev.factions.spatial.LazyLocation;

/**
 * The in-memory representation of a Warp.
 * <p>
 * To help reduce clutter and duplicate code, Warps have an additional middle step between the interface and the
 * implementation that loads information to the database.
 * </p>
 * 
 * @author Michael Ziluck
 */
public abstract class LoadWarp implements Warp
{

    protected Faction faction;

    protected String name;

    protected String stub;

    protected LazyLocation location;

    protected boolean locked;

    protected String password;

    protected boolean loaded;

    @Override
    public Faction getFaction()
    {
        assertLoaded();
        return faction;
    }

    @Override
    public String getName()
    {
        assertLoaded();
        return name;
    }

    @Override
    public String getStub()
    {
        return getName().toLowerCase();
    }

    @Override
    public LazyLocation getLocation()
    {
        assertLoaded();
        return location;
    }

    @Override
    public String getPassword()
    {
        assertLoaded();
        return password;
    }

    @Override
    public boolean hasPassword()
    {
        return getPassword() == null;
    }

    @Override
    public boolean isPassword(String check)
    {
        if (!hasPassword())
        {
            return false;
        }
        return DigestUtils.md5Hex(check).equals(getPassword());
    }

    @Override
    public void setPassword(String password)
    {
        this.password = DigestUtils.md5Hex(password);
    }

    protected void assertLoaded()
    {
        if (!loaded)
        {
            // TODO load
        }
        loaded = true;
    }

}