package com.ziluck.factions.data.mongodb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import com.ziluck.factions.base.struct.FactionType;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.jongo.MongoCollection;

import com.ziluck.factions.base.Faction;
import com.ziluck.factions.base.User;
import com.ziluck.factions.base.UserStore;
import com.ziluck.factions.configuration.Config;
import com.ziluck.factions.configuration.struct.Optimization;
import com.ziluck.factions.data.LoadFactionStore;
import com.ziluck.factions.events.FactionCreateEvent;

/**
 * Faction implementation for processing Factions from MongoDB.
 *
 * @author Michael Ziluck
 */
public class MongoFactionStore extends LoadFactionStore
{

    /**
     * Used if the system optimizes to reduce processing power.
     */
    private HashMap<String, Faction> factionsByName;

    /**
     * Used if the system optimizes to reduce memory usage.
     */
    private List<Faction> factionsList;

    private MongoCollection store;

    /**
     * Construct a new MongoFactionStore. This will grab the information from the config file.
     */
    public MongoFactionStore()
    {
        super();

        store = MongoWrapper.getInstance().getJongo().getCollection("factions");

        int count = Math.toIntExact(store.count() + 5);

        if (Config.OPTIMIZATION.getValue() == Optimization.MEMORY)
        {
            factionsList = new ArrayList<>(count);
        }
        else if (Config.OPTIMIZATION.getValue() == Optimization.PROCESS)
        {
            factionsByName = new HashMap<>(count);
        }

        loadFactions();
    }

    @Override
    public Faction getFaction(long id)
    {
        Faction            faction   = null;
        Predicate<Faction> predicate = f -> f.getId() == id;
        if (Config.OPTIMIZATION.getValue() == Optimization.MEMORY)
        {
            faction = searchList(predicate);
        }
        else if (Config.OPTIMIZATION.getValue() == Optimization.PROCESS)
        {
            faction = searchMap(predicate);
        }
        return faction;
    }

    @Override
    public Faction getFaction(final String name)
    {
        Validate.notNull(name, "Name can't be null.");

        Faction faction = null;
        if (Config.OPTIMIZATION.getValue() == Optimization.MEMORY)
        {
            faction = searchList(f -> f.getStub().equals(name.toLowerCase()));
        }
        else if (Config.OPTIMIZATION.getValue() == Optimization.PROCESS)
        {
            faction = factionsByName.get(name.toLowerCase());
        }
        return faction;
    }

    @Override
    public Faction getCasedFaction(String name)
    {
        Validate.notNull(name, "Name can't be null.");

        Faction faction = null;
        if (Config.OPTIMIZATION.getValue() == Optimization.MEMORY)
        {
            faction = searchList(f -> f.getStub().equals(name.toLowerCase()));
        }
        else if (Config.OPTIMIZATION.getValue() == Optimization.PROCESS)
        {
            faction = factionsByName.get(name.toLowerCase());
            if (faction != null && !faction.getName().equals(name))
            {
                faction = null;
            }
        }
        return faction;
    }

    @Override
    public Faction getFaction(UUID uuid)
    {
        Validate.notNull(uuid, "Uuid can't be null.");

        User user = UserStore.getInstance().getUser(uuid);
        if (user == null)
        {
            return null;
        }
        return user.getFaction();
    }

    @Override
    public Faction getFaction(User user)
    {
        Validate.notNull(user, "User can't be null.");

        return user.getFaction();
    }

    @Override
    public Faction getFaction(Player player)
    {
        Validate.notNull(player, "Player can't be null.");

        return UserStore.getInstance().getUser(player.getUniqueId(), true).getFaction();
    }

    @Override
    public Collection<Faction> getFactions()
    {
        if (Config.OPTIMIZATION.getValue() == Optimization.MEMORY)
        {
            return Collections.unmodifiableCollection(factionsList);
        }
        else if (Config.OPTIMIZATION.getValue() == Optimization.PROCESS)
        {
            return Collections.unmodifiableCollection(factionsByName.values());
        }
        return null;
    }

    // TODO add javadoc
    private Faction searchList(Predicate<Faction> predicate)
    {
        for (Faction faction : factionsList)
        {
            if (predicate.test(faction))
            {
                return faction;
            }
        }
        return null;
    }

    // TODO add javadoc
    private Faction searchMap(Predicate<Faction> predicate)
    {
        for (Faction faction : factionsByName.values())
        {
            if (predicate.test(faction))
            {
                return faction;
            }
        }
        return null;
    }

    @Override
    public void loadFactions()
    {
        for (MongoFaction faction : store.find().as(MongoFaction.class))
        {
            if (faction.getId() == -1)
            {
                wilderness = faction;
            }
            if (Config.OPTIMIZATION.getValue() == Optimization.PROCESS)
            {
                factionsByName.put(faction.getStub(), faction);
            }
            else
            {
                factionsList.add(faction);
            }
            faction.loadLeader();
            // TODO load claims
        }
        if (wilderness == null)
        {
            wilderness = new MongoFaction(-1, "Wilderness", UserStore.getInstance().getConsole(), FactionType.WILDERNESS);
            wilderness.save();
        }

        MongoUser user = store.findOne().orderBy("{_id: -1}").as(MongoUser.class);
        if (user == null)
        {
            nextId = 0;
        }
        else
        {
            nextId = user.getId() + 1;
        }
    }

    @Override
    public void save(Faction faction)
    {
        Validate.notNull(faction, "Faction can't be null.");
        store.save(faction);
    }

    @Override
    public FactionCreateEvent createFaction(User creator, String name, FactionType type)
    {
        Validate.notNull(creator, "Creator can't be null.");
        Validate.notNull(name, "Name can't be null.");
        Validate.notNull(type, "Type can't be null.");

        MongoFaction faction = new MongoFaction(nextId, name, creator, type);

        return new FactionCreateEvent(faction, creator, Config.CREATE_COST.doubleValue());
    }

}
