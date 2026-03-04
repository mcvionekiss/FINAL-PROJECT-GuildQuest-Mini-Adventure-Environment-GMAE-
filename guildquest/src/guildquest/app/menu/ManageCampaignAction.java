package guildquest.app.menu;

import guildquest.app.GuildQuestApp;

public class ManageCampaignAction implements MenuAction {

    @Override
    public void execute(GuildQuestApp app) {
        app.manageCampaign();
    }
}