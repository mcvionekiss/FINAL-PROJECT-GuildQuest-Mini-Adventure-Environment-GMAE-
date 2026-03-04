package guildquest.app.factory;

import guildquest.enums.RarityType;
import guildquest.enums.VisibilityType;
import guildquest.model.*;
import guildquest.model.Character;

public class GameObjectFactory {

    public static Campaign createCampaign(GuildQuest system,
                                          User owner,
                                          String name,
                                          VisibilityType visibility) {

        return system.createCampaign(owner, name, visibility);
    }

    public static QuestEvent createQuestEvent(String title,
                                              WorldTime start,
                                              WorldTime end,
                                              Realm realm,
                                              VisibilityType visibility) {

        return new QuestEvent(title, start, end, realm, visibility);
    }

    public static Item createItem(String name,
                                  RarityType rarity,
                                  String description) {

        return new Item(name, rarity, description);
    }

    public static Character createCharacter(User user,
                                            String name,
                                            String clazz,
                                            int level,
                                            int capacity) {

        return user.addCharacter(name, clazz, level, capacity);
    }
}