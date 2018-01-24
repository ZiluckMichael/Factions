package net.dnddev.factions.spatial;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Rectangle;

import net.dnddev.factions.utils.GeometryUtils;

/**
 * A two-dimensional location within a World. Similar to a LazyLocation, it allows storage of a location without
 * requiring the World to also be loaded. The major difference is that it only uses the x and z values of the location
 * in the World.
 * 
 * @author Michael Ziluck
 */
public class BlockColumn implements Rectangle
{

    private int x;
    private int z;

    private String world;

    private World parsedWorld;

    /**
     * @param x the x coordinate.
     * @param z the z coordinate.
     * @param parsedWorld the Bukkit world.
     */
    public BlockColumn(int x, int z, World parsedWorld)
    {
        this.x = x;
        this.z = z;
        this.parsedWorld = parsedWorld;
        this.world = parsedWorld.getName();
    }

    /**
     * @return the x-coordinate.
     */
    public int getX()
    {
        return x;
    }

    /**
     * @return the z-coordinate.
     */
    public int getZ()
    {
        return z;
    }

    /**
     * @return the World this BlockColumn exists in.
     */
    public World getWorld()
    {
        if (parsedWorld == null)
        {
            parsedWorld = Bukkit.getWorld(world);
        }
        return parsedWorld;
    }

    /**
     * @return all of the {@link Block Blocks} that make up this BlockColumn.
     */
    public List<Block> getBlocks()
    {
        List<Block> blocks = new ArrayList<>(257);
        for (int i = 0; i < 256; i++)
        {
            blocks.add(getWorld().getBlockAt(x, i, z));
        }
        return blocks;
    }

    @Override
    public float x1()
    {
        return x;
    }

    @Override
    public float y1()
    {
        return z;
    }

    @Override
    public float x2()
    {
        return x;
    }

    @Override
    public float y2()
    {
        return z;
    }

    @Override
    public float area()
    {
        return 0;
    }

    @Override
    public Rectangle add(Rectangle r)
    {
        return GeometryUtils.create(Math.min(x, r.x1()), Math.min(z, r.y1()), Math.max(x, r.x2()), Math.max(z, r.y2()));
    }

    @Override
    public boolean contains(double x, double y)
    {
        return this.x == x && this.z == y;
    }

    @Override
    public float intersectionArea(Rectangle r)
    {
        return 0;
    }

    @Override
    public float perimeter()
    {
        return 0;
    }

    @Override
    public double distance(Rectangle r)
    {
        return GeometryUtils.distance(x, z, x, z, r.x1(), r.y1(), r.x2(), r.y2());
    }

    @Override
    public Rectangle mbr()
    {
        return this;
    }

    @Override
    public boolean intersects(Rectangle r)
    {
        return r.x1() <= x && x <= r.x2() && r.y1() <= z && z <= r.y2();
    }

    @Override
    public Geometry geometry()
    {
        return this;
    }

}
