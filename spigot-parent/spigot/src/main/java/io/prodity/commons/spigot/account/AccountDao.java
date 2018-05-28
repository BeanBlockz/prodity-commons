package io.prodity.commons.spigot.account;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RegisterConstructorMapper(PlayerReference.class)
public interface AccountDao {
    @SqlUpdate(
            "INSERT INTO name_cache(player_id, player_name, last_seen) VALUES(:id, :name, :lastJoin) " +
            "ON DUPLICATE KEY UPDATE player_name = :name"
    )
    void updateCache(@BindBean PlayerReference reference);

    @SqlQuery("SELECT * FROM name_cache WHERE player_id = :id")
    Optional<PlayerReference> getPlayer(UUID id);

    @SqlQuery("SELECT * FROM name_cache WHERE player_name = :name ORDER BY last_seen DESC LIMIT 1")
    Optional<PlayerReference> getPlayer(String name);

    /**
     * Retrieves all name pairs with the given name other than
     * the most recently seen.  This is useful for looking up
     * players that we need to retrieve a new name for.
     *
     * @param name the name to look up
     * @return possible empty list of all duplicates
     */
    @SqlQuery("SELECT * FROM name_cache WHERE player_name = :name ORDER BY last_seen DESC LIMIT 1,10000")
    List<PlayerReference> getDuplicates(String name);
}